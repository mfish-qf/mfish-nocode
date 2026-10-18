package cn.com.mfish.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.parser.NacosDataParserHandler;
import com.alibaba.cloud.nacos.refresh.NacosConfigRefreshEvent;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: mfish
 * @description: 网关路由动态刷新（Nacos 配置变更 → 无须重启）
 * @date: 2026/10/15
 *
 * 背景：网关路由(spring.cloud.gateway.routes)由 Nacos 配置下发。改完配置若不重建路由，
 * 请求不会被任何路由命中，会回落到 WebFlux 静态资源处理器
 * (ResourceWebHandler → NoResourceFoundException)，再被统一异常处理兜成 500「系统异常」，
 * 表现就是"改完 Nacos 配置必须重启网关"。
 *
 * 为什么单纯 publish RefreshRoutesEvent 没效果（实测 dataId=mf-gateway-dev.yml）：
 * 1) RefreshRoutesEvent 只让 CachingRouteLocator 清缓存并按 GatewayProperties 重建，
 *    数据源 GatewayProperties 不变 → 重建出来还是旧路由（实测一直是启动时的 10 条）；
 * 2) 用当前 Environment 显式 Binder 重绑定时 isBound=false，说明 Spring Cloud 的整条刷新链路
 *    (ConfigData 重拉 → EnvironmentChangeEvent → @ConfigurationProperties 重绑定)
 *    并没有把网关配置中继进 GatewayProperties。
 *
 * ★ 真正的根因（2026-10-18 离线实测确认）：Spring Cloud Gateway 5.0(Spring Cloud 2025.x)
 *   把 GatewayProperties 的绑定前缀从 spring.cloud.gateway 改成了 spring.cloud.gateway.server.webflux。
 *   本项目 Nacos 配置里写的正是 spring.cloud.gateway.server.webflux.routes。
 *   之前监听器里写死旧前缀 spring.cloud.gateway，Binder 直接返回 null(0 条路由)，
 *   于是每次热更新都在"未解析到路由"分支静默退出——监听器从头到尾一次都没生效过。
 *   离线验证结果：
 *     前缀 spring.cloud.gateway                → 绑定结果 null，路由 0 条
 *     前缀 spring.cloud.gateway.server.webflux → 绑定成功，路由 10 条(oauth2/test/sys/demo/storage/
 *                                                scheduler/nocode/workflow/ai/project)
 *
 * 方案（不再依赖刷新链路）：监听到 Nacos 变更后，
 * 1) 直接用 ConfigService 拉取该 dataId 的最新内容；
 * 2) 【关键】配置变更瞬间 Nacos 客户端本地快照(blob)常处于"半写"状态，getConfig 会优先读缓存，
 *    实测会返回被截断的内容。因此必须做「多次拉取 + 内容稳定校验 + 条目完整性校验」，
 *    宁可多等几百毫秒，也绝不能把一份残缺配置写进 GatewayProperties
 *    （这正是历史上"改完配置全站 500"的成因：完整路由被残缺路由替换，大量路径不命中）；
 * 3) 按 dataId 后缀(yml/yaml/properties/json)解析成临时 PropertySource；
 * 4) 分两阶段用 Binder 绑到临时 GatewayProperties 上做校验：
 *    先只绑最新内容（保证"删掉某条路由"能真正删掉，不会被旧环境兜底补齐），
 *    绑不到再叠加当前环境重试一次（兼容配置里使用 ${...} 占位符的场景）；
 * 5) 解析出有效路由才写入真实 bean，再发布 RefreshRoutesEvent 让 CachingRouteLocator 重建，
 *    并统计前后路由数。
 *
 * 日志为什么全用 ERROR：Nacos 配置变更会触发 Spring Cloud 的 LoggingRebinder 重新初始化日志系统，
 * 之后 info.log 的 INFO/WARN 不再落盘；而 error.log 只收 ERROR。
 * 热更新过程恰好发生在刷新之后，用 ERROR 是唯一能稳定看到诊断信息的方式。
 */
@Slf4j
@Component
public class GatewayRouteRefreshListener {

    /**
     * 必须用 GatewayProperties 自带的 PREFIX 常量，不能写死 "spring.cloud.gateway"：
     * Spring Cloud Gateway 5.0(Spring Cloud 2025.x) 已把绑定前缀改成
     * {@code spring.cloud.gateway.server.webflux}（见 GatewayProperties.PREFIX）。
     * 之前写死旧前缀导致 Binder 永远绑不到 routes，热更新静默失效——这正是"加了监听器也没效果"的根因。
     */
    private static final String GATEWAY_PREFIX = GatewayProperties.PREFIX;

    /** 最多拉取次数：等不到"稳定内容"就退化为采用最后一次可解析的版本 */
    private static final int MAX_ATTEMPTS = 6;
    /** 起始退避间隔(ms)，逐次翻倍，上限 1500ms */
    private static final long BASE_BACKOFF = 200L;
    private static final long MAX_BACKOFF = 1500L;

    private final ApplicationEventPublisher eventPublisher;
    private final ConfigurableEnvironment environment;
    private final GatewayProperties gatewayProperties;
    private final RouteLocator routeLocator;
    private final NacosConfigManager nacosConfigManager;

    /** 独立的守护线程：不占用 reactor/Nacos 线程池，避免热更新被调度延迟 */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor((r) -> {
        Thread t = new Thread(r, "gateway-route-refresh");
        t.setDaemon(true);
        return t;
    });

    public GatewayRouteRefreshListener(ApplicationEventPublisher eventPublisher,
                                       ConfigurableEnvironment environment,
                                       GatewayProperties gatewayProperties,
                                       RouteLocator routeLocator,
                                       NacosConfigManager nacosConfigManager) {
        this.eventPublisher = eventPublisher;
        this.environment = environment;
        this.gatewayProperties = gatewayProperties;
        this.routeLocator = routeLocator;
        this.nacosConfigManager = nacosConfigManager;
        log.error("[网关路由热更新] 监听器已实例化，当前路由定义 {} 条", sizeOf(gatewayProperties.getRoutes()));
    }

    /** 启动完成后记录初始路由快照，作为异常配置时的兜底 */
    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            List<RouteDefinition> defs = gatewayProperties.getRoutes();
            log.error("[网关路由热更新] 初始化完成，当前路由定义 {} 条，实际路由 {} 条",
                    sizeOf(defs), countRoutes());
        } catch (Exception e) {
            log.error("[网关路由热更新] 记录初始路由信息失败", e);
        }
    }

    @EventListener(NacosConfigRefreshEvent.class)
    public void onNacosConfigRefresh(NacosConfigRefreshEvent event) {
        final String dataId = event.getDataId();
        final String group = event.getGroup();
        log.error("[网关路由热更新] 检测到 Nacos 配置变更(dataId={}, group={})，准备从 Nacos 重建网关路由",
                dataId, group);
        // 延迟 500ms 在独立线程执行：
        // 1) 避开 Nacos/Spring Cloud 自身处理该变更的并发窗口；
        // 2) 不用 boundedElastic（实测出现过任务被延迟 50s+ 才执行的情况），
        //    且绝不在 Nacos 监听线程上做阻塞操作(会拖慢 Nacos 心跳)。
        scheduler.schedule(() -> refreshRoutes(dataId, group), 500, TimeUnit.MILLISECONDS);
    }

    private void refreshRoutes(String dataId, String group) {
        log.error("[网关路由热更新] 开始重建 dataId={}", dataId);
        try {
            final int before = countRoutes();
            final int defBefore = sizeOf(gatewayProperties.getRoutes());

            List<RouteDefinition> freshDefs = fetchRouteDefinitions(dataId, group);
            if (freshDefs == null || freshDefs.isEmpty()) {
                log.error("[网关路由热更新] dataId={} 未拿到可用的路由配置，保持现有 {} 条路由不变",
                        dataId, defBefore);
                return;
            }

            // 校验通过后才写入真实 bean，避免坏配置把路由打空导致全站 500
            gatewayProperties.setRoutes(freshDefs);
            eventPublisher.publishEvent(new RefreshRoutesEvent(this));

            log.error("[网关路由热更新] 已重建路由 dataId={} 路由定义 {} → {} 条，实际路由 {} → {} 条",
                    dataId, defBefore, freshDefs.size(), before, countRoutes());
        } catch (Throwable t) {
            // 兜住一切异常：热更新失败最多保持旧路由，绝不能影响网关正常转发
            log.error("[网关路由热更新] 重建路由失败 dataId=" + dataId, t);
        }
    }

    /**
     * 从 Nacos 拉取最新配置并解析出路由定义。
     *
     * <p>重点处理"内容截断"：Nacos 配置变更瞬间，客户端本地快照(blob)可能只写了一半，
     * 而 ConfigService.getConfig() 优先读缓存，此时会返回被截断的内容（实测确有其事）。
     * 因此这里要求：<b>连续两次拉到完全一致</b>，且能成功解析出<b>完整的路由条目</b>，才认定内容可用。</p>
     *
     * @return null 表示始终未能拿到可用配置；空集合表示该 dataId 不含网关路由配置
     */
    private List<RouteDefinition> fetchRouteDefinitions(String dataId, String group) throws Exception {
        long backoff = BASE_BACKOFF;
        String prevContent = null;
        List<RouteDefinition> candidate = null;

        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            String content = pullContent(dataId, group);
            log.error("[网关路由热更新] 第 {}/{} 次拉取 dataId={} 长度={} md5={}",
                    i, MAX_ATTEMPTS, dataId, content == null ? -1 : content.length(), md5(content));

            if (!StringUtils.hasText(content)) {
                sleep(backoff);
                backoff = nextBackoff(backoff);
                continue;
            }

            List<RouteDefinition> defs = parseSafely(dataId, content);
            if (defs == null || defs.isEmpty()) {
                log.error("[网关路由热更新] dataId={} 第 {} 次内容不可用(长度={})，尾部: [{}]",
                        dataId, i, content.length(), tail(content, 200));
                sleep(backoff);
                backoff = nextBackoff(backoff);
                continue;
            }

            if (content.equals(prevContent)) {
                log.error("[网关路由热更新] dataId={} 内容已稳定(长度={})，解析出有效路由 {} 条",
                        dataId, content.length(), defs.size());
                return defs;
            }
            prevContent = content;
            candidate = defs;
            sleep(backoff);
            backoff = nextBackoff(backoff);
        }

        if (candidate != null) {
            log.error("[网关路由热更新] dataId={} 未等到内容稳定，采用最后一次可解析版本(长度={})，路由 {} 条",
                    dataId, prevContent == null ? -1 : prevContent.length(), candidate.size());
        }
        return candidate;
    }

    private String pullContent(String dataId, String group) throws Exception {
        long start = System.currentTimeMillis();
        ConfigService configService = nacosConfigManager.getConfigService();
        long timeout = nacosConfigManager.getNacosConfigProperties().getTimeout();
        String content = configService.getConfig(dataId, group, timeout);
        log.error("[网关路由热更新] getConfig 返回 dataId={} 耗时={}ms 长度={}",
                dataId, System.currentTimeMillis() - start, content == null ? -1 : content.length());
        return content;
    }

    /**
     * 解析配置内容。任何解析异常一律当作"内容不完整"返回 null，交给外层重试。
     * 同时过滤掉因截断导致的残缺条目（缺 uri 或缺 predicates），避免一条脏数据污染整份路由。
     */
    private List<RouteDefinition> parseSafely(String dataId, String content) {
        try {
            String extension = fileExtension(dataId);
            List<PropertySource<?>> sources = NacosDataParserHandler.getInstance()
                    .parseNacosData(dataId, content, extension);
            if (sources == null || sources.isEmpty()) {
                return null;
            }

            // 阶段一：只用 Nacos 最新内容绑定。
            // 关键：不能叠加旧环境兜底，否则"删掉某条路由"时旧值会把 routes[8]/routes[9] 补齐，导致删不掉。
            StandardEnvironment freshOnlyEnv = new StandardEnvironment();
            for (PropertySource<?> ps : sources) {
                freshOnlyEnv.getPropertySources().addFirst(ps);
            }
            GatewayProperties fresh = bindGatewayProperties(freshOnlyEnv);

            // 阶段二：最新内容里没绑到路由时，再叠加当前环境兜底重试一次。
            // 覆盖两种情况：① 该 dataId 不是网关配置(如 application-dev.yml) ② 配置里用了 ${...} 占位符。
            if (fresh == null || fresh.getRoutes() == null || fresh.getRoutes().isEmpty()) {
                StandardEnvironment withFallback = new StandardEnvironment();
                for (PropertySource<?> ps : sources) {
                    withFallback.getPropertySources().addFirst(ps);
                }
                for (PropertySource<?> ps : environment.getPropertySources()) {
                    if (!withFallback.getPropertySources().contains(ps.getName())) {
                        withFallback.getPropertySources().addLast(ps);
                    }
                }
                GatewayProperties fallback = bindGatewayProperties(withFallback);
                if (fallback != null && fallback.getRoutes() != null && !fallback.getRoutes().isEmpty()) {
                    fresh = fallback;
                }
            }
            if (fresh == null || fresh.getRoutes() == null) {
                return null;
            }

            List<RouteDefinition> valid = fresh.getRoutes().stream()
                    .filter(Objects::nonNull)
                    .filter(this::isComplete)
                    .collect(Collectors.toList());
            int dropped = fresh.getRoutes().size() - valid.size();
            if (dropped > 0) {
                log.error("[网关路由热更新] dataId={} 解析到 {} 条路由，其中 {} 条因缺 uri/predicates 被丢弃(疑似截断)",
                        dataId, fresh.getRoutes().size(), dropped);
            }
            return valid;
        } catch (Throwable t) {
            log.error("[网关路由热更新] dataId=" + dataId + " 内容解析异常(疑似截断)", t);
            return null;
        }
    }

    private GatewayProperties bindGatewayProperties(ConfigurableEnvironment env) {
        try {
            return new Binder(ConfigurationPropertySources.get(env))
                    .bind(GATEWAY_PREFIX, Bindable.of(GatewayProperties.class))
                    .orElse(null);
        } catch (Throwable t) {
            log.error("[网关路由热更新] 绑定 " + GATEWAY_PREFIX + " 失败", t);
            return null;
        }
    }

    /** 一条路由定义是否完整：uri 与 predicates 缺一不可 */
    private boolean isComplete(RouteDefinition def) {
        if (!StringUtils.hasText(def.getId())) {
            return false;
        }
        boolean hasUri = def.getUri() != null && StringUtils.hasText(def.getUri().toString());
        boolean hasPredicate = def.getPredicates() != null && !def.getPredicates().isEmpty();
        return hasUri && hasPredicate;
    }

    private String fileExtension(String dataId) {
        int idx = dataId.lastIndexOf('.');
        return idx > 0 && idx < dataId.length() - 1 ? dataId.substring(idx + 1) : "properties";
    }

    private int countRoutes() {
        try {
            List<?> routes = Flux.from(routeLocator.getRoutes()).collectList().block(Duration.ofSeconds(5));
            return routes == null ? 0 : routes.size();
        } catch (Exception e) {
            log.error("[网关路由热更新] 统计当前路由数失败", e);
            return -1;
        }
    }

    private int sizeOf(List<RouteDefinition> defs) {
        return defs == null ? 0 : defs.size();
    }

    private long nextBackoff(long backoff) {
        return Math.min(backoff * 2, MAX_BACKOFF);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String md5(String content) {
        if (content == null) {
            return "-";
        }
        return DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

    /** 取内容尾部若干字符，换行转义后便于在单行日志里判断截断位置 */
    private String tail(String content, int n) {
        if (content == null) {
            return "";
        }
        String s = content.length() <= n ? content : content.substring(content.length() - n);
        return s.replace("\r", "\\r").replace("\n", "\\n");
    }
}

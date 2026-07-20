package cn.com.mfish.common.ai.http;

import cn.com.mfish.common.ai.engine.ApiToolEngine;
import cn.com.mfish.common.ai.provider.ToolProvider;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.utils.Utils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * HTTP工具提供者 —— 基于OpenAPI文档自动生成REST接口工具
 * <p>
 * <b>零配置</b>：baseUrl 自动解析，无需手动配置。
 * <ul>
 *     <li>单实例模式（mfish.type=boot）：自动使用 {@code http://localhost:{server.port}}</li>
 *     <li>微服务模式：通过 DiscoveryClient 查询 {@code mf-gateway} 服务实例地址</li>
 *     <li>手动覆盖：通过 {@code mf.ai.http-provider.base-url} 配置（可选，向后兼容）</li>
 * </ul>
 * </p>
 * <p>
 * <b>文档加载模式</b>：根据 {@link Utils#getServiceType()} 判断服务类型，
 * 单实例模式拉取聚合文档按路径前缀分组，微服务模式通过 swagger-config 逐个拉取。
 * </p>
 * <p>
 * <b>路径前缀映射</b>：从 {@link ServiceConstants.MfService} 枚举自动构建，
 * 与网关路由配置保持一致，无需重复维护。
 * </p>
 * <p>
 * <b>异步加载 + 定时重试</b>：不阻塞 AI 服务启动。失败的服务每 {@code retry-interval} 秒重试，
 * 成功后动态注册到 {@link ApiToolEngine}。微服务滞后启动也能自动注册。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class HttpToolProvider implements ToolProvider, SmartInitializingSingleton {

    private final OpenApiDocParser parser = new OpenApiDocParser();

    // ==================== 配置 ====================

    /**
     * 基础地址（可选手动覆盖）
     * <p>
     * 未配置时自动解析：
     * <ul>
     *     <li>单实例模式（mfish.type=boot）：{@code http://localhost:{server.port}}</li>
     *     <li>微服务模式：通过 DiscoveryClient 查询 {@code mf-gateway} 服务实例地址</li>
     * </ul>
     * </p>
     */
    @Value("${mf.ai.http-provider.base-url:}")
    private String configuredBaseUrl;
    /** OpenAPI 文档路径（默认 /v3/api-docs） */
    @Value("${mf.ai.http-provider.docs-path:/v3/api-docs}")
    private String docsPath;
    /** swagger-config 端点路径（默认 /v3/api-docs/swagger-config） */
    @Value("${mf.ai.http-provider.swagger-config-path:/v3/api-docs/swagger-config}")
    private String swaggerConfigPath;
    /** 重试间隔（秒） */
    @Value("${mf.ai.http-provider.retry-interval:30}")
    private long retryIntervalSeconds;
    /** 当前服务端口（单实例模式用于构建 baseUrl） */
    @Value("${server.port:}")
    private int serverPort;

    /**
     * Spring 应用上下文
     * <p>
     * 用于动态查找 {@code DiscoveryClient} bean，避免直接引用 Spring Cloud 类型。
     * 单实例模式（mf-start-boot）未引入 spring-cloud，DiscoveryClient 类不在 classpath，
     * 直接 @Autowired DiscoveryClient 会导致 JVM 加载 HttpToolProvider 时 NoClassDefFoundError。
     * 通过 ApplicationContext 反射查找，只有微服务模式才会实际调用到。
     * </p>
     */
    @Autowired
    private ApplicationContext applicationContext;

    /** 网关服务名（微服务模式从此服务查询网关实例地址） */
    @Value("${mf.ai.http-provider.gateway-service:mf-gateway}")
    private String gatewayServiceName;

    // ==================== 运行时状态 ====================

    @Autowired(required = false)
    private ApiToolEngine apiToolEngine;

    /** 缓存的工具列表（供 discoverTools 返回，key=serviceId，value=已成功加载的工具） */
    private final Map<String, List<ToolCallback>> cachedTools = new ConcurrentHashMap<>();

    /** 定时重试线程池 */
    private ScheduledExecutorService retryExecutor;

    /** 文档模式：true=聚合文档（单实例），false=独立文档（微服务） */
    private volatile Boolean singleDocMode;

    /**
     * 期望加载的全部服务ID（来自 {@link ServiceConstants.MfService} 枚举）
     * <p>
     * 定时重试的判断依据：{@code 期望全集 - cachedTools.keySet() = 未完成加载的服务}。
     * 只有所有期望服务都成功加载后才停止重试。
     * </p>
     */
    private Set<String> expectedServices;

    @Override
    public String getType() {
        return "http";
    }

    @Override
    public Map<String, List<ToolCallback>> discoverTools() {
        return cachedTools;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 异步加载，不阻塞启动
        startAsyncLoad();
    }

    // ==================== baseUrl 自动解析 ====================

    /**
     * 解析 baseUrl（基础地址）
     * <p>
     * 解析优先级：
     * <ol>
     *     <li>显式配置 {@code mf.ai.http-provider.base-url}（手动覆盖，向后兼容）</li>
     *     <li>单实例模式：{@code http://localhost:{server.port}}</li>
     *     <li>微服务模式：通过 DiscoveryClient 查询 {@code mf-gateway} 实例地址</li>
     * </ol>
     * </p>
     * <p>
     * 服务模式判断统一使用 {@link Utils#getServiceType()}，与 {@link #loadFromBaseUrl} 保持一致，
     * 避免重复判断。
     * </p>
     *
     * @return baseUrl（不含尾部斜杠），解析失败返回 null
     */
    private String resolveBaseUrl() {
        // 1. 显式配置优先
        if (configuredBaseUrl != null && !configuredBaseUrl.trim().isEmpty()) {
            String url = configuredBaseUrl.trim().replaceAll("/+$", "");
            log.info("[HttpToolProvider] 使用配置的 base-url: {}", url);
            return url;
        }

        // 2. 根据服务类型自动获取
        boolean isBoot = ServiceConstants.isBoot(Utils.getServiceType());
        if (isBoot) {
            String url = buildLocalBaseUrl();
            log.info("[HttpToolProvider] 单实例模式，使用当前服务地址: {}", url);
            return url;
        }

        // 3. 微服务模式：从服务发现查询网关地址
        String url = resolveGatewayBaseUrl();
        if (url != null) {
            log.info("[HttpToolProvider] 微服务模式，从服务发现获取网关地址: {}", url);
        }
        return url;
    }

    /**
     * 单实例模式：构建当前服务地址
     * <p>
     * 使用 {@code server.port} 配置项构建 {@code http://localhost:{port}}。
     * </p>
     */
    private String buildLocalBaseUrl() {
        return "http://localhost:" + serverPort;
    }

    /**
     * 微服务模式：通过服务发现查询网关服务实例地址
     * <p>
     * 使用反射动态查找 {@code DiscoveryClient} bean，避免直接引用 Spring Cloud 类型。
     * 单实例模式下 DiscoveryClient 类不在 classpath，此方法不会被调用（resolveBaseUrl 中已按服务类型分支）。
     * 微服务模式下 DiscoveryClient 由 spring-cloud-starter-alibaba-nacos-discovery 自动注册。
     * </p>
     *
     * @return 网关 baseUrl，查询失败返回 null
     */
    private String resolveGatewayBaseUrl() {
        try {
            // 动态加载 DiscoveryClient 类，避免单实例模式下 NoClassDefFoundError
            Class<?> discoveryClientClass = Class.forName(
                    "org.springframework.cloud.client.discovery.DiscoveryClient");
            Object discoveryClient = applicationContext.getBean(discoveryClientClass);
            // 反射调用 getInstances(String serviceId)
            java.lang.reflect.Method getInstances = discoveryClientClass.getMethod("getInstances", String.class);
            List<?> instances = (List<?>) getInstances.invoke(discoveryClient, gatewayServiceName);
            if (instances == null || instances.isEmpty()) {
                log.warn("[HttpToolProvider] 服务发现中未找到网关服务实例: {}", gatewayServiceName);
                return null;
            }
            // 反射调用 getUri() 获取实例地址
            Object instance = instances.getFirst();
            java.lang.reflect.Method getUri = instance.getClass().getMethod("getUri");
            Object uri = getUri.invoke(instance);
            return uri != null ? uri.toString().replaceAll("/+$", "") : null;
        } catch (ClassNotFoundException e) {
            log.warn("[HttpToolProvider] 微服务模式但 DiscoveryClient 类不在 classpath，" +
                    "请配置 mf.ai.http-provider.base-url 或引入 spring-cloud-starter-alibaba-nacos-discovery");
            return null;
        } catch (org.springframework.beans.factory.NoSuchBeanDefinitionException e) {
            log.warn("[HttpToolProvider] DiscoveryClient bean 未注册: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("[HttpToolProvider] 查询网关地址异常: {}", e.getMessage());
            return null;
        }
    }

    // ==================== 异步加载入口 ====================

    /**
     * 启动异步加载线程
     * <p>
     * 延迟2秒后开始加载，避免与启动流程竞争资源。
     * </p>
     * <p>
     * <b>微服务模式下的网关等待</b>：AI 服务可能先于网关启动，此时 {@link #resolveGatewayBaseUrl()} 返回 null。
     * 采用循环重试机制，每 {@link #retryIntervalSeconds} 秒尝试一次，直到获取到网关地址或线程被中断。
     * 获取到网关地址后，再进入文档加载和失败服务定时重试流程。
     * </p>
     * <p>
     * 单实例模式不存在此问题，baseUrl 直接从 {@code server.port} 构建。
     * </p>
     */
    private void startAsyncLoad() {
        Thread asyncLoader = new Thread(() -> {
            try {
                Thread.sleep(2000);
                boolean isBoot = ServiceConstants.isBoot(Utils.getServiceType());
                String url = resolveBaseUrl();
                // 微服务模式下，网关可能尚未启动，循环等待获取网关地址
                while (url == null && !isBoot) {
                    log.info("[HttpToolProvider] 网关尚未就绪，{}秒后重试获取网关地址...", retryIntervalSeconds);
                    Thread.sleep(retryIntervalSeconds * 1000);
                    url = resolveBaseUrl();
                }
                if (url == null) {
                    log.warn("[HttpToolProvider] 未能解析 baseUrl，跳过OpenAPI文档加载");
                    return;
                }
                loadFromBaseUrl(url);
                if (hasPendingServices()) {
                    startRetryScheduler(url);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("[HttpToolProvider] 异步加x载异常", e);
            }
        }, "http-tool-provider-loader");
        asyncLoader.setDaemon(true);
        asyncLoader.start();
    }

    // ==================== 核心加载逻辑 ====================

    /**
     * 通过 base-url 拉取 OpenAPI 文档
     * <p>
     * 服务模式判断统一使用 {@link Utils#getServiceType()}，与 {@link #resolveBaseUrl} 保持一致：
     * <ol>
     *     <li>单实例模式：直接拉取聚合文档 {@code {base-url}/v3/api-docs}，按路径前缀分组</li>
     *     <li>微服务模式：通过 swagger-config 端点发现服务列表，逐个拉取</li>
     * </ol>
     * </p>
     * <p>
     * 首次调用时初始化 {@link #expectedServices}（来自 {@link ServiceConstants.MfService} 枚举），
     * 作为定时重试的期望全集。未启动或拉取失败的服务不会进入 {@link #cachedTools}，
     * 定时重试时通过 {@code 期望全集 - cachedTools.keySet()} 判断是否还有待加载服务。
     * </p>
     */
    private void loadFromBaseUrl(String baseUrl) {
        log.info("[HttpToolProvider] 开始从 {} 加载OpenAPI文档", baseUrl);
        // 初始化期望服务全集（首次调用时）
        if (expectedServices == null) {
            expectedServices = ServiceConstants.MfService.allServiceIds();
            log.info("[HttpToolProvider] 期望加载的服务全集: {}", expectedServices);
        }

        boolean isBoot = ServiceConstants.isBoot(Utils.getServiceType());
        if (isBoot) {
            // 单实例模式：直接拉取聚合文档，按路径前缀分组
            singleDocMode = true;
            log.info("[HttpToolProvider] 文档模式：聚合文档（单实例）");
            Map<String, String> pathMappings = buildPathMappings();
            loadSingleDoc(baseUrl, pathMappings);
            return;
        }

        // 微服务模式：通过 swagger-config 发现服务列表，逐个拉取
        singleDocMode = false;
        List<ServiceDocUrl> serviceUrls = fetchSwaggerConfig(baseUrl);
        if (serviceUrls.isEmpty()) {
            log.warn("[HttpToolProvider] 微服务模式但未发现任何服务文档，等待其他服务启动后重试");
            return;
        }
        log.info("[HttpToolProvider] 文档模式：独立文档（微服务），发现 {} 个服务: {}",
                serviceUrls.size(), serviceUrls.stream().map(ServiceDocUrl::name).toList());
        loadMultiDocFromGateway(baseUrl, serviceUrls);
    }

    /**
     * 微服务模式：通过 swagger-config 发现的服务列表，逐个拉取 OpenAPI 文档
     * <p>
     * 拉取成功的服务进入 {@link #cachedTools}；未启动或拉取失败的服务不会进入，
     * 定时重试时通过 {@code 期望全集 - cachedTools.keySet()} 识别并重试。
     * </p>
     */
    private void loadMultiDocFromGateway(String gatewayUrl, List<ServiceDocUrl> serviceUrls) {
        for (ServiceDocUrl sdu : serviceUrls) {
            try {
                List<ToolCallback> callbacks = loadServiceToolsFromGateway(gatewayUrl, sdu);
                if (!callbacks.isEmpty()) {
                    registerTools(sdu.name(), callbacks);
                } else {
                    log.warn("[HttpToolProvider] 服务 {} 文档为空，等待重试", sdu.name());
                }
            } catch (Exception e) {
                log.warn("[HttpToolProvider] 服务 {} 拉取失败，等待重试: {}", sdu.name(), e.getMessage());
            }
        }
    }

    /**
     * 单实例模式：拉取一次聚合文档，按路径前缀分组
     * <p>
     * 拉取成功的服务进入 {@link #cachedTools}；分组为空或拉取失败的服务不会进入，
     * 定时重试时通过 {@code 期望全集 - cachedTools.keySet()} 识别并重试。
     * </p>
     */
    private void loadSingleDoc(String baseUrl, Map<String, String> pathMappings) {
        if (pathMappings.isEmpty()) {
            log.warn("[HttpToolProvider] 路径前缀映射为空，无法分组，跳过");
            return;
        }
        log.info("[HttpToolProvider] 聚合文档模式：拉取 {}{}，按路径前缀分组: {}", baseUrl, docsPath, pathMappings);
        WebClient webClient = createWebClient(baseUrl);
        try {
            String openApiJson = fetchOpenApiDoc(webClient, "聚合文档");
            if (openApiJson == null || openApiJson.isEmpty()) {
                log.warn("[HttpToolProvider] 聚合文档为空，等待重试");
                return;
            }
            Map<String, List<ToolCallback>> tools = parser.parseByPathPrefix(openApiJson, webClient, pathMappings);
            for (Map.Entry<String, List<ToolCallback>> entry : tools.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    registerTools(entry.getKey(), entry.getValue());
                }
            }
            log.info("[HttpToolProvider] 聚合文档模式 加载完成，共 {} 个服务", tools.size());
        } catch (Exception e) {
            log.error("[HttpToolProvider] 聚合文档模式 加载失败", e);
        }
    }

    // ==================== swagger-config 拉取 ====================

    /**
     * 拉取网关的 swagger-config，解析服务列表
     * <p>
     * 返回格式：{@code {"urls":[{"name":"mf-sys","url":"/sys/v3/api-docs"},...]}}
     * </p>
     * <p>
     * 只在微服务模式下调用。通过校验 url 是否以已知服务前缀开头来过滤，
     * 排除网关自身等非服务条目。
     * </p>
     *
     * @return 服务文档URL列表，拉取失败返回空列表
     */
    private List<ServiceDocUrl> fetchSwaggerConfig(String baseUrl) {
        WebClient webClient = createWebClient(baseUrl);
        try {
            String json = webClient.get()
                    .uri(swaggerConfigPath)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if (json == null || json.isEmpty()) return List.of();
            JSONObject config = JSON.parseObject(json);
            JSONArray urls = config.getJSONArray("urls");
            if (urls == null || urls.isEmpty()) return List.of();

            Map<String, String> pathMappings = buildPathMappings();
            List<ServiceDocUrl> result = new ArrayList<>();
            for (int i = 0; i < urls.size(); i++) {
                JSONObject url = urls.getJSONObject(i);
                String name = url.getString("name");
                String urlPath = url.getString("url");
                if (name == null || name.isEmpty() || urlPath == null || urlPath.isEmpty()) {
                    continue;
                }
                // 只接受 url 以已知服务前缀开头的条目，过滤非服务条目
                if (!isMicroserviceDocUrl(urlPath, pathMappings)) {
                    log.debug("[HttpToolProvider] 跳过非服务文档URL: name={} url={}", name, urlPath);
                    continue;
                }
                result.add(new ServiceDocUrl(name, urlPath));
            }
            return result;
        } catch (Exception e) {
            log.debug("[HttpToolProvider] swagger-config 拉取失败: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * 判断 swagger-config 返回的 url 是否为微服务文档地址
     * <p>
     * 微服务文档 url 以服务前缀开头，如 {@code /sys/v3/api-docs}。
     * </p>
     */
    private boolean isMicroserviceDocUrl(String urlPath, Map<String, String> pathMappings) {
        for (String prefix : pathMappings.keySet()) {
            if (urlPath.startsWith(prefix + "/") || urlPath.equals(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过网关拉取单个服务的 OpenAPI 文档
     * <p>
     * swagger-config 返回的 url 如 /sys/v3/api-docs，
     * 提取路径前缀 /sys 作为 WebClient baseUrl，调用时路径自动拼接。
     * </p>
     */
    private List<ToolCallback> loadServiceToolsFromGateway(String gatewayUrl, ServiceDocUrl sdu) {
        String pathPrefix = extractPathPrefix(sdu.url());
        // WebClient baseUrl = 网关地址 + 路径前缀（如 http://localhost:8888/sys）
        WebClient webClient = createWebClient(gatewayUrl + pathPrefix);
        // 拉取 OpenAPI 文档（用完整 url）
        String openApiJson = createWebClient(gatewayUrl)
                .get()
                .uri(sdu.url())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        if (openApiJson == null || openApiJson.isEmpty()) return List.of();
        return parser.parse(openApiJson, webClient, sdu.name());
    }

    /**
     * 从 swagger-config 返回的 url 中提取路径前缀
     * <p>
     * 如 /sys/v3/api-docs → /sys
     * </p>
     */
    private String extractPathPrefix(String url) {
        String trimmed = url.startsWith("/") ? url.substring(1) : url;
        int idx = trimmed.indexOf('/');
        return idx > 0 ? "/" + trimmed.substring(0, idx) : "";
    }

    // ==================== 定时重试 ====================

    /**
     * 判断是否还有待加载的服务
     * <p>
     * 基于 {@code 期望全集 - cachedTools.keySet()} 判断。
     * 未启动、启动但拉取失败、文档为空的服务都不会进入 cachedTools，
     * 因此都会被识别为待加载。
     * </p>
     */
    private boolean hasPendingServices() {
        if (expectedServices == null) return false;
        for (String serviceId : expectedServices) {
            if (!cachedTools.containsKey(serviceId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取待加载的服务ID列表
     */
    private List<String> getPendingServices() {
        if (expectedServices == null) return List.of();
        List<String> pending = new ArrayList<>();
        for (String serviceId : expectedServices) {
            if (!cachedTools.containsKey(serviceId)) {
                pending.add(serviceId);
            }
        }
        return pending;
    }

    private void startRetryScheduler(String baseUrl) {
        if (retryExecutor != null) return;
        retryExecutor = new ScheduledThreadPoolExecutor(1, r -> {
            Thread t = new Thread(r, "http-tool-provider-retry");
            t.setDaemon(true);
            return t;
        });
        log.info("[HttpToolProvider] 启动定时重试，间隔 {} 秒，待加载服务: {}",
                retryIntervalSeconds, getPendingServices());

        retryExecutor.scheduleWithFixedDelay(() -> {
            try {
                retryPendingServices(baseUrl);
            } catch (Exception e) {
                log.error("[HttpToolProvider] 定时重试异常", e);
            }
        }, retryIntervalSeconds, retryIntervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * 重试加载未完成的服务
     * <p>
     * 根据文档模式选择重试策略：
     * <ul>
     *     <li>微服务模式：重新拉取 swagger-config，尝试加载所有待加载服务</li>
     *     <li>单实例模式：重新拉取聚合文档，重新分组</li>
     * </ul>
     * </p>
     * <p>
     * 判断依据：{@code 期望全集 - cachedTools.keySet()}，而非显式记录的失败列表。
     * 这样即使服务未启动（未出现在 swagger-config 中），也能在下次重试时被识别并尝试加载。
     * </p>
     */
    private void retryPendingServices(String baseUrl) {
        if (!hasPendingServices()) {
            shutdownRetryScheduler();
            return;
        }
        List<String> pending = getPendingServices();
        log.info("[HttpToolProvider] 定时重试开始，待加载服务: {}", pending);

        if (Boolean.FALSE.equals(singleDocMode)) {
            // 微服务模式：重新拉取 swagger-config，尝试加载待加载服务
            List<ServiceDocUrl> serviceUrls = fetchSwaggerConfig(baseUrl);
            if (serviceUrls.isEmpty()) {
                log.info("[HttpToolProvider] swagger-config 仍未返回服务列表，等待下次重试");
                return;
            }
            for (ServiceDocUrl sdu : serviceUrls) {
                // 只重试尚未成功加载的服务
                if (cachedTools.containsKey(sdu.name())) continue;
                try {
                    List<ToolCallback> callbacks = loadServiceToolsFromGateway(baseUrl, sdu);
                    if (!callbacks.isEmpty()) {
                        registerTools(sdu.name(), callbacks);
                        log.info("[HttpToolProvider] 定时重试成功: service={}", sdu.name());
                    }
                } catch (Exception e) {
                    log.debug("[HttpToolProvider] 定时重试失败: service={} {}", sdu.name(), e.getMessage());
                }
            }
        } else {
            // 单实例模式：重新拉取聚合文档
            Map<String, String> pathMappings = buildPathMappings();
            try {
                WebClient webClient = createWebClient(baseUrl);
                String openApiJson = fetchOpenApiDoc(webClient, "聚合文档(重试)");
                if (openApiJson != null && !openApiJson.isEmpty()) {
                    Map<String, List<ToolCallback>> tools = parser.parseByPathPrefix(openApiJson, webClient, pathMappings);
                    for (Map.Entry<String, List<ToolCallback>> entry : tools.entrySet()) {
                        // 只注册尚未加载的服务
                        if (!cachedTools.containsKey(entry.getKey())
                                && entry.getValue() != null && !entry.getValue().isEmpty()) {
                            registerTools(entry.getKey(), entry.getValue());
                            log.info("[HttpToolProvider] 定时重试成功: service={}", entry.getKey());
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("[HttpToolProvider] 聚合文档重试失败: {}", e.getMessage());
            }
        }

        if (!hasPendingServices()) {
            log.info("[HttpToolProvider] 所有服务已加载完成，停止定时重试");
            shutdownRetryScheduler();
        }
    }

    private void shutdownRetryScheduler() {
        if (retryExecutor != null) {
            retryExecutor.shutdown();
            retryExecutor = null;
        }
    }

    // ==================== 工具注册 ====================

    /**
     * 注册工具到 ApiToolEngine 和本地缓存
     */
    private void registerTools(String serviceId, List<ToolCallback> callbacks) {
        if (callbacks == null || callbacks.isEmpty()) return;
        cachedTools.put(serviceId, new ArrayList<>(callbacks));
        if (apiToolEngine != null) {
            apiToolEngine.replace(serviceId, callbacks);
        }
        log.info("[HttpToolProvider] 注册成功: service={} 工具数={}", serviceId, callbacks.size());
    }

    // ==================== 工具方法 ====================

    private WebClient createWebClient(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl.replaceAll("/+$", ""))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
    }

    private String fetchOpenApiDoc(WebClient webClient, String label) {
        log.info("[HttpToolProvider] 请求 {} 的OpenAPI文档: {}", label, docsPath);
        try {
            return webClient.get()
                    .uri(docsPath)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("[HttpToolProvider] 拉取 {} 的OpenAPI文档失败: {}", label, e.getMessage());
            return null;
        }
    }

    /**
     * 基于 {@link ServiceConstants.MfService} 枚举构建路径前缀 → serviceId 映射
     * <p>
     * 单一数据源：所有服务前缀与 serviceId 的对应关系在
     * {@link ServiceConstants.MfService} 中集中定义，本类不再重复维护字符串常量。
     * </p>
     */
    private Map<String, String> buildPathMappings() {
        Map<String, String> mappings = new HashMap<>();
        for (ServiceConstants.MfService service : ServiceConstants.MfService.values()) {
            mappings.put(service.getGatewayPrefix(), service.getValue());
        }
        return mappings;
    }

    private record ServiceDocUrl(String name, String url) {}
}

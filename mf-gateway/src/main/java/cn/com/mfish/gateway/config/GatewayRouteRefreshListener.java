package cn.com.mfish.gateway.config;

import com.alibaba.cloud.nacos.refresh.NacosConfigRefreshEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author: mfish
 * @description: 网关路由动态刷新
 * @date: 2026/10/15
 *
 * 背景：网关路由(spring.cloud.gateway.routes)由 Nacos 配置中心下发，仅在应用启动时加载一次。
 * Spring Cloud Alibaba 2025.x 在配置变更时发布 NacosConfigRefreshEvent，由
 * NacosPropertySourceRefreshListener 重绑定 @ConfigurationProperties(含 spring.cloud.gateway)；
 * 而 Spring Cloud Gateway 的 CachingRouteLocator 只响应 RefreshRoutesEvent——两者之间没有桥接。
 * 因此修改网关 Nacos 配置后，新路由不会生效，请求回落至静态资源处理
 * (NoResourceFoundException 404，被全局异常处理器包装为“系统异常 500”)，必须重启网关才能恢复。
 *
 * 方案：监听 NacosConfigRefreshEvent，向上下文补发 RefreshRoutesEvent，触发
 * CachingRouteLocator 清空缓存并按最新的 spring.cloud.gateway 配置重建路由，
 * 实现网关配置热更新、免重启。
 */
@Slf4j
@Component
public class GatewayRouteRefreshListener {

    private final ApplicationEventPublisher eventPublisher;

    public GatewayRouteRefreshListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @EventListener(NacosConfigRefreshEvent.class)
    public void onNacosConfigRefresh(NacosConfigRefreshEvent event) {
        // 置于最低优先级：确保 NacosPropertySourceRefreshListener 已重绑定 spring.cloud.gateway 之后，
        // CachingRouteLocator 重新读取到的才是最新路由配置。
        log.info("检测到 Nacos 配置变更(dataId={})，触发网关路由动态重载 RefreshRoutesEvent", event.getDataId());
        this.eventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }
}

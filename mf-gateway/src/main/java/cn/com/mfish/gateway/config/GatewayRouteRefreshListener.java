package cn.com.mfish.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.parser.NacosDataParserHandler;
import com.alibaba.cloud.nacos.refresh.NacosConfigRefreshEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Refresh Gateway routes after the gateway Nacos configuration changes. */
@Slf4j
@Component
public class GatewayRouteRefreshListener {

    private final ApplicationEventPublisher eventPublisher;
    private final NacosConfigManager nacosConfigManager;
    private final GatewayProperties gatewayProperties;

    public GatewayRouteRefreshListener(ApplicationEventPublisher eventPublisher,
                                       NacosConfigManager nacosConfigManager,
                                       GatewayProperties gatewayProperties) {
        this.eventPublisher = eventPublisher;
        this.nacosConfigManager = nacosConfigManager;
        this.gatewayProperties = gatewayProperties;
    }

    @EventListener(NacosConfigRefreshEvent.class)
    public void onNacosConfigRefresh(NacosConfigRefreshEvent event) {
        try {
            String dataId = event.getDataId();
            String group = event.getGroup();
            String content = nacosConfigManager.getConfigService().getConfig(
                    dataId, group, nacosConfigManager.getNacosConfigProperties().getTimeout());
            if (!StringUtils.hasText(content)) {
                return;
            }

            List<PropertySource<?>> propertySources = NacosDataParserHandler.getInstance()
                    .parseNacosData(dataId, content, fileExtension(dataId));
            if (propertySources == null || propertySources.isEmpty()) {
                return;
            }

            // Bind only the changed Nacos document so removed routes stay removed.
            StandardEnvironment environment = new StandardEnvironment();
            propertySources.forEach(environment.getPropertySources()::addFirst);
            GatewayProperties properties = new Binder(ConfigurationPropertySources.get(environment))
                    .bind(GatewayProperties.PREFIX, Bindable.of(GatewayProperties.class))
                    .orElse(null);
            if (properties == null || properties.getRoutes() == null || properties.getRoutes().isEmpty()) {
                return;
            }

            List<RouteDefinition> routes = properties.getRoutes().stream()
                    .filter(Objects::nonNull)
                    .filter(this::isComplete)
                    // Explicit Nacos routes must take precedence over DiscoveryClient routes.
                    .peek(route -> {
                        if (route.getOrder() == 0) {
                            route.setOrder(-100);
                        }
                    })
                    .collect(Collectors.toList());
            if (routes.isEmpty()) {
                return;
            }

            gatewayProperties.setRoutes(routes);
            log.info("Refreshing Gateway routes from Nacos: dataId={}, routes={}", dataId, routes.size());
            eventPublisher.publishEvent(new RefreshRoutesEvent(this));
        } catch (Exception e) {
            log.error("Failed to refresh Gateway routes from Nacos", e);
        }
    }

    private String fileExtension(String dataId) {
        int dot = dataId.lastIndexOf('.');
        return dot >= 0 ? dataId.substring(dot + 1) : "properties";
    }

    private boolean isComplete(RouteDefinition route) {
        return StringUtils.hasText(route.getId()) && route.getUri() != null && !route.getPredicates().isEmpty();
    }
}

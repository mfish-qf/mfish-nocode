package cn.com.mfish.gateway.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 文档拦截
 * @author: mfish
 * @date: 2024/4/18
 */
@Slf4j
@Component
public class SwaggerInstancesSubscriber extends Subscriber<InstancesChangeEvent> {

    private static final String ID_PREFIX = "ReactiveCompositeDiscoveryClient_";
    private static final String DOC_PATH = "/v3/api-docs";
    @Resource
    private RouteDefinitionLocator locator;
    @Resource
    private SwaggerUiConfigProperties swaggerUiConfigProperties;
    @Value("#{'${swagger.ignoreServers}'.split(',')}")
    private Set<String> ignoreServers;

    @PostConstruct
    public void registerToNotifyCenter() {
        // 注册监听事件
        NotifyCenter.registerSubscriber((this));
    }

    @Override
    public void onEvent(InstancesChangeEvent instancesChangeEvent) {
        log.info("接收到 InstancesChangeEvent 订阅事件：{}", JSON.toJSONString(instancesChangeEvent));
        List<RouteDefinition> routeList = locator.getRouteDefinitions().collectList().block();
        if (routeList == null || routeList.isEmpty()) {
            return;
        }
        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> set = new HashSet<>(routeList.size());
        for (RouteDefinition route : routeList) {
            if (!route.getMetadata().isEmpty()) {
                String name = route.getId().replace(ID_PREFIX, "");
                if (ignoreServers.contains(name)) {
                    continue;
                }
                set.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(name, "/" + name + DOC_PATH, name));
            }
        }
        swaggerUiConfigProperties.setUrls(set);
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }
}

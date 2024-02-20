package cn.com.mfish.boot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 网关属性（路径对应微服务路由路径）
 * @author: mfish
 * @date: 2024/2/20
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {
    private List<GatewayInfo> routes;

    @Data
    public static class GatewayInfo implements Serializable {
        private String path;
        private String packageName;
    }
}

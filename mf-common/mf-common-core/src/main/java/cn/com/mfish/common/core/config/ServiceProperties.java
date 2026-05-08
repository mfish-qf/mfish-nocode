package cn.com.mfish.common.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 服务属性
 * @author: mfish
 * @date: 2024/1/31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mfish")
public class ServiceProperties {
    /** 服务版本号 */
    private String version;
    /** 服务类型（cloud/boot） */
    private String type = "cloud";
}

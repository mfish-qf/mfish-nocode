package cn.com.mfish.common.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 缓存配置
 * @author: mfish
 * @date: 2023/1/15 14:37
 */
@Configuration
@ConfigurationProperties(prefix = "cache.temp")
@RefreshScope
@Data
public class CacheProperties {
    private long time = 7;
}

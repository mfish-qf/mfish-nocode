package cn.com.mfish.oauth.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @description: shiro白名单地址配置，用于配置不需要拦截的 url
 * @author: zibo
 * @date: 2024/3/22 14:52
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "security.ignore")
public class ShiroWhitesProperties {

    /**
     * 不需要拦截的 url 列表
     */
    private List<String> whites;

}

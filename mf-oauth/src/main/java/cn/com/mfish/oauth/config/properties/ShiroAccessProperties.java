package cn.com.mfish.oauth.config.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description: shiro access 配置属性，用于配置不需要拦截的 url
 * @author: zibo
 * @date: 2024/3/22 14:52
 * @slogan: 慢慢学，不要停。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "shiro-access")
public class ShiroAccessProperties {

    /**
     * 不需要拦截的 url 列表
     */
    private List<String> anonUrls;

}

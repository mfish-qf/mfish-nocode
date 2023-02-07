package cn.com.mfish.oauth.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author: mfish
 * @description: 微信基本属性
 * @date: 2021/12/14 9:15
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.miniapp")
@RefreshScope
public class WeChatProperties {
    /**
     * 设置微信小程序的appid
     */
    private String appId = "****";
    /**
     * 设置微信小程序的Secret
     */
    private String secret = "****";

}

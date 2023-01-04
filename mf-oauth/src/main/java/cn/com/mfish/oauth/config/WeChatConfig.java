package cn.com.mfish.oauth.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: mfish
 * @description: 微信配置
 * @date: 2021/12/14 9:20
 */
@Configuration
@EnableConfigurationProperties(WeChatProperties.class)
public class WeChatConfig {
    private WeChatProperties weChatProperties;

    @Autowired
    public WeChatConfig(WeChatProperties weChatProperties) {
        this.weChatProperties = weChatProperties;
    }

    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(weChatProperties.getAppId());
        config.setSecret(weChatProperties.getSecret());
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}
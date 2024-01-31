package cn.com.mfish.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: 资源配置
 * @author: mfish
 * @date: 2023/1/31 11:42
 */
@Configuration
public class OauthMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //微服务和单体服务分别走static/** 和 oauth2/static/**
        registry.addResourceHandler("oauth2/static/**", "static/**").
                addResourceLocations("classpath:/static/");
    }
}

package cn.com.mfish.boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: MVC配置
 * @author: mfish
 * @date: 2024/1/30
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 单体服务增加入口地址映射，与微服务地址同步
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/oauth2", p -> p.getPackage().getName().startsWith("cn.com.mfish.oauth"));
        configurer.addPathPrefix("/code", p -> p.getPackage().getName().startsWith("cn.com.mfish.code"));
        configurer.addPathPrefix("/sys", p -> p.getPackage().getName().startsWith("cn.com.mfish.sys"));
        configurer.addPathPrefix("/storage", p -> p.getPackage().getName().startsWith("cn.com.mfish.storage"));
        configurer.addPathPrefix("/scheduler", p -> p.getPackage().getName().startsWith("cn.com.mfish.scheduler"));
    }
}

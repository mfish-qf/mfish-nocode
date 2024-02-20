package cn.com.mfish.boot.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @description: MVC配置
 * @author: mfish
 * @date: 2024/1/30
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    GatewayProperties gatewayProperties;

    /**
     * 单体服务增加入口地址映射，与微服务地址同步
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(@NotNull PathMatchConfigurer configurer) {
        if (null == gatewayProperties || gatewayProperties.getRoutes() == null) {
            return;
        }
        for (GatewayProperties.GatewayInfo info : gatewayProperties.getRoutes()) {
            configurer.addPathPrefix(info.getPath(), p -> p.getPackage().getName().startsWith(info.getPackageName()));
        }
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern(CorsConfiguration.ALL);
        // 设置访问源请求头
        config.addAllowedHeader(CorsConfiguration.ALL);
        // 设置访问源请求方法
        config.addAllowedMethod(CorsConfiguration.ALL);
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

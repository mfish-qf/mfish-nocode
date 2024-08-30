package cn.com.mfish.gateway.config;

import cn.com.mfish.gateway.handler.RateLimitHandler;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author: mfish
 * @description: 限流配置
 * @date: 2021/12/30 17:41
 */
@Configuration
public class RateLimitConfig {
    /**
     * ip限流
     *
     * @return 返回ip限流
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getHostString());
    }

    /**
     * @return 请求连接限流
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getURI().getPath());
    }

    /**
     * 用户限流
     *
     * @return 返回用户限流
     */
    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getPrincipal().toString());
    }

    /**
     * 接口限流
     *
     * @return 返回接口限流
     */
    @Bean
    KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }

    /**
     * 通过sentinel限流
     *
     * @return 返回sentinel限流
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public RateLimitHandler sentinelGatewayExceptionHandler() {
        return new RateLimitHandler();
    }
}

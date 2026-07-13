package cn.com.mfish.gateway.config;

import cn.com.mfish.common.captcha.service.CheckCodeService;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * @author: mfish
 * @description: 路由配置信息
 * @date: 2021/12/21 16:33
 */
@Configuration
public class RouteFunctionConfig {
    @Resource
    CheckCodeService checkCodeService;

    /**
     * LLM代理路由，将/v1开头的请求路由到mf-ai微服务
     * LLM慢速长连接由mf-ai处理，避免占用网关资源
     */
    @Bean
    public RouteLocator llmProxyRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("llm-proxy", r -> r.path("/v1/**").uri("lb://mf-ai"))
                .build();
    }

    /**
     * 验证码生成路由，通过GET方式访问/captcha获取验证码
     *
     * @return 验证码路由函数
     */
    @Bean
    public RouterFunction<?> captcha() {
        return RouterFunctions.route(GET("/captcha")
                .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), request -> {
            try {
                return ServerResponse.ok().body(BodyInserters.fromValue(checkCodeService.createCaptcha()));
            } catch (Exception ex) {
                return Mono.error(ex);
            }
        });
    }
}

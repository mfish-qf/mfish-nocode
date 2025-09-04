package cn.com.mfish.gateway.config;

import cn.com.mfish.common.ai.agent.GatewayAssistant;
import cn.com.mfish.common.ai.entity.AiRouterVo;
import cn.com.mfish.common.captcha.service.CheckCodeService;
import cn.com.mfish.common.core.web.Result;
import jakarta.annotation.Resource;
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
    @Resource
    private GatewayAssistant gatewayAssistant;

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

    @Bean
    public RouterFunction<ServerResponse> aiRouter() {
        return RouterFunctions.route(GET("/ai/router"), request -> {
            String prompt = request.queryParam("prompt").orElse("介绍下摸鱼低代码");
            try {
                Mono<Result<AiRouterVo>> result = gatewayAssistant.chat(prompt);
                return ServerResponse.ok()
                        .body(result, Result.class);
            } catch (Exception ex) {
                return Mono.error(ex);
            }
        });
    }
}

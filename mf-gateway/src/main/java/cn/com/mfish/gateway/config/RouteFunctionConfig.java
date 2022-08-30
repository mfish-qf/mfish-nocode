package cn.com.mfish.gateway.config;

import cn.com.mfish.gateway.service.CheckCodeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author ：qiufeng
 * @description：路由配置信息
 * @date ：2021/12/21 16:33
 */
@Configuration
public class RouteFunctionConfig {
    @Resource
    CheckCodeService checkCodeService;

    @Bean
    public RouterFunction captcha() {
        return RouterFunctions.route(RequestPredicates.GET("/captcha")
                .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), request -> {
            try {
                return ServerResponse.ok().body(BodyInserters.fromValue(checkCodeService.createCaptcha()));
            } catch (Exception ex) {
                return Mono.error(ex);
            }
        });
    }
}

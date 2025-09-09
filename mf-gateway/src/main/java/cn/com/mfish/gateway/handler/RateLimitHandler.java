package cn.com.mfish.gateway.handler;

import cn.com.mfish.gateway.common.GatewayUtils;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * @author: mfish
 * @description: 限流回调处理
 * @date: 2022/1/1 12:00
 */
public class RateLimitHandler implements WebExceptionHandler {
    @NotNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NotNull Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        if (!BlockException.isBlockException(ex)) {
            return Mono.error(ex);
        }
        return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, ex)
                .flatMap(res -> GatewayUtils.webFluxResponseWriter(exchange.getResponse(), HttpStatus.INTERNAL_SERVER_ERROR, "错误:服务器繁忙，请稍候再试！"));
    }
}

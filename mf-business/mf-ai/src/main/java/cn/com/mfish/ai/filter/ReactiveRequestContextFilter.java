package cn.com.mfish.ai.filter;

import cn.com.mfish.common.core.utils.ServletUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @description: 请求上下文过滤器（WebFlux模式）
 * 负责将ServerWebExchange写入ThreadLocal，
 * 线程切换时的传播由ReactorContextPropagationConfig统一处理
 * @author: mfish
 * @date: 2026/7/3
 */
@Component
public class ReactiveRequestContextFilter implements WebFilter, Ordered {

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }
        ServletUtils.setExchange(exchange);
        return chain.filter(exchange)
                .doFinally(signal -> ServletUtils.removeExchange());
    }
    private boolean isExcludedPath(String path) {
        return path.startsWith("/actuator") || path.startsWith("/health");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
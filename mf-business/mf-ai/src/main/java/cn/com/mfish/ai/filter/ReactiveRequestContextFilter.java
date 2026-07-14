package cn.com.mfish.ai.filter;

import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.oauth.common.DataScopeUtils;
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
 * 线程切换时的传播由ReactorContextPropagationConfig统一处理。
 * <p>
 * 额外清理DataScopeUtils.context，防止Netty线程上的ThreadLocal残留：
 * DataScopeAspect在Netty线程set ThreadLocal，但doFinally在boundedElastic线程执行，
 * 无法清理Netty线程的残留。本filter在请求入口（Netty线程）先清理上一次请求的残留，
 * 在请求出口的doFinally中再次清理执行线程的残留。
 * </p>
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
        // 进入filter时（Netty线程）清理上一次请求可能残留的DataScope ThreadLocal
        DataScopeUtils.context.remove();
        ServletUtils.setExchange(exchange);
        return chain.filter(exchange)
                .doFinally(signal -> {
                    // 请求结束时清理当前线程的ThreadLocal
                    ServletUtils.removeExchange();
                    DataScopeUtils.context.remove();
                });
    }

    private boolean isExcludedPath(String path) {
        return path.startsWith("/actuator") || path.startsWith("/health");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

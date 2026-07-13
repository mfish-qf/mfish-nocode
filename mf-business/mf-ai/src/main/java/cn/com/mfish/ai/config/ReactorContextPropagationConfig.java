package cn.com.mfish.ai.config;

import cn.com.mfish.common.core.utils.ServletUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.scheduler.Schedulers;

/**
 * @description: Reactor上下文传播配置
 * 解决WebFlux响应式编程中subscribeOn/publishOn切换线程导致ThreadLocal丢失的问题
 * <p>
 * 支持两种运行模式：
 * 1. WebFlux模式：传播 ServerWebExchange 的 ThreadLocal
 * 2. Servlet单实例模式：传播 RequestContextHolder 的 ThreadLocal
 * </p>
 * @author: mfish
 * @date: 2026/7/6
 */
@Configuration
public class ReactorContextPropagationConfig {
    private static final String SCHEDULE_HOOK_KEY = "mfish.context.propagation";

    @PostConstruct
    public void init() {
        Schedulers.onScheduleHook(SCHEDULE_HOOK_KEY, runnable -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServerWebExchange exchange = ServletUtils.getExchange();
            if (requestAttributes == null && exchange == null) {
                return runnable;
            }
            return () -> {
                if (requestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                }
                if (exchange != null) {
                    ServletUtils.setExchange(exchange);
                }
                try {
                    runnable.run();
                } finally {
                    if (requestAttributes != null) {
                        RequestContextHolder.resetRequestAttributes();
                    }
                    if (exchange != null) {
                        ServletUtils.removeExchange();
                    }
                }
            };
        });
    }
}
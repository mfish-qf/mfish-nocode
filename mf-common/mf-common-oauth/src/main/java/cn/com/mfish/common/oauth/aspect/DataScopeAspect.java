package cn.com.mfish.common.oauth.aspect;

import cn.com.mfish.common.core.annotation.GlobalException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.DataScopes;
import cn.com.mfish.common.oauth.common.DataScopeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 数据范围切面（兼容Servlet与WebFlux双栈，支持Mono/Flux响应式返回值）
 * <p>
 * 响应式返回值下，ThreadLocal的跨线程传播由 {@link cn.com.mfish.common.oauth.config.DataScopeContextPropagationConfig}
 * 通过 Schedulers.onScheduleHook 自动处理。本切面在订阅发起线程（通常是Netty IO）设置ThreadLocal，
 * doFinally在Mono完成时清理；onScheduleHook负责在subscribeOn切线程时把ThreadLocal传播到执行线程。
 * </p>
 * <p>
 * 与早期"捕获后清理"方案的区别：onScheduleHook只传播不清理，ThreadLocal的清理统一由
 * Aspect的doFinally负责，避免多级subscribeOn链中中间线程清理导致断链。
 * </p>
 * @author: mfish
 * @date: 2024/4/25
 */
@Aspect
@Component
@GlobalException
@Slf4j
public class DataScopeAspect {

    @Around("@annotation(cn.com.mfish.common.oauth.annotation.DataScopes)||@annotation(cn.com.mfish.common.oauth.annotation.DataScope)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 内部Feign请求不进行数据权限限制
        if (AuthInfoUtils.isInnerRequest()) {
            return joinPoint.proceed();
        }
        List<DataScope> list = resolveDataScopes(joinPoint);

        // 在订阅发起线程（通常是Netty IO）设置ThreadLocal
        // onScheduleHook会在subscribeOn调度时捕获并传播到执行线程（不清理原线程，由doFinally统一清理）
        DataScopeUtils.context.set(list);
        try {
            Object result = joinPoint.proceed();
            if (result instanceof Mono<?> mono) {
                return mono.doFinally(s -> DataScopeUtils.context.remove());
            }
            if (result instanceof Flux<?> flux) {
                return flux.doFinally(s -> DataScopeUtils.context.remove());
            }
            // 同步路径：直接清理
            DataScopeUtils.context.remove();
            return result;
        } catch (Throwable e) {
            DataScopeUtils.context.remove();
            throw e;
        }
    }

    private List<DataScope> resolveDataScopes(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        List<DataScope> list = new ArrayList<>();
        if (method.isAnnotationPresent(DataScopes.class)) {
            DataScopes dataScopes = method.getAnnotation(DataScopes.class);
            list.addAll(Arrays.asList(dataScopes.value()));
        } else {
            list.add(method.getAnnotation(DataScope.class));
        }
        return list;
    }
}

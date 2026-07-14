package cn.com.mfish.common.oauth.aspect;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;
import cn.com.mfish.common.oauth.common.OauthUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Method;

/**
 * @author: mfish
 * @description: 权限切面处理（兼容Servlet与WebFlux双栈，支持Mono/Flux响应式返回值）
 * <p>
 * 权限验证应优先于数据范围（DataScope）执行：无权限的请求直接拒绝，不进入数据权限处理。
 * </p>
 * <p>
 * WebFlux环境下，权限校验中的Feign阻塞调用不能在Netty IO线程执行。
 * 对Mono/Flux返回值，用 Mono.defer(() -> { 校验; return mono; }).subscribeOn(boundedElastic)
 * 将校验推迟到弹性线程池。
 * </p>
 * <p>
 * 关键设计：用defer而非fromCallable+then。
 * .then()在上游Mono完成后才订阅下游，此时onScheduleHook的wrapper已执行finally清理了
 * DataScope ThreadLocal，导致下游调度点捕获到null（断链）。
 * defer在订阅时同步执行lambda，校验和业务Mono订阅在同一个wrapper.run()内完成，
 * finally不会在中间执行，DataScope传播不断链。
 * </p>
 * <p>
 * 此设计使权限验证切面可安全地在DataScope外层执行（权限优先），也可在任意顺序叠加。
 * </p>
 * @date: 2022/12/5 18:04
 */
@Aspect
@Component
@Slf4j
public class AuthorizationAspect {

    @Around("@annotation(cn.com.mfish.common.oauth.annotation.RequiresPermissions)" +
            "||@annotation(cn.com.mfish.common.oauth.annotation.RequiresRoles)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 内部Feign请求不验证按钮权限（仅读header，非阻塞，可在Netty线程执行）
        if (AuthInfoUtils.isInnerRequest()) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();
        // 响应式返回值：将权限校验推迟到boundedElastic线程执行
        // 关键：用defer而非fromCallable+then，确保权限校验与业务Mono在同一个subscribeOn调度内执行
        // .then()会在上游Mono完成后才订阅下游，此时wrapper的finally已清理dataScope，导致断链
        // defer在订阅时同步执行lambda，权限校验和业务Mono订阅在同一个wrapper.run()内完成，finally不会在中间执行
        if (result instanceof Mono<?> mono) {
            return Mono.defer(() -> {
                        checkAuthorization(joinPoint);
                        return mono;
                    })
                    .subscribeOn(Schedulers.boundedElastic());
        }
        if (result instanceof Flux<?> flux) {
            return Flux.defer(() -> {
                        checkAuthorization(joinPoint);
                        return flux;
                    })
                    .subscribeOn(Schedulers.boundedElastic());
        }
        // 同步路径（Servlet环境）：直接校验
        checkAuthorization(joinPoint);
        return result;
    }

    /**
     * 执行权限/角色校验
     * 校验不通过时抛出MyRuntimeException，由全局异常处理器捕获
     */
    private void checkAuthorization(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(RequiresPermissions.class)) {
            if (!OauthUtils.checkPermission(method.getAnnotation(RequiresPermissions.class))) {
                throw new MyRuntimeException("错误:该用户无此操作权限");
            }
        }
        if (method.isAnnotationPresent(RequiresRoles.class)) {
            if (!OauthUtils.checkRoles(method.getAnnotation(RequiresRoles.class))) {
                throw new MyRuntimeException("错误:该角色无此操作访问");
            }
        }
    }
}

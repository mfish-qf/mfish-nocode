package cn.com.mfish.common.api.aspect;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;
import cn.com.mfish.common.oauth.common.OauthUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author: mfish
 * @description: 单实例模式下Boot*Service权限校验切面
 * <p>
 * 单实例模式下Feign接口直接调用Boot*Service实现，不经过Controller层，
 * AuthorizationAspect无法触发。此切面拦截所有Boot*Service方法调用，
 * 根据origin判断：inner放行，ai需校验权限。
 * <p>
 * 权限注解查找：通过ControllerMethodResolver，用Feign接口方法的HTTP路径
 * 反查到对应Controller方法，获取其上的@RequiresPermissions/@RequiresRoles注解。
 * @date: 2026/07/15
 */
@Slf4j
@Aspect
@Component
public class BootApiAuthAspect {

    private final ControllerMethodResolver controllerMethodResolver;

    public BootApiAuthAspect(ControllerMethodResolver controllerMethodResolver) {
        this.controllerMethodResolver = controllerMethodResolver;
    }

    @Around("execution(* cn.com.mfish.common.api..*.*(..))")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String origin = extractOrigin(joinPoint);
        // 内部请求直接放行
        if (RPCConstants.INNER.equals(origin)) {
            return joinPoint.proceed();
        }
        // AI工具调用需校验权限
        if (RPCConstants.AI.equals(origin)) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            checkAuthorization(method, joinPoint.getTarget().getClass());
        }
        return joinPoint.proceed();
    }

    /**
     * 从方法参数中提取origin（Remote*Service接口方法的第一个参数均为String origin）
     */
    private String extractOrigin(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0 && args[0] instanceof String) {
            return (String) args[0];
        }
        return null;
    }

    /**
     * 权限校验：通过Feign接口方法的HTTP路径反查Controller方法上的注解
     */
    private void checkAuthorization(Method method, Class<?> targetClass) {
        // 找到Feign接口方法（Boot*Service实现的方法对应Remote*Service接口方法）
        Method feignMethod = resolveFeignMethod(method, targetClass);
        if (feignMethod == null) return;
        // 通过ControllerMethodResolver反查Controller方法上的注解
        RequiresPermissions rp = controllerMethodResolver.findAnnotationFromController(feignMethod, RequiresPermissions.class);
        if (rp != null && !OauthUtils.checkPermission(rp)) {
            throw new MyRuntimeException("错误:该用户无此操作权限");
        }
        RequiresRoles rr = controllerMethodResolver.findAnnotationFromController(feignMethod, RequiresRoles.class);
        if (rr != null && !OauthUtils.checkRoles(rr)) {
            throw new MyRuntimeException("错误:该角色无此操作访问");
        }
    }

    /**
     * 从实现类方法找到对应的Feign接口方法
     */
    private Method resolveFeignMethod(Method implMethod, Class<?> targetClass) {
        for (Class<?> iface : targetClass.getInterfaces()) {
            try {
                return iface.getDeclaredMethod(implMethod.getName(), implMethod.getParameterTypes());
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }
}

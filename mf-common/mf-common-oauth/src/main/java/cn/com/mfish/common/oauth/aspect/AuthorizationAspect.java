package cn.com.mfish.common.oauth.aspect;

import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author ：qiufeng
 * @description：权限切面处理
 * @date ：2022/12/5 18:04
 */
@Aspect
@Component
@Slf4j
public class AuthorizationAspect {
    @Before("@annotation(cn.com.mfish.common.oauth.annotation.RequiresPermissions)||@annotation(cn.com.mfish.common.oauth.annotation.RequiresRoles)")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(RequiresPermissions.class)) {
            RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
            System.out.println(requiresPermissions.value()+"  RequiresPermissions");
        }
        if (method.isAnnotationPresent(RequiresRoles.class)) {
            System.out.println("RequiresRoles");
        }
    }

}

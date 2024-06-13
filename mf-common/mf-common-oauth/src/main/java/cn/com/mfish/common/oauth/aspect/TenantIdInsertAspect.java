package cn.com.mfish.common.oauth.aspect;

import cn.com.mfish.common.core.annotation.GlobalException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author zibo
 * @date 2024/6/13 上午10:22
 * @slogan 慢慢学，不要停。
 */
@Aspect
@Component
@GlobalException
@Slf4j
public class TenantIdInsertAspect {

    @SneakyThrows
    @Before("@annotation(cn.com.mfish.common.oauth.annotation.TenantIdInsert)")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 修改接收到的参数
        Parameter[] parameters = method.getParameters();
        // 获取当前租户ID
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        // 设置租户ID到参数
        if (parameters.length > 0 && parameters[0].getType().isInstance(joinPoint.getArgs()[0])) {
            // 获取第一个参数的实际值（目标对象）
            Object paramValue = joinPoint.getArgs()[0];
            // 获取设置租户ID的方法
            Method setterMethod = parameters[0].getType().getMethod("setTenantId", parameters[0].getType().getDeclaredField("tenantId").getType());
            // 使用setter方法设置新值
            setterMethod.invoke(paramValue, tenantId);
        }
    }

    @AfterThrowing(value = "@annotation(cn.com.mfish.common.oauth.annotation.TenantIdInsert)", throwing = "e")
    public void doAfterThrowing(Throwable e) {
        log.error("doAfterThrowing", e);
    }
}

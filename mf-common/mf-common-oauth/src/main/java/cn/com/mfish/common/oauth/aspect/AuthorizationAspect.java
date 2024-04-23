package cn.com.mfish.common.oauth.aspect;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;
import cn.com.mfish.common.oauth.common.OauthUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author: mfish
 * @description: 权限切面处理
 * @date: 2022/12/5 18:04
 */
@Aspect
@Component
@Slf4j
public class AuthorizationAspect {
    @Before("@annotation(cn.com.mfish.common.oauth.annotation.RequiresPermissions)" +
            "||@annotation(cn.com.mfish.common.oauth.annotation.RequiresRoles)")
    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            throw new MyRuntimeException("错误:未获取到请求信息");
        }
        String source = request.getHeader(RPCConstants.REQ_ORIGIN);
        //判断如果是内部Feign接口请求不需要验证按钮权限
        if (!StringUtils.isEmpty(source) && source.equals(RPCConstants.INNER)) {
            return;
        }
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

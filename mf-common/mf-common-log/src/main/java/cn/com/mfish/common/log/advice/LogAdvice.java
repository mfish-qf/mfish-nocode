package cn.com.mfish.common.log.advice;

import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.utils.AuthUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.oauth.model.UserInfo;
import cn.com.mfish.oauth.remote.RemoteUserService;
import cn.com.mfish.sys.api.entity.SysLog;
import cn.com.mfish.sys.api.remote.RemoteLogService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ：qiufeng
 * @description：日志记录切面
 * @date ：2022/9/4 12:05
 */
@Aspect
@Component
@Slf4j
public class LogAdvice {
    ThreadLocal<SysLog> ssoLogThreadLocal = new ThreadLocal<>();
    @Resource
    RemoteLogService remoteLogService;
    @Resource
    RemoteUserService remoteUserService;

    @Before("@annotation(cn.com.mfish.common.log.annotation.Log)")
    public void doBefore(JoinPoint joinPoint) {
        SysLog sysLog = new SysLog();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        sysLog.setOperIp(AuthUtils.getRemoteIP(request));
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String method;
        Log aLog = methodSignature.getMethod().getDeclaredAnnotation(Log.class);
        method = aLog.title();
        if (StringUtils.isEmpty(method)) {
            ApiOperation apiOperation = methodSignature.getMethod().getDeclaredAnnotation(ApiOperation.class);
            if (apiOperation != null) {
                method = apiOperation.value();
            } else {
                method = methodSignature.getName();
            }
        }
        sysLog.setMethod(method);
        ssoLogThreadLocal.set(sysLog);
    }

    @AfterReturning(value = "@annotation(cn.com.mfish.common.log.annotation.Log)", returning = "returnValue")
    public void doAfterReturning(Object returnValue) {
        setReturn(0, JSON.toJSONString(returnValue));
    }

    @AfterThrowing(value = "@annotation(cn.com.mfish.common.log.annotation.Log)", throwing = "e")
    public void doAfterThrowing(Throwable e) {
        setReturn(1, e.getMessage());
    }

    private void setReturn(int state, String remark) {
        SysLog sysLog = ssoLogThreadLocal.get();
        Result<UserInfo> user = remoteUserService.getUserInfo(CredentialConstants.INNER);
        if(user.isSuccess()){
            sysLog.setOperName(user.getData().getAccount());
        }
        remoteLogService.add(CredentialConstants.INNER, sysLog);
    }
}

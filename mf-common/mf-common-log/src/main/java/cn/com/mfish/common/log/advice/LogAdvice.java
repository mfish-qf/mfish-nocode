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
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

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
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String title;
        Log aLog = methodSignature.getMethod().getDeclaredAnnotation(Log.class);
        title = aLog.title();
        if (StringUtils.isEmpty(title)) {
            ApiOperation apiOperation = methodSignature.getMethod().getDeclaredAnnotation(ApiOperation.class);
            if (apiOperation != null) {
                title = apiOperation.value();
            } else {
                title = methodSignature.getName();
            }
        }
        SysLog sysLog = new SysLog();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        sysLog.setReqUri(request.getRequestURI());
        sysLog.setReqType(request.getMethod());
        sysLog.setReqParam(argsArrayToString(joinPoint.getArgs()));
        sysLog.setReqSource(aLog.reqSource().getValue());
        sysLog.setOperType(aLog.operateType().getValue());
        sysLog.setOperIp(AuthUtils.getRemoteIP(request));
        sysLog.setTitle(title);
        sysLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
        ssoLogThreadLocal.set(sysLog);
    }


    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (StringUtils.isNotNull(o) && !isFilterObject(o)) {
                    params += JSON.toJSONString(o);
                }
            }
        }
        return params.trim();
    }

    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
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
        if (user.isSuccess()) {
            sysLog.setOperName(user.getData().getAccount());
        }
        sysLog.setOperTime(new Date());
        sysLog.setOperStatus(state);
        sysLog.setRemark(remark.substring(0,2000));
        remoteLogService.add(CredentialConstants.INNER, sysLog);
    }
}

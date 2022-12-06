package cn.com.mfish.common.log.aspect;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.log.service.AsyncSaveLog;
import cn.com.mfish.sys.api.entity.SysLog;
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
import java.util.Date;

/**
 * @author ：qiufeng
 * @description：日志记录切面
 * @date ：2022/9/4 12:05
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    ThreadLocal<SysLog> logThreadLocal = new ThreadLocal<>();
    @Resource
    AsyncSaveLog asyncSaveLog;

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
        sysLog.setReqParam(getParams(joinPoint.getArgs()));
        sysLog.setReqSource(aLog.reqSource().getValue());
        sysLog.setOperType(aLog.operateType().getValue());
        sysLog.setOperIp(Utils.getRemoteIP(request));
        sysLog.setTitle(title);
        sysLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
        logThreadLocal.set(sysLog);
    }


    private String getParams(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object obj : paramsArray) {
                if (StringUtils.isNull(obj)) {
                    continue;
                }
                if (obj instanceof String) {
                    params += "," + obj;
                    continue;
                }
                if (obj instanceof HttpServletRequest) {
                    HttpServletRequest request = (HttpServletRequest) obj;
                    obj = request.getParameterMap();
                }
                try {
                    params += "," + JSON.toJSONString(obj);
                } catch (Exception ex) {
                    log.error("参数转json出错", ex);
                    params += obj.toString();
                }
            }
        }
        if (StringUtils.isEmpty(params)) {
            return params;
        }
        return params.substring(1);
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
        SysLog sysLog = logThreadLocal.get();
        sysLog.setOperName(AuthInfoUtils.getCurrentAccount());
        sysLog.setOperTime(new Date());
        sysLog.setOperStatus(state);
        sysLog.setRemark(StringUtils.substring(remark, 0, 2000));
        asyncSaveLog.saveLog(sysLog);
    }
}

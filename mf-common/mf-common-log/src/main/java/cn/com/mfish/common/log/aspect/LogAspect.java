package cn.com.mfish.common.log.aspect;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.log.service.AsyncSaveLog;
import cn.com.mfish.sys.api.entity.SysLog;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author: mfish
 * @description: 日志记录切面
 * @date: 2022/9/4 12:05
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
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String origin = request.getHeader(RPCConstants.REQ_ORIGIN);
        if (!StringUtils.isEmpty(origin) && origin.equals(RPCConstants.INNER)) {
            //内部请求不记录日志
            return;
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String title;
        Log aLog = methodSignature.getMethod().getDeclaredAnnotation(Log.class);
        title = aLog.title();
        if (StringUtils.isEmpty(title)) {

            Operation apiOperation = methodSignature.getMethod().getDeclaredAnnotation(Operation.class);
            if (apiOperation != null) {
                title = apiOperation.description();
            } else {
                title = methodSignature.getName();
            }
        }
        SysLog sysLog = new SysLog();
        sysLog.setReqUri(request.getRequestURI());
        sysLog.setReqType(request.getMethod());
        sysLog.setReqParam(getParams(joinPoint.getArgs()));
        sysLog.setReqSource(aLog.reqSource().getValue());
        sysLog.setOperType(aLog.operateType().toString());
        sysLog.setOperIp(Utils.getRemoteIP(request));
        sysLog.setTitle(title);
        sysLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
        logThreadLocal.set(sysLog);
    }

    /**
     * 获取请求参数并格式化为字符串
     *
     * @param paramsArray 请求参数数组，包含可能的各种类型参数
     * @return 格式化后的参数字符串，如果没有参数则返回空字符串
     */
    private String getParams(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object obj : paramsArray) {
                if (null == obj || obj instanceof Map && ((Map<?, ?>) obj).isEmpty() || obj instanceof HttpServletResponse) {
                    continue;
                }
                if (obj instanceof String) {
                    params.append(",").append(obj);
                    continue;
                }
                if (obj instanceof HttpServletRequest request) {
                    obj = request.getParameterMap();
                }
                try {
                    params.append(",").append(JSON.toJSONString(obj));
                } catch (Exception ex) {
                    log.error("参数转json出错 {} {}", obj, ex.getMessage(), ex);
                    params.append(obj.toString());
                }
            }
        }
        if (StringUtils.isEmpty(params.toString())) {
            return params.toString();
        }
        return params.substring(1);
    }

    @AfterReturning(value = "@annotation(cn.com.mfish.common.log.annotation.Log)", returning = "returnValue")
    public void doAfterReturning(Object returnValue) {
        setReturn(0, JSON.toJSONString(returnValue));
    }

    @AfterThrowing(value = "@annotation(cn.com.mfish.common.log.annotation.Log)", throwing = "e")
    public void doAfterThrowing(Throwable e) {
        StackTraceElement[] elements = e.getStackTrace();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", e.toString());
        if (elements.length > 0) {
            jsonObject.put("stack", elements[0].toString());
        }
        setReturn(1, jsonObject.toJSONString());
    }

    private void setReturn(int state, String remark) {
        SysLog sysLog = logThreadLocal.get();
        if (sysLog == null) {
            return;
        }
        try {
            sysLog.setCreateBy(AuthInfoUtils.getCurrentAccount());
            sysLog.setCreateTime(new Date());
            sysLog.setOperStatus(state);
            sysLog.setRemark(remark);
            asyncSaveLog.saveLog(sysLog);
        } finally {
            logThreadLocal.remove();
        }
    }
}

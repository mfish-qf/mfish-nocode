package cn.com.mfish.common.log.aspect;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
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
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Map;

/**
 * @author: mfish
 * @description: 日志记录切面（兼容Servlet与WebFlux双栈，支持Mono/Flux响应式返回值）
 * @date: 2022/9/4 12:05
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    @Resource
    AsyncSaveLog asyncSaveLog;

    /**
     * 环绕通知，记录请求信息并在方法执行完成后记录日志
     * 对响应式返回值（Mono/Flux），在管线完成或出错时记录，避免在publisher返回瞬间误记
     */
    @Around("@annotation(cn.com.mfish.common.log.annotation.Log)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 内部Feign请求不记录日志
        String origin = ServletUtils.getHeader(RPCConstants.REQ_ORIGIN);
        if (RPCConstants.INNER.equals(origin)) {
            return joinPoint.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Log aLog = methodSignature.getMethod().getDeclaredAnnotation(Log.class);
        SysLog sysLog = buildSysLog(joinPoint, methodSignature, aLog);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 同步路径异常
            completeLog(sysLog, e, 1);
            throw e;
        }

        // 响应式返回值：在管线真正完成或出错时记录
        if (result instanceof Mono<?> mono) {
            return mono
                    .doOnSuccess(v -> completeLog(sysLog, v, 0))
                    .doOnError(e -> completeLog(sysLog, e, 1));
        }
        if (result instanceof Flux<?> flux) {
            return flux
                    .doOnComplete(() -> completeLog(sysLog, null, 0))
                    .doOnError(e -> completeLog(sysLog, e, 1));
        }
        // 同步路径成功
        completeLog(sysLog, result, 0);
        return result;
    }

    private SysLog buildSysLog(ProceedingJoinPoint joinPoint, MethodSignature methodSignature, Log aLog) {
        String title = aLog.title();
        if (StringUtils.isEmpty(title)) {
            Operation apiOperation = methodSignature.getMethod().getDeclaredAnnotation(Operation.class);
            if (apiOperation != null) {
                title = apiOperation.description();
            } else {
                title = methodSignature.getName();
            }
        }
        SysLog sysLog = new SysLog();
        sysLog.setReqUri(ServletUtils.getRequestURI());
        sysLog.setReqType(ServletUtils.getMethod());
        sysLog.setReqParam(getParams(joinPoint.getArgs()));
        sysLog.setReqSource(aLog.reqSource().getValue());
        sysLog.setOperType(aLog.operateType().toString());
        sysLog.setOperIp(ServletUtils.getRemoteIP());
        sysLog.setTitle(title);
        sysLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
        return sysLog;
    }

    /**
     * 获取请求参数并格式化为字符串
     * 跳过Servlet/Reactive的请求与响应对象，避免序列化无意义数据
     */
    private String getParams(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object obj : paramsArray) {
                if (null == obj
                        || obj instanceof HttpServletResponse
                        || obj instanceof ServerHttpResponse
                        || obj instanceof Map && ((Map<?, ?>) obj).isEmpty()) {
                    continue;
                }
                if (obj instanceof String) {
                    params.append(",").append(obj);
                    continue;
                }
                if (obj instanceof HttpServletRequest request) {
                    obj = request.getParameterMap();
                } else if (obj instanceof ServerHttpRequest request) {
                    obj = request.getQueryParams();
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

    /**
     * 完成日志记录
     *
     * @param sysLog      日志对象
     * @param returnValue 返回值（state=0时为方法返回值，state=1时为Throwable异常）
     * @param state       0正常 1异常
     */
    private void completeLog(SysLog sysLog, Object returnValue, int state) {
        try {
            String remark;
            if (state == 0) {
                remark = returnValue == null ? "" : JSON.toJSONString(returnValue);
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("error", returnValue.toString());
                StackTraceElement[] elements = ((Throwable) returnValue).getStackTrace();
                if (elements.length > 0) {
                    jsonObject.put("stack", elements[0].toString());
                }
                remark = jsonObject.toJSONString();
            }
            sysLog.setCreateBy(AuthInfoUtils.getCurrentAccount());
            sysLog.setCreateTime(new Date());
            sysLog.setOperStatus(state);
            sysLog.setRemark(remark);
            asyncSaveLog.saveLog(sysLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
}

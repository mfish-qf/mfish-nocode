package cn.com.mfish.common.prom.aspect;

import cn.com.mfish.common.core.annotation.GlobalException;
import cn.com.mfish.common.prom.annotation.MetricsMonitor;
import cn.com.mfish.common.prom.annotation.MetricsMonitors;
import cn.com.mfish.common.prom.common.PromClientUtils;
import cn.com.mfish.common.prom.enums.MetricEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description: 数据范围切面
 * @author: mfish
 * @date: 2024/4/25
 */
@Aspect
@Component
@GlobalException
@Slf4j
public class MetricsAspect {
    @Getter
    static class MetricsInfo {
        List<MetricsMonitor> metricsMonitorList;
        Date startTime;

        public MetricsInfo(List<MetricsMonitor> metricsMonitorList, Date date) {
            this.metricsMonitorList = metricsMonitorList;
            this.startTime = date;
        }
    }

    ThreadLocal<MetricsInfo> durationThreadLocal = new ThreadLocal<>();

    /**
     * 方法执行前增加指标值
     *
     * @param joinPoint 连接点
     */
    @Before("@annotation(cn.com.mfish.common.prom.annotation.MetricsMonitor)||@annotation(cn.com.mfish.common.prom.annotation.MetricsMonitors)")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        List<MetricsMonitor> list = new ArrayList<>();
        if (method.isAnnotationPresent(MetricsMonitors.class)) {
            MetricsMonitors metricsMonitors = method.getAnnotation(MetricsMonitors.class);
            list.addAll(Arrays.asList(metricsMonitors.value()));
        } else {
            list.add(method.getAnnotation(MetricsMonitor.class));
        }
        durationThreadLocal.set(new MetricsInfo(list, new Date()));
    }

    @AfterReturning("@annotation(cn.com.mfish.common.prom.annotation.MetricsMonitor)||@annotation(cn.com.mfish.common.prom.annotation.MetricsMonitors)")
    public void doAfterReturning() {
        MetricsInfo metricsInfo = durationThreadLocal.get();
        List<MetricsMonitor> list = metricsInfo.getMetricsMonitorList();
        for (MetricsMonitor metricsMonitor : list) {
            MetricEnum metricEnum = metricsMonitor.metricEnum();
            String[] tagValues = metricsMonitor.tagValues();
            switch (metricEnum) {
                case MFISH_REQUEST_COUNT:
                    PromClientUtils.increment(metricEnum, tagValues);
                    break;
                case MFISH_REQUEST_DURATION:
                    long duration = new Date().getTime() - metricsInfo.getStartTime().getTime();
                    PromClientUtils.setValue(metricEnum, duration, tagValues);
                    break;
            }
        }
    }

    /**
     * 异常处理
     *
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(cn.com.mfish.common.prom.annotation.MetricsMonitor)||@annotation(cn.com.mfish.common.prom.annotation.MetricsMonitors)", throwing = "e")
    public void doAfterThrowing(Throwable e) {
        log.error("doAfterThrowing", e);
    }
}

package cn.com.mfish.common.prom.aspect;

import cn.com.mfish.common.core.annotation.GlobalException;
import cn.com.mfish.common.prom.annotation.MetricsMonitor;
import cn.com.mfish.common.prom.annotation.MetricsMonitors;
import cn.com.mfish.common.prom.common.PromClientUtils;
import cn.com.mfish.common.prom.enums.MetricEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 指标监控切面（兼容Servlet与WebFlux双栈，支持Mono/Flux响应式返回值）
 * <p>
 * 原实现用 @Before + @AfterReturning + ThreadLocal，在WebFlux下存在三个问题：
 * 1. @AfterReturning在方法返回Mono/Flux瞬间触发，此时响应式链尚未订阅执行，
 *    duration记录的是"构建Mono/Flux链的时间"（微秒级），而非请求真正执行时间
 * 2. count在Mono/Flux订阅前就+1，若订阅后LLM调用失败，失败请求被计为成功
 * 3. Mono/Flux订阅后的异常不会被@AfterThrowing捕获，ThreadLocal不清理
 * </p>
 * <p>
 * 改用@Around后：
 * - 同步返回值：与原@Before+@AfterReturning行为一致
 * - Mono/Flux返回值：用doOnSuccess/doOnError/doFinally在响应式链真正完成时记录指标，
 *   duration测量的是从切面进入到响应式链完成的真实耗时，count在成功时才+1
 * - 去掉ThreadLocal，startTime作为局部变量捕获到lambda中，无线程残留风险
 * </p>
 * @author: mfish
 * @date: 2024/4/25
 */
@Aspect
@Component
@GlobalException
@Slf4j
public class MetricsAspect {

    @Around("@annotation(cn.com.mfish.common.prom.annotation.MetricsMonitor)||@annotation(cn.com.mfish.common.prom.annotation.MetricsMonitors)")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        List<MetricsMonitor> list = resolveMetricsMonitors(joinPoint);
        long startTime = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 同步路径异常：记录失败指标
            recordMetrics(list, startTime, false);
            throw e;
        }

        // 响应式返回值：在管线真正完成或出错时记录
        if (result instanceof Mono<?> mono) {
            return mono
                    .doOnSuccess(v -> recordMetrics(list, startTime, true))
                    .doOnError(e -> recordMetrics(list, startTime, false));
        }
        if (result instanceof Flux<?> flux) {
            return flux
                    .doOnComplete(() -> recordMetrics(list, startTime, true))
                    .doOnError(e -> recordMetrics(list, startTime, false));
        }
        // 同步路径成功
        recordMetrics(list, startTime, true);
        return result;
    }

    /**
     * 记录指标
     *
     * @param list      指标列表
     * @param startTime 开始时间（毫秒）
     * @param success   是否成功（true成功 false失败）
     */
    private void recordMetrics(List<MetricsMonitor> list, long startTime, boolean success) {
        long duration = System.currentTimeMillis() - startTime;
        for (MetricsMonitor metricsMonitor : list) {
            try {
                MetricEnum metricEnum = metricsMonitor.metricEnum();
                String[] tagValues = metricsMonitor.tagValues();
                switch (metricEnum) {
                    case MFISH_REQUEST_COUNT:
                        // 仅成功时计数，失败不计入请求成功数
                        if (success) {
                            PromClientUtils.increment(metricEnum, tagValues);
                        }
                        break;
                    case MFISH_REQUEST_DURATION:
                        PromClientUtils.setValue(metricEnum, duration, tagValues);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                log.error("记录指标失败", e);
            }
        }
    }

    private List<MetricsMonitor> resolveMetricsMonitors(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        List<MetricsMonitor> list = new ArrayList<>();
        if (method.isAnnotationPresent(MetricsMonitors.class)) {
            MetricsMonitors metricsMonitors = method.getAnnotation(MetricsMonitors.class);
            list.addAll(Arrays.asList(metricsMonitors.value()));
        } else {
            list.add(method.getAnnotation(MetricsMonitor.class));
        }
        return list;
    }
}

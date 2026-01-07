package cn.com.mfish.common.prom.annotation;

import cn.com.mfish.common.prom.enums.MetricEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 指标监控
 * @author: mfish
 * @date: 2026/1/6
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetricsMonitor {
    /**
     * 指标枚举
     */
    MetricEnum metricEnum() default MetricEnum.UNKNOWN;

    /**
     * 标签值
     */
    String[] tagValues() default {};
}

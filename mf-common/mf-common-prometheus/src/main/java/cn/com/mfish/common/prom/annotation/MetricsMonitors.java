package cn.com.mfish.common.prom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 指标监控组合注解
 * @author: mfish
 * @date: 2026/1/6
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetricsMonitors {
    MetricsMonitor[] value();
}

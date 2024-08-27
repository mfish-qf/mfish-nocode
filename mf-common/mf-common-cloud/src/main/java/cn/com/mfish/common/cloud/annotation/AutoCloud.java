package cn.com.mfish.common.cloud.annotation;

import cn.com.mfish.common.web.annotation.AutoWeb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: web应用微服务自动注解
 * @author: mfish
 * @date: 2024/8/27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoFeignClients
@AutoWeb
public @interface AutoCloud {
}

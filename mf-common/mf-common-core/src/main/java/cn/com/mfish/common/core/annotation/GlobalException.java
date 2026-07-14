package cn.com.mfish.common.core.annotation;

import cn.com.mfish.common.core.advice.ExceptionHandlerAdvice;
import cn.com.mfish.common.core.advice.ReactiveExceptionHandlerAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: mfish
 * @description: 全局异常处理注解
 * <p>
 * 同时导入Servlet与WebFlux两套异常处理器，由 @ConditionalOnWebApplication 自动按环境生效。
 * </p>
 * @date: 2021/12/13 18:32
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ExceptionHandlerAdvice.class, ReactiveExceptionHandlerAdvice.class})
public @interface GlobalException {
    String value() default "";
}

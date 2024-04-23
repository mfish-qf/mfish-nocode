package cn.com.mfish.common.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: mfish
 * @description: 标记为内部接口
 * @date: 2021/12/3 20:16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InnerUser {
    //内部请求是否校验用户 默认:false不校验
    boolean validateUser() default false;
}

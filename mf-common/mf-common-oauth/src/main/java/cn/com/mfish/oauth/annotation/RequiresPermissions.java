package cn.com.mfish.oauth.annotation;

import cn.com.mfish.oauth.common.Logical;

import java.lang.annotation.*;

/**
 * @author ：qiufeng
 * @description：权限校验注解
 * @date ：2022/12/5 17:48
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    String[] value();

    Logical logical() default Logical.AND;
}
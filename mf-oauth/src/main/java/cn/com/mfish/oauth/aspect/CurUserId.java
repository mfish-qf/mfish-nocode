package cn.com.mfish.oauth.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ：qiufeng
 * @description：当前用户ID
 * @date ：2021/12/9 11:22
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurUserId {
    String value() default "";
}

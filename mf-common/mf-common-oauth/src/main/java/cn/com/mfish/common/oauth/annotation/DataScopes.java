package cn.com.mfish.common.oauth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 数据范围组（多个组之间是and条件，即多个条件同时满足）
 * @author: mfish
 * @date: 2024/4/28
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScopes {
    DataScope[] value();
}

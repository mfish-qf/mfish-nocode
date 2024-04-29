package cn.com.mfish.common.ds.annotation;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: mfish
 * @description: 主库数据源配置注解
 * @date: 2021/11/30 11:42
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@DS("master")
public @interface Master {
}

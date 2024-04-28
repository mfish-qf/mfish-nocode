package cn.com.mfish.common.ds.annotation;

import cn.com.mfish.common.ds.common.DataScopeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 数据范围
 * @author: mfish
 * @date: 2024/4/25
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {
    /**
     * 需要进行权限控制的表
     *
     * @return
     */
    String table() default "";

    /**
     * 进行权限控制的类型
     *
     * @return
     */
    DataScopeType type() default DataScopeType.Tenant;

    /**
     * 表进行过滤的字段名称
     * 注意：fieldNames与tables数组值一一对应
     *
     * @return
     */
    String fieldName() default "";

    String value() default "";
}

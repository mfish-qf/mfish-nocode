package cn.com.mfish.common.oauth.annotation;

import cn.com.mfish.common.oauth.common.DataScopeType;

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
     * @return 表名
     */
    String table() default "";

    /**
     * 进行权限控制的类型
     *
     * @return 权限控制类型
     */
    DataScopeType type() default DataScopeType.Tenant;

    /**
     * 表进行过滤的字段名称（不填 采用默认值。例如：租户使用tenant_id）
     *
     * @return 字段名
     */
    String fieldName() default "";

    /**
     * 字段值（不填各权限控制使用默认值，传入优先使用传入值。例如：租户使用当前租户）
     *
     * @return 字段值
     */
    String[] values() default {};
}

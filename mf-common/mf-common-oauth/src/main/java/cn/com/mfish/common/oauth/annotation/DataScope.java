package cn.com.mfish.common.oauth.annotation;

import cn.com.mfish.common.oauth.common.DataScopeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 数据范围
 * 使用方法如下：通过在查询方法上增加@DataScope注解进行数据权限控制
 *     *@DataScopes({
 *             *@DataScope(table = "mf_api_folder", type = DataScopeType.Tenant),
 *             *@DataScope(table = "mf_api", type = DataScopeType.Tenant)
 *     })
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
    DataScopeType type() default DataScopeType.None;

    /**
     * 表进行过滤的字段名称，这个根据具体需要过滤的表建表时名称而定，尽量采用默认值标准命名（不填 采用默认值）
     * 租户：默认值tenant_id
     * 用户：默认值user_id
     * 角色：默认值role_id
     * 组织：默认值org_id
     *
     * @return 字段名
     */
    String fieldName() default "";

    /**
     * 字段值 多个值之间是or条件（不填各权限控制使用默认值，传入优先使用传入值。例如：租户使用当前租户）
     * 用户：传账号名称 例如:admin,mfish
     * 角色：传入角色固定编码 例如:superAdmin,manage
     * 组织:传入组织固定编码 例如:admin
     * 租户:直接传入id（注意:::考虑到租户一般情况下都是查询当前租户数据，所以该值并未过多实现）
     *
     * @return 字段值
     */
    String[] values() default {};
}

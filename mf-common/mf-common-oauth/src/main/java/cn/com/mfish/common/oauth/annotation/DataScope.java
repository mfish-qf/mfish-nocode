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
     * 用户：传账号名称 例如:values={"admin","mfish"}
     * 角色：传入角色固定编码 例如:values={"superAdmin","manage"}
     * 组织:传入组织固定编码 例如:values="admin"
     * 租户:直接传入id（注意:::考虑到租户一般情况下都是查询当前租户数据，所以该值并未过多实现）
     *
     * @return 字段值
     */
    String[] values() default {};

    /**
     * 排除条件 满足排除条件的数据不会被过滤，即会被查询出来 如果是多个条件，多个条件的数据都会被查询出来（不传时不做排除）
     * 例如:exclude={"isPublic=1"}
     * 如果排除值为变量采用#{XXX}格式，例如exclude={"isPublic=#{XXX}"}
     * 变量值可以通过ServletRequest.getParameter中获取，如果变量值为空，则不进行过滤
     * @return 排除条件
     */
    String[] excludes() default {};

    /**
     * 忽略条件（特殊处理，优先级最高） 当前表满足忽略条件时，优先使用忽略条件设置的条件进行过滤
     * 只要一个数据范围中存在忽略条件，其他所有数据范围条件失效
     * 变量值可以通过ServletRequest.getParameter中获取，如果变量值为空，则不使用忽略条件
     * @return 跳过条件
     */
    String[] ignores() default {};
}

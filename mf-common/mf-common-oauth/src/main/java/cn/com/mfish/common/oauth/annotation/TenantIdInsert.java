package cn.com.mfish.common.oauth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于为新增接口的请求参数附加租户ID
 * 注意：
 * 1、请求参数（实体对象）必须有setTenantId方法和tenantId属性；
 * 2、注解初衷是用于为add方法的请求参数附加租户ID，第一个参数必须是要附加租户ID的对象。
 *
 * @author zibo
 * @date 2024/6/13 上午10:20
 * @slogan 慢慢学，不要停。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantIdInsert {
}

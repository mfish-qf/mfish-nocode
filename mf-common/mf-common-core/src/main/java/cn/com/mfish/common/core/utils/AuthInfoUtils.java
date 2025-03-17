package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.utils.http.WebRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author: mfish
 * @description: 认证相关信息，此类中方法不支持异步调用获取
 * @date: 2020/2/12 13:47
 */
@Slf4j
public class AuthInfoUtils {
    /**
     * 超户
     */
    public static final String SUPER_ACCOUNT_ID = "1";
    /**
     * 超户角色ID
     */
    public static final String SUPER_ROLE_ID = "1";

    /**
     * 个人角色ID 三方账号注册默认赋个人角色
     */
    public static final String PERSON_ROLE_ID = "0";
    /**
     * 超户角色编码
     */
    public static final String SUPER_ROLE = "superAdmin";
    /**
     * 系统默认租户
     */
    public static final String SUPER_TENANT_ID = "1";
    /**
     * 系统默认组织
     */
    public static final String SUPER_ORG_ID = "1";

    public static final String ALL_PERMISSION = "*:*:*";

    /**
     * 从请求中获取token值
     * token通过access_token=****直接赋值
     * 或者token放到head中 以Authorization=Bearer******方式传入
     *
     * @param t 请求类型
     * @return 返回token
     */
    public static <T> String getAccessToken(T t) {
        // 头部的Authorization值以Bearer开头
        String auth = WebRequest.getHeader(t, Constants.AUTHENTICATION);
        String accessToken = null;
        if (StringUtils.isNotEmpty(auth) && auth.startsWith(Constants.OAUTH_HEADER_NAME)) {
            accessToken = auth.replaceFirst(Constants.OAUTH_HEADER_NAME, StringUtils.EMPTY).trim();
        }
        // 请求参数中包含access_token参数
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = WebRequest.getParameter(t, Constants.ACCESS_TOKEN);
        }
        return accessToken;
    }

    /**
     * 获取当前accessToken
     *
     * @return 返回token
     */
    public static String getAccessToken() {
        return getAccessToken(ServletUtils.getRequest());
    }

    /**
     * 获取当前用户ID(该方法要在主线程运行)
     *
     * @return 返回用户ID
     */
    public static String getCurrentUserId() {
        return getAttr(RPCConstants.REQ_USER_ID);
    }

    /**
     * 获取当前帐号(该方法要在主线程运行)
     *
     * @return 返回帐号
     */
    public static String getCurrentAccount() {
        return getAttr(RPCConstants.REQ_ACCOUNT);
    }

    /**
     * 获取当前租户ID(该方法要在主线程运行)
     *
     * @return 返回租户ID
     */
    public static String getCurrentTenantId() {
        String tenantId = getAttr(RPCConstants.REQ_TENANT_ID);
        if (StringUtils.isEmpty(tenantId)) {
            throw new OAuthValidateException("错误：未获取到租户信息");
        }
        return tenantId;
    }

    /**
     * 获取属性 先到header中查找，找不到再到attribute中查找
     * 微服务架构用户属性放在header中传递
     * 单实例架构用户属性放在attribute中传递
     *
     * @param attr 属性名称，用于查找header和attribute
     * @return 返回属性值，如果找不到则返回null
     */
    private static String getAttr(String attr) {
        String value = ServletUtils.getHeader(attr);
        if (StringUtils.isEmpty(value)) {
            Object obj = ServletUtils.getAttribute(attr);
            if (obj == null) {
                return null;
            }
            value = obj.toString();
        }
        return StringUtils.isEmpty(value) ? null : value;
    }

    /**
     * 是否超户
     *
     * @return 是否
     */
    public static boolean isSuper() {
        return isSuper(getCurrentUserId());
    }

    /**
     * 是否超户
     *
     * @param userId 用户id
     * @return 是否
     */
    public static boolean isSuper(String userId) {
        return SUPER_ACCOUNT_ID.equals(userId);
    }

    /**
     * 判断角色是否超户角色
     *
     * @param roleId 角色id
     * @return 是否
     */
    public static boolean isSuperRole(String roleId) {
        return SUPER_ROLE_ID.equals(roleId);
    }

    /**
     * 判断是否系统默认租户
     *
     * @param tenantId 租户id
     * @return 是否
     */
    public static boolean isSuperTenant(String tenantId) {
        return SUPER_TENANT_ID.equals(tenantId);
    }

    /**
     * 判断角色组中是否包含超户角色
     *
     * @param roleIds 角色id组
     * @return 是否
     */
    public static boolean isContainSuperAdmin(List<String> roleIds) {
        if (roleIds == null) {
            return false;
        }
        return roleIds.stream().anyMatch(AuthInfoUtils::isSuperRole);
    }

    /**
     * 是否内部请求
     *
     * @return 是否
     */
    public static boolean isInnerRequest() {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            throw new MyRuntimeException("错误:未获取到请求信息");
        }
        String source = request.getHeader(RPCConstants.REQ_ORIGIN);
        return !StringUtils.isEmpty(source) && source.equals(RPCConstants.INNER);
    }
}

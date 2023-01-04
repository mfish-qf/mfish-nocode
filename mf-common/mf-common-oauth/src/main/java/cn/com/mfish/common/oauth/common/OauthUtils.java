package cn.com.mfish.common.oauth.common;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;
import cn.com.mfish.common.oauth.cache.UserPermissionCache;
import cn.com.mfish.common.oauth.cache.UserRoleCache;
import cn.com.mfish.common.oauth.api.entity.UserRole;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: mfish
 * @description：OAUTH工具方法
 * @date: 2022/12/6 17:23
 */
public class OauthUtils {


    /**
     * 生成6位数验证码
     *
     * @return
     */
    public static String buildCode() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        return String.valueOf((int) (random.nextDouble() * 900000 + 100000));
    }

    /**
     * 校验角色
     *
     * @param requiresRoles 角色限制
     * @return
     */
    public static boolean checkRoles(RequiresRoles requiresRoles) {
        UserRoleCache userRoleCache = SpringBeanFactory.getBean("userRoleCache");
        List<UserRole> list = userRoleCache.getFromCacheAndDB(AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentClientId());
        Set<String> set = list.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //如果用户为超户，直接返回
        if (null != set && set.contains(SerConstant.SUPER_ROLE)) {
            return true;
        }
        return checkValue(requiresRoles.logical(), requiresRoles.value(), set);
    }

    /**
     * 校验权限
     *
     * @param requiresPermissions 权限限制
     * @return
     */
    public static boolean checkPermission(RequiresPermissions requiresPermissions) {
        UserPermissionCache userPermissionCache = SpringBeanFactory.getBean("userPermissionCache");
        Set<String> set = userPermissionCache.getFromCacheAndDB(AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentClientId());
        //如果用户拥有所有权限直接返回true
        if (null != set && set.contains(SerConstant.ALL_PERMISSION)) {
            return true;
        }
        return checkValue(requiresPermissions.logical(), requiresPermissions.value(), set);
    }

    /**
     * 校验结果
     *
     * @param logical
     * @param values
     * @param set
     * @return
     */
    private static boolean checkValue(Logical logical, String[] values, Set<String> set) {
        //未设置权限值，校验通过
        if (values == null) {
            return true;
        }
        //未获取到用户权限，校验不通过
        if (set == null) {
            return false;
        }
        switch (logical) {
            case AND:
                for (String value : values) {
                    if (!set.contains(value)) {
                        return false;
                    }
                }
                return true;
            case OR:
                for (String value : values) {
                    if (set.contains(value)) {
                        return true;
                    }
                }
            default:
                return false;
        }
    }

    /**
     * 是否超户
     *
     * @return
     */
    public static boolean isSuper() {
        return isSuper(AuthInfoUtils.getCurrentUserId());
    }

    public static boolean isSuper(String userId) {
        return "1".equals(userId);
    }
}

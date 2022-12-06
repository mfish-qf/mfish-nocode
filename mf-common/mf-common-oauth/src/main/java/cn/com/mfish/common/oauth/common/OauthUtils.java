package cn.com.mfish.common.oauth.common;

import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author ：qiufeng
 * @description：OAUTH工具方法
 * @date ：2022/12/6 17:23
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
        return true;
    }

    /**
     * 校验权限
     *
     * @param requiresPermissions 权限限制
     * @return
     */
    public static boolean checkPermission(RequiresPermissions requiresPermissions) {
        return true;
    }
}

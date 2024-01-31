package cn.com.mfish.common.oauth.common;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.enums.DeviceType;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.api.remote.RemoteUserService;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.entity.WeChatToken;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.common.oauth.service.TokenService;
import cn.com.mfish.common.oauth.service.impl.WeChatTokenServiceImpl;
import cn.com.mfish.common.oauth.service.impl.WebTokenServiceImpl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: mfish
 * @description: OAUTH工具方法
 * @date: 2022/12/6 17:23
 */
public class OauthUtils {

    private OauthUtils() {

    }

    /**
     * 获取用户信息
     *
     * @return
     */
    public static UserInfo getUser() {
        if (ServiceConstants.isBoot(Utils.getServiceType())) {
            SsoUserService userService = SpringBeanFactory.getBean(SsoUserService.class);
            return userService.getUserById(AuthInfoUtils.getCurrentUserId());
        }
        RemoteUserService remoteUserService = SpringBeanFactory.getBean(RemoteUserService.class);
        Result<UserInfo> result = remoteUserService.getUserById(RPCConstants.INNER, AuthInfoUtils.getCurrentUserId());
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

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
     * 获取当前用户角色
     *
     * @return
     */
    public static List<UserRole> getRoles() {
        if (ServiceConstants.isBoot(Utils.getServiceType())) {
            SsoUserService userService = SpringBeanFactory.getBean(SsoUserService.class);
            return userService.getUserRoles(AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId());
        }
        RemoteUserService remoteUserService = SpringBeanFactory.getBean(RemoteUserService.class);
        Result<List<UserRole>> result = remoteUserService.getRoles(RPCConstants.INNER, AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId());
        if (result == null || !result.isSuccess()) {
            return new ArrayList<>();
        }
        return result.getData();
    }

    /**
     * 校验角色
     *
     * @param requiresRoles 角色限制
     * @return
     */
    public static boolean checkRoles(RequiresRoles requiresRoles) {
        List<UserRole> list = getRoles();
        if (list == null || list.isEmpty()) {
            return false;
        }
        Set<String> set = list.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //如果用户为超户，直接返回
        if (set.contains(SerConstant.SUPER_ROLE)) {
            return true;
        }
        return checkValue(requiresRoles.logical(), requiresRoles.value(), set);
    }

    /**
     * 获取当前用户按钮权限
     *
     * @return
     */
    public static Set<String> getPermission() {
        if (ServiceConstants.isBoot(Utils.getServiceType())) {
            SsoUserService userService = SpringBeanFactory.getBean(SsoUserService.class);
            return userService.getUserPermissions(AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId());
        }
        RemoteUserService remoteUserService = SpringBeanFactory.getBean(RemoteUserService.class);
        Result<Set<String>> result = remoteUserService.getPermissions(RPCConstants.INNER, AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId());
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    /**
     * 获取租户列表
     *
     * @return
     */
    public static List<TenantVo> getTenants() {
        if (ServiceConstants.isBoot(Utils.getServiceType())) {
            SsoUserService userService = SpringBeanFactory.getBean(SsoUserService.class);
            return userService.getUserTenants(AuthInfoUtils.getCurrentUserId());
        }
        RemoteUserService remoteUserService = SpringBeanFactory.getBean(RemoteUserService.class);
        Result<List<TenantVo>> result = remoteUserService.getTenants(RPCConstants.INNER, AuthInfoUtils.getCurrentUserId());
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    /**
     * 校验权限
     *
     * @param requiresPermissions 权限限制
     * @return
     */
    public static boolean checkPermission(RequiresPermissions requiresPermissions) {
        Set<String> set = getPermission();
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
     * 根据token获取tokenService
     *
     * @param token
     * @return
     */
    public static TokenService getTokenService(String token) {
        if (token.startsWith(SerConstant.WX_PREFIX)) {
            return SpringBeanFactory.getBean(WeChatTokenServiceImpl.class);
        }
        return SpringBeanFactory.getBean(WebTokenServiceImpl.class);

    }

    /**
     * 设置token
     *
     * @param tokenId
     * @param token
     */
    public static void setToken(String tokenId, Object token) {
        getTokenService(tokenId).setToken(token);
    }

    /**
     * 获取当前token对象
     *
     * @return
     */
    public static Object getToken() {
        String token = AuthInfoUtils.getAccessToken();
        return getToken(getTokenService(token), token);
    }

    /**
     * 获取token获取token对象
     *
     * @param token
     * @return
     */
    public static Object getToken(String token) {
        return getToken(getTokenService(token), token);
    }

    public static Object getToken(TokenService tokenService, String token) {
        return tokenService.getToken(token);
    }

    /**
     * 删除token
     *
     * @param token
     */
    public static void delToken(String token) {
        delToken(getTokenService(token), token);
    }

    public static void delToken(TokenService tokenService, String token) {
        tokenService.delToken(token);
    }

    /**
     * 删除refreshToken
     *
     * @param refreshToken
     */
    public static void delRefreshToken(String refreshToken) {
        delRefreshToken(getTokenService(refreshToken), refreshToken);
    }

    public static void delRefreshToken(TokenService tokenService, String refreshToken) {
        tokenService.delRefreshToken(refreshToken);
    }

    /**
     * 删除token和对应refreshToken
     *
     * @param token token
     * @return 返回 token关联的sessionId
     */
    public static LoginMutexEntity delTokenAndRefreshToken(String token) {
        TokenService tokenService = getTokenService(token);
        Object accessToken = getToken(tokenService, token);
        if (accessToken == null) {
            return null;
        }
        delToken(tokenService, token);
        if (accessToken instanceof RedisAccessToken) {
            RedisAccessToken redisAccessToken = (RedisAccessToken) accessToken;
            delRefreshToken(tokenService, redisAccessToken.getRefreshToken());
            return new LoginMutexEntity().setDeviceId(redisAccessToken.getTokenSessionId()).setDeviceType(DeviceType.Web).setUserId(redisAccessToken.getUserId());
        } else if (accessToken instanceof WeChatToken) {
            WeChatToken weChatToken = (WeChatToken) accessToken;
            delRefreshToken(tokenService, weChatToken.getRefresh_token());
            return new LoginMutexEntity().setDeviceId(weChatToken.getOpenid()).setDeviceType(DeviceType.WX).setUserId(weChatToken.getUserId());
        }
        return null;
    }
}

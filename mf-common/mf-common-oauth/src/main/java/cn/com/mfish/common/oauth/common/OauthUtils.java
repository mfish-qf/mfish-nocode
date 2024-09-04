package cn.com.mfish.common.oauth.common;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.enums.DeviceType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.api.remote.RemoteUserService;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.entity.WeChatToken;
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
@SuppressWarnings({"rawtypes", "unchecked"})
public class OauthUtils {

    private OauthUtils() {
    }

    /**
     * 获取远程用户接口服务
     *
     * @return 返回远程用户接口服务
     */
    private static RemoteUserService getRemoteUserService() {
        return SpringBeanFactory.getRemoteService(RemoteUserService.class);
    }

    /**
     * 获取用户信息
     * 注意：该方法请勿用异步调用
     *
     * @return 返回用户信息
     */
    public static UserInfo getUser() {
        Result<UserInfo> result = getRemoteUserService().getUserById(RPCConstants.INNER, AuthInfoUtils.getCurrentUserId());
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    /**
     * 生成6位数验证码
     *
     * @return 返回验证码
     */
    public static String buildCode() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        return String.valueOf((int) (random.nextDouble() * 900000 + 100000));
    }

    /**
     * 获取当前用户角色
     * 注意：该方法请勿用异步调用
     *
     * @return 返回用户角色
     */
    public static List<UserRole> getRoles() {
        Result<List<UserRole>> result = getRemoteUserService().getRoles(RPCConstants.INNER, AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId());
        if (result == null || !result.isSuccess()) {
            return new ArrayList<>();
        }
        return result.getData();
    }

    /**
     * 校验角色
     *
     * @param requiresRoles 角色限制
     * @return 返回是否通过校验
     */
    public static boolean checkRoles(RequiresRoles requiresRoles) {
        List<UserRole> list = getRoles();
        if (list == null || list.isEmpty()) {
            return false;
        }
        Set<String> set = list.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //如果用户为超户，直接返回
        if (set.contains(AuthInfoUtils.SUPER_ROLE)) {
            return true;
        }
        return checkValue(requiresRoles.logical(), requiresRoles.value(), set);
    }

    /**
     * 获取当前用户按钮权限
     * 注意：该方法请勿用异步调用
     *
     * @return 返回用户按钮权限
     */
    public static Set<String> getPermission() {
        Result<Set<String>> result = getRemoteUserService().getPermissions(RPCConstants.INNER, AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId());
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    /**
     * 获取租户列表
     * 注意：该方法请勿用异步调用
     *
     * @return 返回租户列表
     */
    public static List<TenantVo> getTenants() {
        Result<List<TenantVo>> result = getRemoteUserService().getTenants(RPCConstants.INNER, AuthInfoUtils.getCurrentUserId());
        if (result == null || !result.isSuccess()) {
            return null;
        }
        return result.getData();
    }

    /**
     * 获取当前租户的信息
     *
     * 本函数的目的是根据当前的token来确定并返回当前租户的详细信息
     * 它首先检查是否存在有效的token，如果不存在，则抛出异常
     * 如果token存在，它进一步判断token的类型（RedisAccessToken或WeChatToken）
     * 根据token类型提取出当前租户的ID，然后从所有租户列表中匹配该租户信息
     * 如果找不到匹配的租户，或者用户不属于该租户，则抛出异常
     * 最后，返回匹配到的租户信息
     *
     * @return TenantVo 当前租户的信息，如果找不到则返回null
     * @throws MyRuntimeException 如果未获取到token信息，或者找不到当前租户，或者用户不属于此租户
     */
    public static TenantVo getCurrentTenant() {
        Object token = getToken();
        if (token == null) {
            throw new MyRuntimeException("错误：未获取到token信息");
        }
        String tenantId;
        if (token instanceof RedisAccessToken redisAccessToken) {
            tenantId = redisAccessToken.getTenantId();
        } else if (token instanceof WeChatToken weChatToken) {
            tenantId = weChatToken.getTenantId();
        } else {
            throw new MyRuntimeException("错误：未找到当前租户");
        }
        List<TenantVo> list = getTenants();
        if (list == null) {
            throw new MyRuntimeException("错误：未获取到账号租户列表");
        }
        return list.stream().filter(tenantVo -> tenantVo.getId().equals(tenantId)).findFirst().orElse(null);
    }

    /**
     * 校验权限
     *
     * @param requiresPermissions 权限限制
     * @return 返回是否通过校验
     */
    public static boolean checkPermission(RequiresPermissions requiresPermissions) {
        Set<String> set = getPermission();
        //如果用户拥有所有权限直接返回true
        if (null != set && set.contains(AuthInfoUtils.ALL_PERMISSION)) {
            return true;
        }
        return checkValue(requiresPermissions.logical(), requiresPermissions.value(), set);
    }

    /**
     * 校验结果
     *
     * @param logical 组合逻辑
     * @param values  需要校验的权限
     * @param set     所有权限
     * @return 返回校验结果
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
     * @param token token
     * @return 返回tokenService
     */
    public static TokenService getTokenService(String token) {
        if (!StringUtils.isEmpty(token) && token.startsWith(SerConstant.WX_PREFIX)) {
            return SpringBeanFactory.getBean(WeChatTokenServiceImpl.class);
        }
        return SpringBeanFactory.getBean(WebTokenServiceImpl.class);

    }

    /**
     * 设置token
     *
     * @param tokenId tokenID
     * @param token   token
     */
    public static void setToken(String tokenId, Object token) {
        getTokenService(tokenId).setToken(token);
    }

    /**
     * 获取当前token对象
     * 注意：该方法请勿用异步调用
     *
     * @return 返回token对象
     */
    public static Object getToken() {
        String token = AuthInfoUtils.getAccessToken();
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return getToken(getTokenService(token), token);
    }

    /**
     * 获取token获取token对象
     *
     * @param token token
     * @return 返回token对象
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
     * @param token token
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
     * @param refreshToken refreshToken
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
        if (accessToken instanceof RedisAccessToken redisAccessToken) {
            delRefreshToken(tokenService, redisAccessToken.getRefreshToken());
            return new LoginMutexEntity().setDeviceId(redisAccessToken.getTokenSessionId()).setDeviceType(DeviceType.Web).setUserId(redisAccessToken.getUserId());
        } else if (accessToken instanceof WeChatToken weChatToken) {
            delRefreshToken(tokenService, weChatToken.getRefresh_token());
            return new LoginMutexEntity().setDeviceId(weChatToken.getOpenid()).setDeviceType(DeviceType.WX).setUserId(weChatToken.getUserId());
        }
        return null;
    }
}

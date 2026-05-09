package cn.com.mfish.oauth.security;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.oauth.service.LoginService;
import cn.com.mfish.oauth.service.SsoTenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @description: 微信手机号认证提供者，处理通过微信小程序手机号进行登录认证的逻辑，支持自动注册新用户
 * @author: mfish
 * @date: 2026/05/09
 */
@Slf4j
@Component
public class WxPhoneAuthenticationProvider implements AuthenticationProvider {

    /** 用户服务，用于查询和操作用户信息 */
    private final SsoUserService ssoUserService;
    /** 登录服务，用于获取微信OpenID */
    private final LoginService loginService;
    /** 租户服务，用于新用户注册时创建租户关联 */
    private final SsoTenantService ssoTenantService;

    /** 是否自动创建用户，通过配置oauth2.user.autoCreate控制 */
    @Value("${oauth2.user.autoCreate}")
    boolean autoCreateUser = false;

    /**
     * 构造函数，注入依赖服务
     *
     * @param ssoUserService  用户服务
     * @param loginService    登录服务
     * @param ssoTenantService 租户服务
     */
    @Autowired
    public WxPhoneAuthenticationProvider(SsoUserService ssoUserService, LoginService loginService,
                                         SsoTenantService ssoTenantService) {
        this.ssoUserService = ssoUserService;
        this.loginService = loginService;
        this.ssoTenantService = ssoTenantService;
    }

    /**
     * 执行微信手机号认证，通过sessionKey获取微信OpenID进行身份验证，并根据配置决定是否自动注册新用户
     *
     * @param authentication 认证令牌，用户名为手机号，凭证为微信sessionKey
     * @return 认证成功后的Authentication对象
     * @throws AuthenticationException 用户不存在、未开启自动注册或OpenID无效时抛出异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfishAuthenticationToken token = (MfishAuthenticationToken) authentication;
        if (token.getLoginType() != SerConstant.LoginType.微信登录) {
            return null;
        }
        String username = token.getUsername();
        SsoUser user = ssoUserService.getUserByAccount(username);
        boolean isNew = false;
        if (user == null) {
            if (!autoCreateUser) {
                String error = "账号[" + username + "]不存在或已被禁用";
                log.error(error);
                throw new UsernameNotFoundException(error);
            }
            user = buildUser(username);
            isNew = true;
        }
        token.setUserInfo(user);
        token.setNew(isNew);

        String sessionKey = String.valueOf(token.getCredentials());
        String openId = loginService.getOpenIdBySessionKey(sessionKey);
        if (StringUtils.isEmpty(openId)) {
            throw new BadCredentialsException(SerConstant.INVALID_WX_ID_DESCRIPTION);
        }

        if (isNew) {
            ssoTenantService.createTenantUser(user);
        }

        token.setAuthenticated(true);
        return token;
    }

    /**
     * 根据手机号构建新用户信息，用于微信登录自动注册场景
     *
     * @param phone 手机号
     * @return 新构建的用户对象
     * @throws UsernameNotFoundException 手机号为空或格式不正确时抛出
     */
    private SsoUser buildUser(String phone) {
        if (StringUtils.isEmpty(phone) || !StringUtils.isPhone(phone)) {
            throw new UsernameNotFoundException("错误:手机号不正确");
        }
        SsoUser userInfo = new SsoUser();
        userInfo.setPhone(phone);
        userInfo.setId(Utils.uuid32());
        userInfo.setAccount(phone);
        userInfo.setNickname("user" + phone.substring(7, 11));
        userInfo.setSex(1);
        return userInfo;
    }

    /**
     * 判断当前Provider是否支持处理指定类型的认证令牌
     *
     * @param authentication 认证令牌类型
     * @return 是否支持该认证类型
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return MfishAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

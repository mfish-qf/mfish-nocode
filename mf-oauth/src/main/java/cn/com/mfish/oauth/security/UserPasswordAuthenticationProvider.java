package cn.com.mfish.oauth.security;

import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.oauth.common.PasswordHelper;
import cn.com.mfish.oauth.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @description: 用户密码认证提供者，处理通过用户名和密码进行登录认证的逻辑，包含密码加密校验和登录重试限制
 * @author: mfish
 * @date: 2026/05/09
 */
@Slf4j
@Component
public class UserPasswordAuthenticationProvider implements AuthenticationProvider {

    /** 用户服务，用于查询用户信息 */
    private final SsoUserService ssoUserService;
    /** 登录服务，用于登录重试次数限制 */
    private final LoginService loginService;

    /**
     * 构造函数，注入依赖服务
     *
     * @param ssoUserService 用户服务
     * @param loginService   登录服务
     */
    @Autowired
    public UserPasswordAuthenticationProvider(SsoUserService ssoUserService, LoginService loginService) {
        this.ssoUserService = ssoUserService;
        this.loginService = loginService;
    }

    /**
     * 执行用户密码认证，通过加密后的密码与数据库中存储的密码进行比对，并执行登录重试次数限制
     *
     * @param authentication 认证令牌，用户名为账号，凭证为明文密码
     * @return 认证成功后的Authentication对象
     * @throws AuthenticationException 用户不存在或密码错误时抛出异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfishAuthenticationToken token = (MfishAuthenticationToken) authentication;
        if (token.getLoginType() != SerConstant.LoginType.密码登录) {
            return null;
        }
        String username = token.getUsername();
        SsoUser user = ssoUserService.getUserByAccount(username);
        if (user == null) {
            String error = "账号[" + username + "]不存在或已被禁用";
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        token.setUserInfo(user);

        String submittedPassword = String.valueOf(token.getCredentials());
        String hashedPassword = PasswordHelper.encryptPassword(user.getId(), submittedPassword, user.getSalt());
        boolean matches = hashedPassword.equals(user.getPassword());

        loginService.retryLimit(user.getId(), matches);

        token.setAuthenticated(true);
        return token;
    }

    /**
     * 判断当前Provider是否支持处理指定类型的认证令牌
     *
     * @param authentication 认证令牌类型
     * @return 是否支持该认证类型
     */
    @Override
    public boolean supports(Class<?> authentication) {
        if (!MfishAuthenticationToken.class.isAssignableFrom(authentication)) {
            return false;
        }
        return true;
    }
}

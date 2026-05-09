package cn.com.mfish.oauth.security;

import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.oauth.service.SsoUserService;
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
 * 用户密码认证提供者
 * 替代 UserPasswordRealm + MyHashedCredentialsMatcher
 */
@Slf4j
@Component
public class UserPasswordAuthenticationProvider implements AuthenticationProvider {

    private final SsoUserService ssoUserService;
    private final LoginService loginService;

    @Autowired
    public UserPasswordAuthenticationProvider(SsoUserService ssoUserService, LoginService loginService) {
        this.ssoUserService = ssoUserService;
        this.loginService = loginService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfishAuthenticationToken token = (MfishAuthenticationToken) authentication;
        String username = token.getUsername();
        SsoUser user = ssoUserService.getUserByAccount(username);
        if (user == null) {
            String error = "账号[" + username + "]不存在或已被禁用";
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        token.setUserInfo(user);

        String submittedPassword = String.valueOf(token.getCredentials());
        String hashedPassword = PasswordHashUtils.md5Hash(submittedPassword, user.getId() + user.getSalt());
        boolean matches = hashedPassword.equals(user.getPassword());

        loginService.retryLimit(user.getId(), matches);

        token.setAuthenticated(true);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        if (!MfishAuthenticationToken.class.isAssignableFrom(authentication)) {
            return false;
        }
        return true;
    }
}

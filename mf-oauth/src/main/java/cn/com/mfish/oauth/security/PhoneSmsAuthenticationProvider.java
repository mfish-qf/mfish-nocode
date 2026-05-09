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
 * 手机短信认证提供者
 * 替代 PhoneSmsRealm + SmsCredentialsMatcher
 */
@Slf4j
@Component
public class PhoneSmsAuthenticationProvider implements AuthenticationProvider {

    private final SsoUserService ssoUserService;
    private final LoginService loginService;
    private final SsoTenantService ssoTenantService;

    @Value("${oauth2.user.autoCreate}")
    boolean autoCreateUser = false;

    @Autowired
    public PhoneSmsAuthenticationProvider(SsoUserService ssoUserService, LoginService loginService,
                                          SsoTenantService ssoTenantService) {
        this.ssoUserService = ssoUserService;
        this.loginService = loginService;
        this.ssoTenantService = ssoTenantService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfishAuthenticationToken token = (MfishAuthenticationToken) authentication;
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

        String code = loginService.getSmsCode(user.getPhone());
        if (StringUtils.isEmpty(code)) {
            throw new BadCredentialsException(SerConstant.INVALID_PHONE_CODE_DESCRIPTION);
        }

        String submittedCode = String.valueOf(token.getCredentials());
        boolean matches = code.equals(submittedCode);

        loginService.retryLimit(user.getId(), matches);

        if (isNew) {
            ssoTenantService.createTenantUser(user);
        }
        if (matches) {
            loginService.delSmsCode(username);
        }

        token.setAuthenticated(true);
        return token;
    }

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

    @Override
    public boolean supports(Class<?> authentication) {
        return MfishAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

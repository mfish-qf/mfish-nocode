package cn.com.mfish.oauth.security;

import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.oauth.entity.RedisQrCode;
import cn.com.mfish.oauth.service.QRCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 扫码登录认证提供者
 * 替代 QRCodeRealm + QRCodeCredentialsMatcher
 */
@Component
public class QRCodeAuthenticationProvider implements AuthenticationProvider {

    private final SsoUserService ssoUserService;
    private final QRCodeService qrCodeService;

    @Autowired
    public QRCodeAuthenticationProvider(SsoUserService ssoUserService, QRCodeService qrCodeService) {
        this.ssoUserService = ssoUserService;
        this.qrCodeService = qrCodeService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfishAuthenticationToken token = (MfishAuthenticationToken) authentication;
        String username = token.getUsername();
        SsoUser user = ssoUserService.getUserByAccount(username);
        if (user == null) {
            throw new UsernameNotFoundException("账号[" + username + "]不存在或已被禁用");
        }
        token.setUserInfo(user);

        String[] secret = StringUtils.split(String.valueOf(token.getCredentials()), ",");
        if (secret == null || secret.length != 2) {
            throw new BadCredentialsException(SerConstant.INVALID_USER_SECRET_DESCRIPTION);
        }
        RedisQrCode redisQrCode = qrCodeService.checkQRCode(secret[0]);
        if (redisQrCode == null) {
            throw new BadCredentialsException(SerConstant.INVALID_USER_SECRET_DESCRIPTION);
        }

        String expected = redisQrCode.getCode() + "," + redisQrCode.getSecret();
        if (!expected.equals(secret[0] + "," + secret[1])) {
            throw new BadCredentialsException(SerConstant.INVALID_USER_SECRET_DESCRIPTION);
        }

        token.setAuthenticated(true);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MfishAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

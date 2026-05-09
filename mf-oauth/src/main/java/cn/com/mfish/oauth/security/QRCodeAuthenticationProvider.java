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
 * @description: 扫码登录认证提供者，处理通过扫描二维码进行身份认证的逻辑，校验二维码凭证的有效性
 * @author: mfish
 * @date: 2026/05/09
 */
@Component
public class QRCodeAuthenticationProvider implements AuthenticationProvider {

    /** 用户服务，用于查询用户信息 */
    private final SsoUserService ssoUserService;
    /** 二维码服务，用于校验二维码凭证的有效性 */
    private final QRCodeService qrCodeService;

    /**
     * 构造函数，注入依赖服务
     *
     * @param ssoUserService 用户服务
     * @param qrCodeService  二维码服务
     */
    @Autowired
    public QRCodeAuthenticationProvider(SsoUserService ssoUserService, QRCodeService qrCodeService) {
        this.ssoUserService = ssoUserService;
        this.qrCodeService = qrCodeService;
    }

    /**
     * 执行扫码登录认证，校验用户是否存在以及二维码凭证（code,secret）是否匹配
     *
     * @param authentication 认证令牌，用户名为账号，凭证为"code,secret"格式的字符串
     * @return 认证成功后的Authentication对象
     * @throws AuthenticationException 用户不存在或凭证无效时抛出异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfishAuthenticationToken token = (MfishAuthenticationToken) authentication;
        if (token.getLoginType() != SerConstant.LoginType.扫码登录) {
            return null;
        }
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

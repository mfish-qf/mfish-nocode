package cn.com.mfish.oauth.realm;

import cn.com.mfish.oauth.common.MyUsernamePasswordToken;
import cn.com.mfish.oauth.common.SerConstant;
import cn.com.mfish.oauth.model.RedisQrCode;
import cn.com.mfish.oauth.model.SSOUser;
import cn.com.mfish.oauth.service.QRCodeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/11 9:42
 */
public class QRCodeRealm extends BaseRealm {
    @Resource
    QRCodeService qrCodeService;

    @Override
    protected AuthenticationInfo buildAuthenticationInfo(SSOUser user, AuthenticationToken authenticationToken, boolean newUser) {
        String[] secret = StringUtils.split(String.valueOf(((MyUsernamePasswordToken) authenticationToken).getPassword()), ",");
        //secret由扫码code值和secret拼接而成
        if (secret == null || secret.length != 2) {
            throw new IncorrectCredentialsException(SerConstant.INVALID_USER_SECRET_DESCRIPTION);
        }
        RedisQrCode redisQrCode = qrCodeService.checkQRCode(secret[0]);
        if (redisQrCode == null) {
            throw new IncorrectCredentialsException(SerConstant.INVALID_USER_SECRET_DESCRIPTION);
        }
        AuthenticationInfo authorizationInfo = new SimpleAuthenticationInfo(
                user.getId(), redisQrCode.getCode() + "," + redisQrCode.getSecret(), getName());
        return authorizationInfo;
    }
}

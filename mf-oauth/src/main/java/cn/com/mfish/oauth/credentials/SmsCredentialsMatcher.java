package cn.com.mfish.oauth.credentials;

import cn.com.mfish.oauth.common.MyUsernamePasswordToken;
import cn.com.mfish.oauth.service.LoginService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

import jakarta.annotation.Resource;


/**
 * @description: 短信验证码凭证匹配器，校验短信验证码并支持新用户自动创建
 * @author: mfish
 * @date: 2020/2/25 16:51
 */
public class SmsCredentialsMatcher extends AutoUserCredentialsMatcher {
    @Resource
    LoginService loginService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) authenticationToken;
        boolean matches = super.doCredentialsMatch(authenticationToken, authenticationInfo);
        if (matches) {
            insertNewUser(myToken.isNew(), myToken.getUserInfo());
        }
        boolean success = loginService.retryLimit(myToken.getUserInfo().getId(), matches);
        if (success) {
            loginService.delSmsCode(myToken.getUsername());
        }
        return success;
    }
}

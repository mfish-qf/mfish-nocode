package cn.com.mfish.oauth.credentials;

import cn.com.mfish.oauth.common.MyUsernamePasswordToken;
import cn.com.mfish.oauth.service.LoginService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

import javax.annotation.Resource;


/**
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
        if(matches){
            insertNewUser(myToken.isNew(),myToken.getUserInfo());
        }
        return loginService.retryLimit(myToken.getUserInfo().getId(), matches);
    }
}

package cn.com.mfish.oauth.credentials;

import cn.com.mfish.oauth.common.MyUsernamePasswordToken;
import cn.com.mfish.oauth.service.LoginService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/10 19:48
 */
public class MyHashedCredentialsMatcher extends HashedCredentialsMatcher {
    @Resource
    LoginService loginService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) token;
        boolean matches = super.doCredentialsMatch(token, info);
        return loginService.retryLimit(myToken.getUserInfo().getId(), matches);
    }

}

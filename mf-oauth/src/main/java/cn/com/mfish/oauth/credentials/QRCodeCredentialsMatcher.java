package cn.com.mfish.oauth.credentials;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * @description: 二维码凭证匹配器，用于扫码登录方式的凭证校验
 * @author: mfish
 * @date: 2020/2/25 16:49
 */
public class QRCodeCredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        return super.doCredentialsMatch(authenticationToken, authenticationInfo);
    }
}

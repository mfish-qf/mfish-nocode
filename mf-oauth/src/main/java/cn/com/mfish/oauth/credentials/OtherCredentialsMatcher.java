package cn.com.mfish.oauth.credentials;

import cn.com.mfish.oauth.common.MyUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 第三方凭证校验创建用户
 * @author: mfish
 * @date: 2021/10/26 17:00
 */
public class OtherCredentialsMatcher extends AutoUserCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) authenticationToken;
        insertNewUser(myToken.isNew(), myToken.getUserInfo());
        return true;
    }
}

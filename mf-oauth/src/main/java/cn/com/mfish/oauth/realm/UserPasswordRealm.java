package cn.com.mfish.oauth.realm;

import cn.com.mfish.common.oauth.entity.SsoUser;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.lang.util.ByteSource;

/**
 * @author: mfish
 * @date: 2020/2/11 9:33
 */
public class UserPasswordRealm extends BaseRealm {

    @Override
    protected AuthenticationInfo buildAuthenticationInfo(SsoUser user, AuthenticationToken authenticationToken, boolean newUser) {
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        return new SimpleAuthenticationInfo(
                user.getId(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getId() + user.getSalt()),
                getName()  //调用基类realm
        );
    }
}

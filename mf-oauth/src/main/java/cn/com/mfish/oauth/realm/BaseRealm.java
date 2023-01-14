package cn.com.mfish.oauth.realm;

import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.oauth.common.MyUsernamePasswordToken;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.service.SsoUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: mfish
 * @date: 2020/2/26 17:23
 */
@Slf4j
public abstract class BaseRealm extends AuthorizingRealm {
    @Autowired
    SsoUserService ssoUserService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
//        String userId = (String) principalCollection.getPrimaryPrincipal();
//        if (StringUtils.isEmpty(userId)) {
//            throw new UnknownAccountException();//没找到帐号
//        }
//        String clientId = AuthUtils.getCurrentClientId();
//        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
//        List<UserRole> list = ssoUserService.getUserRoles(userId, clientId);
//        authorizationInfo.setRoles(new HashSet<>(list.stream().map(UserRole::getRoleCode).collect(Collectors.toList())));
//        authorizationInfo.setStringPermissions(ssoUserService.getUserPermissions(userId, clientId));
//        return authorizationInfo;

        // 此处如果开启需要在shiroConfig中配置securityManager.setAuthorizer()并需要设置realm
        // 与现有多realm逻辑不太契合
        // 不基于当前shiro验证权限，基于整个微服务框架验证权限
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) authenticationToken;
        SsoUser user = ssoUserService.getUserByAccount(myToken.getUsername());
        boolean isNew = false;
        if (user == null) {
            //微信登录 短信登录 允许登录时创建账号,其他方式不允许
            if (myToken.getLoginType() != SerConstant.LoginType.微信登录 && myToken.getLoginType() != SerConstant.LoginType.短信登录) {
                String error = "账号[" + myToken.getUsername() + "]不存在或已被禁用";
                log.error(error);
                throw new UnknownAccountException(error);
            }
            user = buildUser(myToken.getUsername());
            isNew = true;
        }
        //设置用户ID
        myToken.setUserInfo(user);
        //设置是否新用户
        myToken.setNew(isNew);
        //不同登录方式采用不同的AuthenticationInfo构建方式
        return buildAuthenticationInfo(user, authenticationToken, isNew);
    }

    /**
     * 构建新用户 只适用于短信登录和微信登录
     *
     * @param phone 手机号
     * @return
     */
    private SsoUser buildUser(String phone) {
        if (phone.length() != 11) {
            throw new UnknownAccountException("手机号不正确");
        }
        SsoUser userInfo = new SsoUser();
        userInfo.setPhone(phone);
        userInfo.setId(Utils.uuid32());
        userInfo.setAccount(phone);
        userInfo.setNickname("user" + phone.substring(7, 11));
        userInfo.setSex(1);
        return userInfo;
    }

    protected abstract AuthenticationInfo buildAuthenticationInfo(SsoUser user, AuthenticationToken authenticationToken, boolean newUser);
}

package cn.com.mfish.oauth.realm;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.common.redis.common.IDBuild;
import cn.com.mfish.oauth.common.MyUsernamePasswordToken;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Arrays;

/**
 * @description: gitee登录
 * @author: mfish
 * @date: 2025/2/7
 */
@Slf4j
public class GitRealm extends AuthorizingRealm {
    @Resource
    SsoUserService ssoUserService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) authenticationToken;
        SsoUser user = getUser(myToken.getUsername(), myToken);
        //设置用户信息
        myToken.setUserInfo(user);
        return new SimpleAuthenticationInfo(
                user.getId(), Arrays.toString(myToken.getPassword()), getName());
    }

    private SsoUser getUser(String giteeInfo, MyUsernamePasswordToken myToken) {
        if (StringUtils.isEmpty(giteeInfo)) {
            throw new UnknownAccountException("错误:未获取到用户信息");
        }
        JSONObject json = JSON.parseObject(giteeInfo);
        String username = json.getString("login");
        if (StringUtils.isEmpty(username)) {
            throw new UnknownAccountException("错误：未获取到账号");
        }
        SsoUser user = null;
        if (myToken.getLoginType() == SerConstant.LoginType.Github) {
            user = ssoUserService.getUserByGithub(username);
        } else if (myToken.getLoginType() == SerConstant.LoginType.Gitee) {
            user = ssoUserService.getUserByGitee(username);
        }
        if (null != user) return user;
        user = ssoUserService.getUserByAccount(username);
        String account;
        if (user == null && StringUtils.isMatch("^[a-zA-Z0-9]+$", username)) {
            account = username;
        } else {
            account = IDBuild.getID("G");
        }
        SsoUser userInfo = new SsoUser();
        userInfo.setId(Utils.uuid32());
        userInfo.setAccount(account);
        if (myToken.getLoginType() == SerConstant.LoginType.Github) {
            userInfo.setGithub(username);
        } else {
            userInfo.setGitee(username);
        }
        userInfo.setNickname(json.getString("name"));
        String email = json.getString("email");
        if(!StringUtils.isEmail(email)) {
            email = null;
        }
        userInfo.setEmail(email);
        userInfo.setHeadImgUrl(json.getString("avatar_url"));
        userInfo.setRemark(json.getString("remark"));
        userInfo.setSex(1);
        myToken.setNew(true);
        return userInfo;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }
}

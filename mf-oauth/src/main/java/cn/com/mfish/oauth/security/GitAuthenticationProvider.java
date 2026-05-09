package cn.com.mfish.oauth.security;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.common.redis.common.IDBuild;
import cn.com.mfish.oauth.service.SsoTenantService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Gitee/Github 第三方登录认证提供者
 * 替代 GitRealm + OtherCredentialsMatcher
 */
@Slf4j
@Component
public class GitAuthenticationProvider implements AuthenticationProvider {

    private final SsoUserService ssoUserService;
    private final SsoTenantService ssoTenantService;

    @Autowired
    public GitAuthenticationProvider(SsoUserService ssoUserService, SsoTenantService ssoTenantService) {
        this.ssoUserService = ssoUserService;
        this.ssoTenantService = ssoTenantService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfishAuthenticationToken token = (MfishAuthenticationToken) authentication;
        String giteeInfo = token.getUsername();
        SsoUser user = getUser(giteeInfo, token);
        token.setUserInfo(user);
        ssoTenantService.createTenantUser(user);
        token.setAuthenticated(true);
        return token;
    }

    private SsoUser getUser(String giteeInfo, MfishAuthenticationToken myToken) {
        if (StringUtils.isEmpty(giteeInfo)) {
            throw new UsernameNotFoundException("错误:未获取到用户信息");
        }
        JSONObject json = JSON.parseObject(giteeInfo);
        String username = json.getString("login");
        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("错误：未获取到账号");
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
        if (!StringUtils.isEmail(email)) {
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
    public boolean supports(Class<?> authentication) {
        return MfishAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

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
 * @description: Gitee/Github 第三方登录认证提供者，处理通过Gitee或Github账号进行OAuth认证的逻辑，包括用户查找与自动注册
 * @author: mfish
 * @date: 2026/05/09
 */
@Slf4j
@Component
public class GitAuthenticationProvider implements AuthenticationProvider {

    /** 用户服务，用于查询和操作用户信息 */
    private final SsoUserService ssoUserService;
    /** 租户服务，用于新用户注册时创建租户关联 */
    private final SsoTenantService ssoTenantService;

    /**
     * 构造函数，注入依赖服务
     *
     * @param ssoUserService  用户服务
     * @param ssoTenantService 租户服务
     */
    @Autowired
    public GitAuthenticationProvider(SsoUserService ssoUserService, SsoTenantService ssoTenantService) {
        this.ssoUserService = ssoUserService;
        this.ssoTenantService = ssoTenantService;
    }

    /**
     * 执行Gitee/Github第三方登录认证，根据第三方平台返回的用户信息查找或自动注册本地用户
     *
     * @param authentication 认证令牌，包含第三方平台返回的用户信息
     * @return 认证成功后的Authentication对象
     * @throws AuthenticationException 认证失败时抛出异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MfishAuthenticationToken token = (MfishAuthenticationToken) authentication;
        SerConstant.LoginType loginType = token.getLoginType();
        if (loginType != SerConstant.LoginType.Github && loginType != SerConstant.LoginType.Gitee) {
            return null;
        }
        String giteeInfo = token.getUsername();
        SsoUser user = getUser(giteeInfo, token);
        token.setUserInfo(user);
        if (token.isNew()) {
            ssoTenantService.createTenantUser(user);
        }
        token.setAuthenticated(true);
        return token;
    }

    /**
     * 根据第三方平台用户信息查找已有用户或构建新用户
     * 优先通过Github/Gitee账号关联查找，若未找到再通过本地账号名查找，均不存在则自动注册新用户
     *
     * @param giteeInfo 第三方平台返回的JSON格式用户信息
     * @param myToken   当前认证令牌
     * @return 查找到或新构建的用户对象
     * @throws UsernameNotFoundException 未获取到用户信息时抛出
     */
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

    /**
     * 判断当前Provider是否支持处理指定类型的认证令牌
     *
     * @param authentication 认证令牌类型
     * @return 是否支持该认证类型
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return MfishAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

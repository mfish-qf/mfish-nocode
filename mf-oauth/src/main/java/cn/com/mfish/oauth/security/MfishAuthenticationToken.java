package cn.com.mfish.oauth.security;

import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * @description: 自定义认证令牌，支持多种登录类型（密码、短信、扫码、微信、Github、Gitee等）
 * @author: mfish
 * @date: 2026/05/09
 */
public class MfishAuthenticationToken extends AbstractAuthenticationToken {

    /** 用户名/账号/手机号/第三方信息等，根据登录类型含义不同 */
    @Getter
    private final String username;
    /** 凭证信息，如密码、短信验证码、扫码凭证等 */
    private final Object credentials;

    /** 登录类型，默认为密码登录 */
    @Getter
    @Setter
    private SerConstant.LoginType loginType = SerConstant.LoginType.密码登录;

    /** 认证通过后的用户信息 */
    @Getter
    @Setter
    private SsoUser userInfo;

    /** 是否为新注册用户 */
    @Getter
    @Setter
    private boolean isNew = false;

    /** OAuth2客户端ID，用于标识发起认证的客户端应用 */
    @Getter
    @Setter
    private String clientId;

    /**
     * 构造认证令牌（不带客户端ID）
     *
     * @param username    用户名
     * @param credentials 凭证
     * @param loginType   登录类型
     */
    public MfishAuthenticationToken(String username, Object credentials, SerConstant.LoginType loginType) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.username = username;
        this.credentials = credentials;
        this.loginType = loginType;
    }

    /**
     * 构造认证令牌（带客户端ID）
     *
     * @param username    用户名
     * @param credentials 凭证
     * @param loginType   登录类型
     * @param clientId    OAuth2客户端ID
     */
    public MfishAuthenticationToken(String username, Object credentials, SerConstant.LoginType loginType, String clientId) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.username = username;
        this.credentials = credentials;
        this.loginType = loginType;
        this.clientId = clientId;
    }

    /**
     * 获取认证主体，优先返回已认证用户的ID，未认证时返回用户名
     *
     * @return 用户ID或用户名
     */
    @Override
    public Object getPrincipal() {
        return userInfo != null ? userInfo.getId() : username;
    }

    /**
     * 获取凭证信息
     *
     * @return 凭证对象
     */
    @Override
    public Object getCredentials() {
        return credentials;
    }
}

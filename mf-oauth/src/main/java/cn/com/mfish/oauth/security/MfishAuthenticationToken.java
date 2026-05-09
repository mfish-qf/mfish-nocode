package cn.com.mfish.oauth.security;

import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * 自定义认证令牌，支持多种登录类型
 */
public class MfishAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final String username;
    private final Object credentials;

    @Getter
    @Setter
    private SerConstant.LoginType loginType = SerConstant.LoginType.密码登录;

    @Getter
    @Setter
    private SsoUser userInfo;

    @Getter
    @Setter
    private boolean isNew = false;

    @Getter
    @Setter
    private String clientId;

    public MfishAuthenticationToken(String username, Object credentials, SerConstant.LoginType loginType) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.username = username;
        this.credentials = credentials;
        this.loginType = loginType;
    }

    public MfishAuthenticationToken(String username, Object credentials, SerConstant.LoginType loginType, String clientId) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.username = username;
        this.credentials = credentials;
        this.loginType = loginType;
        this.clientId = clientId;
    }

    @Override
    public Object getPrincipal() {
        return userInfo != null ? userInfo.getId() : username;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }
}

package cn.com.mfish.oauth.common;

import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.oauth.entity.SsoUser;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author: mfish
 * @date: 2020/2/10 19:28
 */
public class MyUsernamePasswordToken extends UsernamePasswordToken {

    //用户ID
    @Setter
    @Getter
    private SsoUser userInfo;
    //是否新用户
    private boolean isNew;

    //登录类型
    @Getter
    private SerConstant.LoginType loginType = SerConstant.LoginType.密码登录;

    //客户端ID
    @Getter
    private String clientId;

    public MyUsernamePasswordToken(String username, String password) {
        super(username, password);
    }

    public MyUsernamePasswordToken(String username, String password, boolean rememberMe) {
        super(username, password, rememberMe);
    }


    public MyUsernamePasswordToken setLoginType(SerConstant.LoginType loginType) {
        this.loginType = loginType;
        return this;
    }

    public boolean isNew() {
        return isNew;
    }

    public MyUsernamePasswordToken setNew(boolean aNew) {
        isNew = aNew;
        return this;
    }

    public MyUsernamePasswordToken setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

}

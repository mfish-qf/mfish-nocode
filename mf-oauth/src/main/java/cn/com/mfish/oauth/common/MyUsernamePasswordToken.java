package cn.com.mfish.oauth.common;

import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.oauth.entity.SsoUser;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author qiufeng
 * @date 2020/2/10 19:28
 */
public class MyUsernamePasswordToken extends UsernamePasswordToken {

    //用户ID
    private SsoUser userInfo;
    //是否新用户
    private boolean isNew;

    //登录类型
    private SerConstant.LoginType loginType = SerConstant.LoginType.密码登录;

    public MyUsernamePasswordToken(String username, String password) {
        super(username, password);
    }

    public MyUsernamePasswordToken(String username, String password, boolean rememberMe) {
        super(username, password, rememberMe);
    }


    public SerConstant.LoginType getLoginType() {
        return loginType;
    }

    public MyUsernamePasswordToken setLoginType(SerConstant.LoginType loginType) {
        this.loginType = loginType;
        return this;
    }

    public SsoUser getUserInfo() {
        return userInfo;
    }

    public MyUsernamePasswordToken setUserInfo(SsoUser userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

}

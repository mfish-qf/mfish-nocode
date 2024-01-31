package cn.com.mfish.oauth.realm;

import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.oauth.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;

import javax.annotation.Resource;


/**
 * @author: mfish
 * @date: 2020/2/11 9:41
 */
public class PhoneSmsRealm extends BaseRealm {
    @Resource
    LoginService loginService;

    @Override
    protected AuthenticationInfo buildAuthenticationInfo(SsoUser user, AuthenticationToken authenticationToken, boolean newUser) {
        String code = loginService.getSmsCode(user.getPhone());
        if (StringUtils.isEmpty(code)) {
            throw new IncorrectCredentialsException(SerConstant.INVALID_PHONE_CODE_DESCRIPTION);
        }
        return new SimpleAuthenticationInfo(
                user.getId(), code, getName());
    }
}

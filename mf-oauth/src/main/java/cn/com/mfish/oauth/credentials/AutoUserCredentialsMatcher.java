package cn.com.mfish.oauth.credentials;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.SerConstant;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.service.SsoUserService;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

import javax.annotation.Resource;

/**
 * 不存在的用户自动创建用户
 *
 * @author: mfish
 * @date: 2021/10/26 17:57
 */
public class AutoUserCredentialsMatcher extends SimpleCredentialsMatcher {
    @Resource
    SsoUserService ssoUserService;

    protected void insertNewUser(boolean newUser, SsoUser user, String clientId) {
        if (newUser) {
            Result<SsoUser> result = ssoUserService.insertUser(user, clientId);
            if (!result.isSuccess()) {
                throw new OAuthValidateException(SerConstant.INVALID_NEW_USER_DESCRIPTION);
            }
        }
    }
}

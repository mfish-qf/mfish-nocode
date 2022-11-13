package cn.com.mfish.oauth.credentials;

import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.common.SerConstant;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.oauth.entity.SSOUser;
import cn.com.mfish.oauth.service.SsoUserService;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

import javax.annotation.Resource;

/**
 * 不存在的用户自动创建用户
 * @author qiufeng
 * @date 2021/10/26 17:57
 */
public class AutoUserCredentialsMatcher extends SimpleCredentialsMatcher {
    @Resource
    SsoUserService ssoUserService;

    protected void insertNewUser(boolean newUser, SSOUser user) {
        if (newUser) {
            CheckWithResult<SSOUser> result = ssoUserService.insert(user);
            if (!result.isSuccess()) {
                throw new OAuthValidateException(SerConstant.INVALID_NEW_USER_DESCRIPTION);
            }
        }
    }
}

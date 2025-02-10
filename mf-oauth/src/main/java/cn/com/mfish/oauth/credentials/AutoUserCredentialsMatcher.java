package cn.com.mfish.oauth.credentials;

import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.oauth.service.SsoTenantService;
import jakarta.annotation.Resource;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * 不存在的用户自动创建用户
 *
 * @author: mfish
 * @date: 2021/10/26 17:57
 */
public class AutoUserCredentialsMatcher extends SimpleCredentialsMatcher {
    @Resource
    SsoTenantService ssoTenantService;

    protected void insertNewUser(boolean newUser, SsoUser user) {
        if (newUser) {
            ssoTenantService.createTenantUser(user);
        }
    }
}

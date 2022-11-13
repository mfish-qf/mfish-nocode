package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SsoUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author qiufeng
 * @date 2020/2/13 16:50
 */
public interface SsoUserService extends IService<SsoUser> {
    Result<SsoUser> changePassword(String userId, String newPassword);
    Result<SsoUser> insert(SsoUser user);
    Result<SsoUser> updateUser(SsoUser user);
    SsoUser getUserByAccount(String account);
    SsoUser getUserById(String userId);
    Integer getUserClientExist(String account, String clientId);
}

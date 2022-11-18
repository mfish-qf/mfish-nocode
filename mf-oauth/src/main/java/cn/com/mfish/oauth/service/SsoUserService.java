package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.api.entity.UserInfo;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.req.ReqSsoUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    List<UserInfo> getUserList(ReqSsoUser reqSsoUser);

    /**
     * 判断客户端下是否存在该用户
     *
     * @param account
     * @param clientId
     * @return
     */
    boolean isUserClientExist(String account, String clientId);

    /**
     * 判断帐号是否存在
     *
     * @param account
     * @return
     */
    boolean isAccountExist(String account);
}

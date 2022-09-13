package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.oauth.cache.temp.Account2IdTempCache;
import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.common.PasswordHelper;
import cn.com.mfish.oauth.mapper.SSOUserMapper;
import cn.com.mfish.oauth.model.SSOUser;
import cn.com.mfish.oauth.cache.temp.UserTempCache;
import cn.com.mfish.oauth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author qiufeng
 * @date 2020/2/13 16:51
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    PasswordHelper passwordHelper;
    @Resource
    SSOUserMapper ssoUserMapper;
    @Resource
    UserTempCache userTempCache;
    @Resource
    Account2IdTempCache account2IdTempCache;

    /**
     * 修改用户密码
     *
     * @param userId
     * @param newPassword
     * @return
     */
    @Override
    public CheckWithResult<SSOUser> changePassword(String userId, String newPassword) {
        CheckWithResult<SSOUser> result = verifyPassword(newPassword);
        if (!result.isSuccess()) {
            return result;
        }
        SSOUser user = userTempCache.getCacheInfo(userId);
        if (user == null) {
            String error = MessageFormat.format("错误:用户id{0}未获取到用户信息!", userId);
            log.error(error);
            return result.setSuccess(false).setMsg(error);
        }

        user.setOldPassword(setOldPwd(user.getOldPassword(), user.getPassword()));
        user.setPassword(passwordHelper.encryptPassword(userId, newPassword, user.getSalt()));
        if (user.getOldPassword().indexOf(user.getPassword()) >= 0) {
            return result.setSuccess(false).setMsg("密码5次内不得循环使用");
        }
        user.setUpdateTime(new Date());
        ssoUserMapper.update(user);
        //更新用户信息,刷新redis 用户缓存
        userTempCache.updateCacheInfo(userId, user);
        return result.setSuccess(true).setMsg("修改成功").setResult(user);
    }

    @Override
    public CheckWithResult<SSOUser> insert(SSOUser user) {
        CheckWithResult<SSOUser> result = new CheckWithResult<SSOUser>().setResult(user);
        int res = ssoUserMapper.insert(user);
        if (res > 0) {
            userTempCache.updateCacheInfo(user.getId(), user);
            return result;
        }
        return result.setSuccess(false).setMsg("错误:插入用户信息失败!");
    }

    @Override
    public CheckWithResult<SSOUser> update(SSOUser user) {
        CheckWithResult<SSOUser> result = new CheckWithResult<SSOUser>().setResult(user);
        int res = ssoUserMapper.update(user);
        if (res > 0) {
            //更新用户信息,刷新redis 用户缓存
            user = ssoUserMapper.getUserById(user.getId());
            userTempCache.updateCacheInfo(user.getId(), user);
            return result;
        }
        return result.setSuccess(false).setMsg("未找到用户信息更新数据");
    }

    @Override
    public SSOUser getUserByAccount(String account) {
        String userId = account2IdTempCache.getCacheInfo(account);
        return getUserById(userId);
    }

    @Override
    public SSOUser getUserById(String userId) {
        return userTempCache.getCacheInfo(userId);
    }

    /**
     * 获取用户客户端是否存在
     * @param account
     * @param clientId
     * @return
     */
    @Override
    public Integer getUserClientExist(String account, String clientId) {
        return ssoUserMapper.getUserClientExist(account, clientId);
    }

    /**
     * 密码校验 密码长度必须8~16位
     * 密码必须由数字、字母、特殊字符组合
     *
     * @param password
     * @return
     */
    public CheckWithResult<SSOUser> verifyPassword(String password) {
        CheckWithResult<SSOUser> result = new CheckWithResult<>();
        if (StringUtils.isEmpty(password)) {
            return result.setSuccess(false).setMsg("密码不允许为空");
        }
        if (password.length() < 8 || password.length() > 16) {
            return result.setSuccess(false).setMsg("密码长度必须8~16位");
        }
        if (!password.matches("(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,16}")) {
            return result.setSuccess(false).setMsg("密码必须由数字、字母、特殊字符组合");
        }
        return result;
    }

    /**
     * 设置旧密码 将最后一次密码添加到最近密码列表中，密码逗号隔开
     * 只存储最近5次密码
     *
     * @param oldPwd
     * @param pwd
     * @return
     */
    private String setOldPwd(String oldPwd, String pwd) {
        String[] pwds = StringUtils.isEmpty(oldPwd) ? new String[0] : oldPwd.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(pwds));
        if (list.size() >= 5) {
            list.remove(0);
        }
        if (!StringUtils.isEmpty(pwd)) {
            list.add(pwd);
        }
        return StringUtils.join(list.iterator(), ",");
    }
}

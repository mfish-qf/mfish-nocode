package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.api.entity.UserInfo;
import cn.com.mfish.oauth.cache.temp.Account2IdTempCache;
import cn.com.mfish.oauth.cache.temp.UserTempCache;
import cn.com.mfish.oauth.common.PasswordHelper;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import cn.com.mfish.oauth.req.ReqSsoUser;
import cn.com.mfish.oauth.service.SsoUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author qiufeng
 * @date 2020/2/13 16:51
 */
@Service
@Slf4j
public class SsoUserServiceImpl extends ServiceImpl<SsoUserMapper, SsoUser> implements SsoUserService {
    @Resource
    PasswordHelper passwordHelper;
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
    public Result<SsoUser> changePassword(String userId, String newPassword) {
        Result<SsoUser> result = verifyPassword(newPassword);
        if (!result.isSuccess()) {
            return result;
        }
        SsoUser user = userTempCache.getCacheInfo(userId);
        if (user == null) {
            String error = MessageFormat.format("错误:用户id{0}未获取到用户信息!", userId);
            log.error(error);
            return Result.fail(error);
        }

        user.setOldPassword(setOldPwd(user.getOldPassword(), user.getPassword()));
        user.setPassword(passwordHelper.encryptPassword(userId, newPassword, user.getSalt()));
        if (user.getOldPassword().indexOf(user.getPassword()) >= 0) {
            return Result.fail("错误:密码5次内不得循环使用");
        }
        user.setUpdateTime(new Date());
        if (baseMapper.updateById(user) == 1) {
            //更新用户信息,刷新redis 用户缓存
            userTempCache.updateCacheInfo(userId, user);
            return Result.ok(user, "用户密码-修改成功");
        }
        return Result.fail(user, "错误:修改失败");
    }

    @Override
    public Result<SsoUser> insert(SsoUser user) {
        int res = baseMapper.insert(user);
        if (res > 0) {
            userTempCache.updateCacheInfo(user.getId(), user);
            return Result.ok(user, "用户信息-新增成功");
        }
        return Result.fail("错误:插入用户信息失败!");
    }

    @Override
    public Result<SsoUser> updateUser(SsoUser user) {
        int res = baseMapper.updateById(user);
        if (res > 0) {
            //更新用户信息,刷新redis 用户缓存
            user = baseMapper.selectById(user.getId());
            userTempCache.updateCacheInfo(user.getId(), user);
            return Result.ok(user, "用户信息-更新成功");
        }
        return Result.fail("错误:未找到用户信息更新数据");
    }

    @Override
    public SsoUser getUserByAccount(String account) {
        String userId = account2IdTempCache.getCacheInfo(account);
        return getUserById(userId);
    }

    @Override
    public SsoUser getUserById(String userId) {
        return userTempCache.getCacheInfo(userId);
    }

    @Override
    public IPage<UserInfo> getUserList(IPage<UserInfo> iPage, ReqSsoUser reqSsoUser) {
        return iPage.setRecords(baseMapper.getUserList(iPage, reqSsoUser));
    }

    /**
     * 获取用户客户端是否存在
     *
     * @param userId
     * @param clientId
     * @return
     */
    @Override
    public boolean isUserClientExist(String userId, String clientId) {
        return baseMapper.isUserClientExist(userId, clientId) > 0;
    }

    @Override
    public boolean isAccountExist(String account) {
        return baseMapper.isAccountExist(account) > 0;
    }

    /**
     * 密码校验 密码长度必须8~16位
     * 密码必须由数字、字母、特殊字符组合
     *
     * @param password
     * @return
     */
    public Result<SsoUser> verifyPassword(String password) {
        if (StringUtils.isEmpty(password)) {
            return Result.fail("密码不允许为空");
        }
        if (password.length() < 8 || password.length() > 16) {
            return Result.fail("密码长度必须8~16位");
        }
        if (!password.matches("(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,16}")) {
            return Result.fail("密码必须由数字、字母、特殊字符组合");
        }
        return Result.ok("校验成功");
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

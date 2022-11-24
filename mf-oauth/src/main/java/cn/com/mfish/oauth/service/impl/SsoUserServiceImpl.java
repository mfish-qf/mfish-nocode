package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.api.entity.UserInfo;
import cn.com.mfish.oauth.cache.temp.Account2IdTempCache;
import cn.com.mfish.oauth.cache.temp.UserTempCache;
import cn.com.mfish.oauth.common.PasswordHelper;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import cn.com.mfish.oauth.req.ReqSsoUser;
import cn.com.mfish.oauth.service.SsoUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Result<SsoUser> insert(SsoUser user) {
        if (isAccountExist(user.getAccount(), null)) {
            return Result.fail("错误:帐号已存在-新增失败!");
        }
        user.setId(Utils.uuid32());
        user.setSalt(PasswordHelper.buildSalt());
        user.setPassword(passwordHelper.encryptPassword(user.getId(), user.getPassword(), user.getSalt()));
        int res = baseMapper.insert(user);
        if (res > 0) {
            insertUserClient(user.getId(), null);
            insertUserOrg(user.getId(), user.getOrgId());
            insertUserRole(user.getId(), user.getRoles());
            userTempCache.updateCacheInfo(user.getId(), user);
            return Result.ok(user, "用户信息-新增成功");
        }
        return Result.fail("错误:用户信息-新增失败!");
    }

    @Override
    @Transactional
    public Result<SsoUser> updateUser(SsoUser user) {
        if (baseMapper.isAccountExist(user.getPhone(), user.getId()) > 0) {
            return Result.fail("错误:手机号已存在");
        }
        //帐号名称密码不在此处更新
        String account = user.getAccount();
        user.setAccount(null);
        user.setPassword(null);
        int res = baseMapper.updateById(user);
        if (res > 0) {
            user.setAccount(account);
            baseMapper.deleteUserOrg(user.getId());
            insertUserOrg(user.getId(), user.getOrgId());
            baseMapper.deleteUserRole(user.getId());
            insertUserRole(user.getId(), user.getRoles());
            //移除缓存下次登录时会自动拉取
            userTempCache.removeCacheInfo(user.getId());
            return Result.ok(user, "用户信息-更新成功");
        }
        return Result.fail("错误:未找到用户信息更新数据");
    }

    @Override
    public boolean removeUser(String id) {
        SsoUser ssoUser = new SsoUser();
        ssoUser.setDelFlag(1).setId(id);
        if (baseMapper.updateById(ssoUser) == 1) {
            userTempCache.removeCacheInfo(id);
            return true;
        }
        return false;
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
    public List<UserInfo> getUserList(ReqSsoUser reqSsoUser) {
        return baseMapper.getUserList(reqSsoUser);
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
    public boolean isAccountExist(String account, String userId) {
        return baseMapper.isAccountExist(account, userId) > 0;
    }

    @Override
    public int insertUserRole(String userId, List<String> roles) {
        if (roles == null || roles.size() == 0) {
            return 0;
        }
        return baseMapper.insertUserRole(userId, roles);
    }

    @Override
    public int insertUserOrg(String userId, String orgList) {
        //todo 暂时只支持一个用户挂在一个组织下，后期根据情况完善是否一个用户挂在多个组织下
        if (StringUtils.isEmpty(orgList)) {
            return 0;
        }
        return baseMapper.insertUserOrg(userId, Arrays.asList(new String[]{orgList}));
    }

    public int insertUserClient(String userId, String clientId) {
        //todo 暂时写死system，后面增加客户端后为用户分配客户端
        clientId = "system";
        return baseMapper.insertUserClient(userId, Arrays.asList(new String[]{clientId}));
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

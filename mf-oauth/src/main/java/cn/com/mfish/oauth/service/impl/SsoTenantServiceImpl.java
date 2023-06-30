package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.SsoTenant;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.oauth.cache.common.ClearCache;
import cn.com.mfish.oauth.cache.temp.UserTenantTempCache;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.mapper.SsoTenantMapper;
import cn.com.mfish.oauth.req.ReqSsoTenant;
import cn.com.mfish.oauth.service.SsoOrgService;
import cn.com.mfish.oauth.service.SsoTenantService;
import cn.com.mfish.oauth.service.SsoUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.0.0
 */
@Service
@Slf4j
public class SsoTenantServiceImpl extends ServiceImpl<SsoTenantMapper, SsoTenant> implements SsoTenantService {
    @Resource
    SsoOrgService ssoOrgService;
    @Resource
    SsoUserService ssoUserService;
    @Resource
    UserTenantTempCache userTenantTempCache;
    @Resource
    ClearCache clearCache;

    @Override
    public List<TenantVo> queryList(ReqSsoTenant reqSsoTenant, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return baseMapper.queryList(reqSsoTenant);
    }

    @Override
    @Transactional
    public Result<SsoTenant> insertTenant(SsoTenant ssoTenant) {
        if (validateTenant(ssoTenant) && baseMapper.insert(ssoTenant) > 0) {
            SsoOrg org = new SsoOrg();
            org.setTenantId(AuthInfoUtils.getCurrentTenantId());
            org.setOrgName(ssoTenant.getName());
            org.setTenantId(ssoTenant.getId());
            org.setStatus(0);
            org.setOrgSort(1);
            if (!StringUtils.isEmpty(ssoTenant.getUserId())) {
                setOrgLeader(org, ssoTenant.getUserId());
            }
            Result<SsoOrg> result = ssoOrgService.insertOrg(org);
            if (!result.isSuccess()) {
                throw new MyRuntimeException(result.getMsg());
            }
            if (StringUtils.isEmpty(ssoTenant.getUserId()) || ssoUserService.insertUserOrg(ssoTenant.getUserId(), org.getId()) > 0) {
                ssoOrgService.insertOrgRole(org.getId(), ssoTenant.getRoleIds());
                return Result.ok(ssoTenant, "租户信息-添加成功!");
            }
            throw new MyRuntimeException("错误:用户组织绑定出错");
        }
        return Result.fail(ssoTenant, "租户信息-添加失败!");
    }

    /**
     * 设置租户组织用户信息
     *
     * @param org
     * @param userId
     */
    private void setOrgLeader(SsoOrg org, String userId) {
        SsoUser ssoUser = ssoUserService.getUserById(userId);
        org.setLeader(ssoUser.getAccount());
        //租户组织固定编码使用管理员帐号
        org.setOrgFixCode(ssoUser.getAccount());
        org.setEmail(ssoUser.getEmail());
        org.setPhone(ssoUser.getPhone());
    }

    /**
     * 校验租户信息
     *
     * @param ssoTenant 租户信息
     * @return
     */
    private boolean validateTenant(SsoTenant ssoTenant) {
        if (StringUtils.isEmpty(ssoTenant.getName())) {
            throw new MyRuntimeException("错误:租户名称不允许为空");
        }
        return true;
    }

    @Override
    @Transactional
    public Result<SsoTenant> updateTenant(SsoTenant ssoTenant) {
        SsoTenant oldTenant = baseMapper.selectById(ssoTenant.getId());
        if (baseMapper.updateById(ssoTenant) <= 0) {
            return Result.fail(ssoTenant, "错误:租户信息-更新失败!");
        }
        SsoOrg org = new SsoOrg();
        org.setTenantId(ssoTenant.getId());
        if (!StringUtils.isEmpty(ssoTenant.getName()) && !ssoTenant.getName().equals(oldTenant.getName())) {
            org.setOrgName(ssoTenant.getName());
        }
        boolean userChange = false;
        if (!StringUtils.isEmpty(ssoTenant.getUserId()) && !ssoTenant.getUserId().equals(oldTenant.getUserId())) {
            setOrgLeader(org, ssoTenant.getUserId());
            userChange = true;
        }
        SsoOrg oldOrg = ssoOrgService.getOne(new LambdaQueryWrapper<SsoOrg>().eq(SsoOrg::getTenantId, org.getTenantId()));
        if (oldOrg == null) {
            throw new MyRuntimeException("错误:删除用户组织关系失败");
        }
        org.setId(oldOrg.getId());
        Result<SsoOrg> result = ssoOrgService.updateOrg(org);
        if (!result.isSuccess()) {
            throw new MyRuntimeException("错误:组织信息-更新失败");
        }
        if (ssoTenant.getRoleIds() != null) {
            log.info(MessageFormat.format("删除组织角色数量:{0}条", ssoOrgService.deleteOrgRole(oldOrg.getId())));
            ssoOrgService.insertOrgRole(org.getId(), ssoTenant.getRoleIds());
        }

        //移除租户相关用户的租户信息
        List<String> userIds = baseMapper.getTenantUser(ssoTenant.getId());
        userTenantTempCache.removeMoreCache(userIds.stream().map(RedisPrefix::buildUser2TenantsKey).collect(Collectors.toList()));

        //如果用户变更删除之前用户组织关系
        if (!userChange) {
            return Result.ok(ssoTenant, "租户信息-编辑成功!");
        }
        ssoUserService.deleteUserOrg(oldTenant.getUserId(), org.getId());
        if (ssoUserService.insertUserOrg(ssoTenant.getUserId(), org.getId()) > 0) {
            clearCache.removeUserAuthCache(Collections.singletonList(oldTenant.getUserId()));
            return Result.ok(ssoTenant, "租户信息-编辑成功!");
        }
        throw new MyRuntimeException("错误:删除用户组织关系失败");
    }

    @Override
    @Transactional
    public Result<Boolean> deleteTenant(String id) {
        if (AuthInfoUtils.isSuperTenant(id)) {
            return Result.fail(false, "错误:系统租户不允许删除");
        }
        if (baseMapper.updateById(new SsoTenant().setDelFlag(1).setId(id)) > 0
                && ssoOrgService.update(new SsoOrg().setDelFlag(1), new LambdaQueryWrapper<SsoOrg>().eq(SsoOrg::getTenantId, id))) {
            return Result.ok(true, "租户信息-删除成功!");
        }
        throw new MyRuntimeException("错误:租户信息-删除失败!");
    }

    @Override
    public boolean isTenantMaster(String userId, String tenantId) {
        return baseMapper.isTenantMaster(userId, tenantId) > 0;
    }

    @Override
    public List<TenantVo> getTenantByRoleCode(String roleCode) {
        return baseMapper.getTenantByRoleCode(roleCode);
    }
}

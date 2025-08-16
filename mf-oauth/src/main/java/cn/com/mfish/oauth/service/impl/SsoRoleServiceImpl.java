package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.SsoRole;
import cn.com.mfish.common.oauth.service.SsoRoleService;
import cn.com.mfish.oauth.cache.common.ClearCache;
import cn.com.mfish.oauth.mapper.SsoRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @version: V2.1.0
 */
@Service
@Slf4j
public class SsoRoleServiceImpl extends ServiceImpl<SsoRoleMapper, SsoRole> implements SsoRoleService {

    @Resource
    ClearCache clearCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<SsoRole> insertRole(SsoRole ssoRole) {
        validateRole(ssoRole);
        if (baseMapper.insert(ssoRole) >= 1) {
            if (insertRoleMenus(ssoRole)) {
                return Result.ok(ssoRole, "角色信息-添加成功");
            }
            throw new MyRuntimeException("错误:插入角色菜单失败");
        }
        throw new MyRuntimeException("错误:角色信息-添加失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<SsoRole> updateRole(SsoRole ssoRole) {
        validateRole(ssoRole);
        if (baseMapper.updateById(ssoRole) >= 1) {
            log.info(MessageFormat.format("删除角色菜单数量:{0}条",
                    baseMapper.deleteRoleMenus(ssoRole.getId())));
            if (insertRoleMenus(ssoRole)) {
                CompletableFuture.runAsync(() -> removeRoleCache(ssoRole.getId()));
                return Result.ok(ssoRole, "角色信息-修改成功");
            }
            throw new MyRuntimeException("错误:更新角色菜单失败");
        }
        throw new MyRuntimeException("错误:更新角色失败");
    }

    private void validateRole(SsoRole ssoRole) {
        if (roleCodeExist(ssoRole.getId(), ssoRole.getTenantId(), ssoRole.getRoleCode())) {
            throw new MyRuntimeException("错误:角色编码已存在");
        }
    }


    private boolean insertRoleMenus(SsoRole ssoRole) {
        if (ssoRole.getMenus() == null || ssoRole.getMenus().isEmpty()) {
            return true;
        }
        int count = baseMapper.insertRoleMenus(ssoRole.getId(), ssoRole.getMenus());
        log.info(MessageFormat.format("插入角色菜单数量:{0}条", count));
        return count > 0;
    }

    @Override
    public Result<Boolean> deleteRole(String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.fail(false, "错误:删除失败-组织ID不允许为空!");
        }
        if (baseMapper.updateById(new SsoRole().setDelFlag(1).setId(id)) == 1) {
            CompletableFuture.runAsync(() -> removeRoleCache(id));
            log.info(MessageFormat.format("删除角色成功,角色ID:{0}", id));
            return Result.ok(true, "角色信息-删除成功!");
        }
        log.error(MessageFormat.format("错误:删除角色失败,角色ID:{0}", id));
        return Result.fail(false, "错误:角色信息-删除失败!");
    }

    /**
     * 移除角色相关缓存
     *
     * @param roleId 角色id
     */
    private void removeRoleCache(String roleId) {
        //查询角色对应的用户并删除角色权限缓存
        List<String> list = baseMapper.getRoleUsers(Collections.singletonList(roleId), true);
        clearCache.removeUserAuthCache(list);
    }

    /**
     * 判断roleCode是否存在
     *
     * @param roleCode 角色编码
     * @return 返回是否存在
     */
    @Override
    public boolean roleCodeExist(String roleId, String tenantId, String roleCode) {
        return baseMapper.roleCodeExist(roleId, tenantId, roleCode) > 0;
    }

    @Override
    public List<String> getRoleMenus(String roleId) {
        return baseMapper.getRoleMenus(roleId);
    }

    @Override
    public boolean isTenantRole(String roleId, String tenantId) {
        if (StringUtils.isEmpty(roleId)) {
            throw new MyRuntimeException("错误:角色ID不允许为空");
        }
        return baseMapper.isTenantRole(roleId, tenantId) > 0;
    }

    @Override
    public Result<List<String>> getRoleIdsByCode(String tenantId, List<String> roleCodes) {
        return Result.ok(baseMapper.getRoleIdsByCode(tenantId, roleCodes), "通过角色编码获取角色ID成功");
    }

    @Override
    public Result<List<String>> getRoleUsers(String tenantId, List<String> roleCodes) {
        List<String> roleIds = baseMapper.getRoleIdsByCode(tenantId, roleCodes);
        List<String> list = baseMapper.getRoleUsers(roleIds, false);
        if (list.isEmpty()) {
            return Result.fail("错误：未获取到用户");
        }
        return Result.ok(list, "获取用户成功");
    }

    @Override
    public Result<List<SsoRole>> queryByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return Result.fail(null, "错误:id不允许为空");
        }
        String[] idList = ids.split(",");
        return Result.ok(baseMapper.selectList(new LambdaQueryWrapper<SsoRole>().in(SsoRole::getId, Arrays.asList(idList))), "角色列表-查询成功!");
    }
}

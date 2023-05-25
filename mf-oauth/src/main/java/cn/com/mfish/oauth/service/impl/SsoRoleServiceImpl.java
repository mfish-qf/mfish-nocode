package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.oauth.cache.temp.UserPermissionTempCache;
import cn.com.mfish.oauth.cache.temp.UserRoleTempCache;
import cn.com.mfish.oauth.entity.SsoRole;
import cn.com.mfish.oauth.mapper.SsoRoleMapper;
import cn.com.mfish.oauth.service.SsoRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.1
 */
@Service
@Slf4j
public class SsoRoleServiceImpl extends ServiceImpl<SsoRoleMapper, SsoRole> implements SsoRoleService {

    @Resource
    UserRoleTempCache userRoleTempCache;
    @Resource
    UserPermissionTempCache userPermissionTempCache;

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
                String clientId = AuthInfoUtils.getCurrentClientId();
                CompletableFuture.runAsync(() -> removeRoleCache(ssoRole.getId(), clientId));
                return Result.ok(ssoRole, "角色信息-修改成功");
            }
            throw new MyRuntimeException("错误:更新角色菜单失败");
        }
        throw new MyRuntimeException("错误:更新角色失败");
    }

    private boolean validateRole(SsoRole ssoRole) {
        if (StringUtils.isEmpty(ssoRole.getClientId())) {
            ssoRole.setClientId(AuthInfoUtils.getCurrentClientId());
        }
        if (StringUtils.isEmpty(ssoRole.getClientId())) {
            throw new MyRuntimeException("错误:客户端ID不存在");
        }
        if (roleCodeExist(ssoRole.getClientId(), ssoRole.getId(), ssoRole.getRoleCode())) {
            throw new MyRuntimeException("错误:角色编码已存在");
        }
        return true;
    }


    private boolean insertRoleMenus(SsoRole ssoRole) {
        if (ssoRole.getMenus() == null || ssoRole.getMenus().size() == 0) {
            return true;
        }
        int count = baseMapper.insertRoleMenus(ssoRole.getId(), ssoRole.getMenus());
        log.info(MessageFormat.format("插入角色菜单数量:{0}条", count));
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteRole(String id) {
        if (baseMapper.updateById(new SsoRole().setDelFlag(1).setId(id)) == 1) {
            String clientId = AuthInfoUtils.getCurrentClientId();
            CompletableFuture.runAsync(() -> removeRoleCache(id, clientId));
            log.info(MessageFormat.format("删除角色成功,角色ID:{0}", id));
            return true;
        }
        log.error(MessageFormat.format("错误:删除角色失败,角色ID:{0}", id));
        return false;
    }

    /**
     * 移除角色相关缓存
     *
     * @param roleId
     */
    private void removeRoleCache(String roleId, String clientId) {
        //查询角色对应的用户并删除角色权限缓存
        List<String> list = baseMapper.getRoleUser(roleId);
        List<String> roleKeys = new ArrayList<>();
        List<String> permissionKeys = new ArrayList<>();
        for (String userId : list) {
            roleKeys.add(RedisPrefix.buildUser2RolesKey(userId, clientId));
            permissionKeys.add(RedisPrefix.buildUser2PermissionsKey(userId, clientId));
        }
        userRoleTempCache.removeMoreCache(roleKeys);
        userPermissionTempCache.removeMoreCache(permissionKeys);
    }

    /**
     * 判断roleCode是否存在
     *
     * @param roleCode
     * @return
     */
    @Override
    public boolean roleCodeExist(String clientId, String roleId, String roleCode) {
        return baseMapper.roleCodeExist(clientId, roleId, roleCode) > 0;
    }

    @Override
    public List<String> getRoleMenus(String roleId) {
        return baseMapper.getRoleMenus(roleId);
    }
}

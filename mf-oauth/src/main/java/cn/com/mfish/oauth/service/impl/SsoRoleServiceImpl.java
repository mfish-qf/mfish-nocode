package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SsoRole;
import cn.com.mfish.oauth.mapper.SsoRoleMapper;
import cn.com.mfish.oauth.req.ReqSsoRole;
import cn.com.mfish.oauth.service.SsoRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Service
@Slf4j
public class SsoRoleServiceImpl extends ServiceImpl<SsoRoleMapper, SsoRole> implements SsoRoleService {

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
                return Result.ok(ssoRole, "角色信息-修改成功");
            }
            throw new MyRuntimeException("错误:更新角色菜单失败");
        }
        throw new MyRuntimeException("错误:更新角色失败");
    }

    private boolean validateRole(SsoRole ssoRole) {
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
            log.info(MessageFormat.format("删除角色成功,角色ID:{0}", id));
            return true;
        }
        log.error(MessageFormat.format("错误:删除角色失败,角色ID:{0}", id));
        return false;
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
    public List<SsoRole> getRoleList(ReqSsoRole reqSsoRole) {
        return baseMapper.getRoleList(reqSsoRole);
    }
}

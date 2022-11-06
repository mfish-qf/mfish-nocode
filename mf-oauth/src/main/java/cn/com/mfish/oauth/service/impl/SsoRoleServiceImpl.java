package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.oauth.entity.SsoRole;
import cn.com.mfish.oauth.mapper.SsoRoleMapper;
import cn.com.mfish.oauth.service.SsoRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

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
    public boolean insertRole(SsoRole ssoRole) {
        if (baseMapper.insert(ssoRole) >= 1) {
            log.info(MessageFormat.format("插入角色菜单数量:{0}条",
                    baseMapper.insertRoleMenus(ssoRole.getId(), ssoRole.getMenuIds())));
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SsoRole ssoRole) {
        if (baseMapper.updateById(ssoRole) >= 1) {
            log.info(MessageFormat.format("删除角色菜单数量:{0}条",
                    baseMapper.deleteRoleMenus(ssoRole.getId())));
            log.info(MessageFormat.format("插入角色菜单数量:{0}条",
                    baseMapper.insertRoleMenus(ssoRole.getId(), ssoRole.getMenuIds())));
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteRole(String id) {
        if (baseMapper.deleteById(id) >= 1) {
            baseMapper.deleteRoleMenus(id);
            log.info(MessageFormat.format("删除角色记录,角色ID:{0}", id));
            return true;
        }
        log.error(MessageFormat.format("警告:未删除记录,角色ID:{0}", id));
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

        return false;
    }
}

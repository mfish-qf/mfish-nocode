package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.oauth.cache.temp.UserPermissionTempCache;
import cn.com.mfish.oauth.entity.SsoMenu;
import cn.com.mfish.oauth.mapper.SsoMenuMapper;
import cn.com.mfish.oauth.req.ReqSsoMenu;
import cn.com.mfish.oauth.service.SsoMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @Date: 2022-09-21
 * @Version: V1.0
 */
@Service
public class SsoMenuServiceImpl extends ServiceImpl<SsoMenuMapper, SsoMenu> implements SsoMenuService {

    @Resource
    UserPermissionTempCache userPermissionTempCache;

    @Override
    public boolean insertMenu(SsoMenu ssoMenu) {
        if (StringUtils.isEmpty(ssoMenu.getParentId())) {
            ssoMenu.setParentId("");
        }
        return baseMapper.insertMenu(ssoMenu) == 1;
    }

    @Override
    public List<SsoMenu> queryMenu(ReqSsoMenu reqSsoMenu, String userId) {
        //如果是超户获取所有菜单
        if (OauthUtils.isSuper(userId)) {
            userId = null;
        }
        Integer level = baseMapper.queryMaxMenuLevel(reqSsoMenu, userId);
        List<Integer> list = new ArrayList<>();
        if (level != null) {
            for (int i = 1; i < level; i++) {
                list.add(i);
            }
        }
        return baseMapper.queryMenu(reqSsoMenu, list, userId);
    }

    @Override
    public Result<SsoMenu> updateMenu(SsoMenu ssoMenu) {
        if (StringUtils.isEmpty(ssoMenu.getClientId())) {
            ssoMenu.setClientId(AuthInfoUtils.getCurrentClientId());
        }
        if (baseMapper.updateById(ssoMenu) > 0) {
            CompletableFuture.runAsync(() -> removeMenuCache(ssoMenu));
            return Result.ok(ssoMenu, "菜单表-编辑成功!");
        }
        return Result.fail("错误:菜单表-编辑失败!");
    }

    @Override
    @Transactional
    public Result<Boolean> deleteMenu(String menuId) {
        if (StringUtils.isEmpty(menuId)) {
            return Result.fail(false, "错误:菜单ID不允许为空");
        }
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<SsoMenu>().eq(SsoMenu::getParentId, menuId));
        if (count > 0) {
            return Result.fail(false, "错误:菜单包含子节点，不允许删除");
        }
        if (baseMapper.deleteById(menuId) > 0) {
            SsoMenu ssoMenu = baseMapper.selectById(menuId);
            if (StringUtils.isEmpty(ssoMenu.getClientId())) {
                ssoMenu.setClientId(AuthInfoUtils.getCurrentClientId());
            }
            CompletableFuture.runAsync(() -> removeMenuCache(ssoMenu));
            baseMapper.deleteMenuRoles(menuId);
            return Result.ok("菜单表-删除成功!");
        }
        return Result.fail("错误:菜单表-删除失败!");
    }

    /**
     * 按钮修改移除缓存中按钮权限
     *
     * @param ssoMenu 菜单
     */
    private void removeMenuCache(SsoMenu ssoMenu) {
        //如果菜单类型为按钮，清空相关用户缓存
        if (2 != ssoMenu.getMenuType()) {
            return;
        }
        List<String> list = baseMapper.queryMenuUser(ssoMenu.getId());
        userPermissionTempCache.removeMoreCache(list.stream()
                .map(item -> RedisPrefix.buildUser2PermissionsKey(item, ssoMenu.getClientId())).collect(Collectors.toList()));
    }

}

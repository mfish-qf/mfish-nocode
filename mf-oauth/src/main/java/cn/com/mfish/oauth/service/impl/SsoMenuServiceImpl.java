package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.oauth.cache.common.ClearCache;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.oauth.mapper.SsoMenuMapper;
import cn.com.mfish.common.oauth.req.ReqSsoMenu;
import cn.com.mfish.common.oauth.service.SsoMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @date: 2022-09-21
 * @Version: V1.3.2
 */
@Service
public class SsoMenuServiceImpl extends ServiceImpl<SsoMenuMapper, SsoMenu> implements SsoMenuService {

    @Resource
    ClearCache clearCache;

    @Override
    public Result<SsoMenu> insertMenu(SsoMenu ssoMenu) {
        Result<SsoMenu> result = verifyMenu(ssoMenu);
        if (!result.isSuccess()) {
            return result;
        }
        if (baseMapper.insertMenu(ssoMenu) == 1) {
            return Result.ok(ssoMenu, "菜单表-添加成功!");
        }
        return Result.fail("错误:菜单表-添加失败!");
    }

    @Override
    public Result<List<SsoMenu>> queryMenuTree(ReqSsoMenu reqSsoMenu, String userId) {
        List<SsoMenu> list = queryMenu(reqSsoMenu, userId);
        List<SsoMenu> menuTrees = new ArrayList<>();
        TreeUtils.buildTree("", list, menuTrees, SsoMenu.class);
        return Result.ok(menuTrees, "菜单表-查询成功!");
    }

    @Override
    public List<SsoMenu> queryMenu(ReqSsoMenu reqSsoMenu, String userId) {
        List<String> roleIds = new ArrayList<>();
        //如果是超户获取所有菜单
        if (!StringUtils.isEmpty(userId) && !AuthInfoUtils.isSuper(userId)) {
            roleIds = OauthUtils.getRoles().stream().map(UserRole::getId).collect(Collectors.toList());
            if (roleIds.isEmpty()) {
                return new ArrayList<>();
            }
        }
        Integer level = baseMapper.queryMaxMenuLevel(reqSsoMenu, roleIds);
        List<Integer> list = new ArrayList<>();
        if (level != null) {
            for (int i = 1; i < level; i++) {
                list.add(i);
            }
        }
        List<SsoMenu> menus = baseMapper.queryMenu(reqSsoMenu, list, roleIds);
        //菜单节点是从底部往上寻找，如果权限只设置了子节点，父节点并未存储
        //所以根据菜单类型展示时，先全部查出后再过滤不需要的类型
        if (reqSsoMenu.getMenuType() != null) {
            return menus.stream().filter((menu) -> menu.getMenuType() <= reqSsoMenu.getMenuType()).collect(Collectors.toList());
        }
        return menus;
    }

    @Override
    @Transactional
    public Result<SsoMenu> updateMenu(SsoMenu ssoMenu) {
        Result<SsoMenu> result = verifyMenu(ssoMenu);
        if (!result.isSuccess()) {
            return result;
        }
        SsoMenu oldMenu = baseMapper.selectById(ssoMenu.getId());
        if (oldMenu == null) {
            throw new MyRuntimeException("错误:未找到菜单");
        }
        boolean success;
        if ((StringUtils.isEmpty(oldMenu.getParentId()) && StringUtils.isEmpty(ssoMenu.getParentId())) ||
                (!StringUtils.isEmpty(oldMenu.getParentId()) && oldMenu.getParentId().equals(ssoMenu.getParentId()))) {
            success = baseMapper.updateById(ssoMenu) > 0;
        } else {
            List<SsoMenu> list = baseMapper.selectList(new LambdaQueryWrapper<SsoMenu>()
                    .likeRight(SsoMenu::getMenuCode, oldMenu.getMenuCode()).orderByAsc(SsoMenu::getMenuCode));
            if (list == null || list.isEmpty()) {
                throw new MyRuntimeException("错误:未查询到菜单");
            }
            list.set(0, ssoMenu);
            //父节点发生变化，重新生成序列
            baseMapper.deleteByIds(list.stream().map(SsoMenu::getId).collect(Collectors.toList()));
            for (SsoMenu menu : list) {
                if (baseMapper.insertMenu(menu) <= 0) {
                    throw new MyRuntimeException("错误:更新菜单失败");
                }
            }
            success = true;
        }
        if (success) {
            CompletableFuture.runAsync(() -> removeMenuCache(ssoMenu));
            return Result.ok(ssoMenu, "菜单表-编辑成功!");
        }
        throw new MyRuntimeException("错误:更新菜单失败");
    }

    private Result<SsoMenu> verifyMenu(SsoMenu ssoMenu) {
        //parentId为空时，设置为空字符串
        if (StringUtils.isEmpty(ssoMenu.getParentId())) {
            ssoMenu.setParentId("");
        }
        if (!StringUtils.isEmpty(ssoMenu.getPermissions())) {
            //替换中文逗号
            ssoMenu.setPermissions(ssoMenu.getPermissions().replace("，", ","));
        }
        if (!StringUtils.isEmpty(ssoMenu.getParentId())) {
            SsoMenu pMenu = baseMapper.selectById(ssoMenu.getParentId());
            //非目录级别的菜单，父菜单类型必须小于子菜单类型
            if (pMenu.getMenuType() > 0 && pMenu.getMenuType() >= ssoMenu.getMenuType()) {
                return Result.fail("错误:上级菜单选择不正确");
            }
        }
        if (!StringUtils.isEmpty(ssoMenu.getRoutePath()) && baseMapper.exists(new LambdaQueryWrapper<SsoMenu>()
                .eq(SsoMenu::getRoutePath, ssoMenu.getRoutePath())
                .eq(SsoMenu::getParentId, ssoMenu.getParentId())
                .ne(!StringUtils.isEmpty(ssoMenu.getId()), SsoMenu::getId, ssoMenu.getId()))) {
            return Result.fail("错误：路由地址已存在");
        }
        return Result.ok("菜单校验成功");
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
        SsoMenu ssoMenu = baseMapper.selectById(menuId);
        if (baseMapper.deleteById(menuId) > 0) {
            CompletableFuture.runAsync(() -> removeMenuCache(ssoMenu));
            baseMapper.deleteMenuRoles(menuId);
            return Result.ok("菜单表-删除成功!");
        }
        return Result.fail("错误:菜单表-删除失败!");
    }

    @Override
    public Result<Boolean> routeExist(String routePath, String parentId) {
        if (baseMapper.exists(new LambdaQueryWrapper<SsoMenu>().eq(SsoMenu::getRoutePath, routePath)
                .eq(SsoMenu::getParentId, parentId))) {
            return Result.ok(true, "路由地址已存在");
        }
        return Result.fail(false, "路由地址不存在");
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
        CompletableFuture.runAsync(() -> removeUserAuthCache(ssoMenu.getId()));
    }

    private void removeUserAuthCache(String menuId) {
        List<String> list = baseMapper.queryMenuUser(menuId);
        clearCache.removeUserAuthCache(list);
    }
}

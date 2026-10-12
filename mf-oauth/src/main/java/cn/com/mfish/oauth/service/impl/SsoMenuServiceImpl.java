package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.api.vo.MenuRouteVo;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.oauth.cache.common.ClearCache;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.oauth.mapper.SsoMenuMapper;
import cn.com.mfish.common.oauth.api.req.ReqSsoMenu;
import cn.com.mfish.common.oauth.service.SsoMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @date: 2022-09-21
 * @version: V2.4.1
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

    @Override
    public Result<List<MenuRouteVo>> queryRoutePaths(String keyword) {
        // 查询所有目录和菜单（排除按钮 menuType=2），按钮无路由地址
        List<SsoMenu> menus = baseMapper.selectList(new LambdaQueryWrapper<SsoMenu>()
                .lt(SsoMenu::getMenuType, 2)
                .isNotNull(SsoMenu::getRoutePath));
        // 以 id 为 key 构建 map，便于通过 parentId 查找父菜单
        Map<String, SsoMenu> menuMap = new LinkedHashMap<>();
        for (SsoMenu menu : menus) {
            menuMap.put(menu.getId(), menu);
        }
        // 递归拼接完整路由地址，子菜单路由拼接父菜单路由
        // 例如：父 /demo + 子 /demo-leave-apply → /demo/demo-leave-apply
        List<MenuRouteVo> routePaths = new ArrayList<>();
        for (SsoMenu menu : menus) {
            String fullPath = buildFullPath(menu, menuMap);
            if (StringUtils.isNotEmpty(fullPath)) {
                routePaths.add(new MenuRouteVo(menu.getMenuName(), fullPath));
            }
        }
        // 关键词过滤：支持逗号分隔的多个关键词（OR 匹配，忽略大小写）
        // 例如 keyword="大屏,可视化,screen" → 菜单名称包含任一关键词即匹配
        if (StringUtils.isNotEmpty(keyword)) {
            String[] keywords = keyword.split("[,，]");
            List<String> lowerKeywords = Arrays.stream(keywords)
                    .map(String::trim)
                    .filter(k -> !k.isEmpty())
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            routePaths = routePaths.stream()
                    .filter(vo -> vo.getMenuName() != null
                            && lowerKeywords.stream()
                                    .anyMatch(kw -> vo.getMenuName().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }
        return Result.ok(routePaths, "路由地址查询成功");
    }

    /**
     * 递归构建菜单的完整路由地址（子路由拼接父路由）
     * <p>
     * 示例：父菜单 /system → 子菜单 /menu → 完整路径 /system/menu
     * 顶层菜单（parentId 为空）直接返回自身 routePath。
     * </p>
     *
     * @param menu    当前菜单
     * @param menuMap 菜单 id → 菜单对象 映射
     * @return 完整路由地址，无路由时返回空字符串
     */
    private String buildFullPath(SsoMenu menu, Map<String, SsoMenu> menuMap) {
        if (menu == null || StringUtils.isEmpty(menu.getRoutePath())) {
            return "";
        }
        String parentId = menu.getParentId();
        if (StringUtils.isEmpty(parentId)) {
            // 顶层菜单，直接返回路由
            return menu.getRoutePath();
        }
        SsoMenu parent = menuMap.get(parentId);
        if (parent == null) {
            // 父菜单不存在（可能被删除或权限不足），返回自身路由
            return menu.getRoutePath();
        }
        String parentPath = buildFullPath(parent, menuMap);
        if (StringUtils.isEmpty(parentPath)) {
            return menu.getRoutePath();
        }
        // 拼接：父路径 + 子路径（处理重复的斜杠）
        String childPath = menu.getRoutePath();
        if (parentPath.endsWith("/") && childPath.startsWith("/")) {
            return parentPath + childPath.substring(1);
        }
        if (!parentPath.endsWith("/") && !childPath.startsWith("/")) {
            return parentPath + "/" + childPath;
        }
        return parentPath + childPath;
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

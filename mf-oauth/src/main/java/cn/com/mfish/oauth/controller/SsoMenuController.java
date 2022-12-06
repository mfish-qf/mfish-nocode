package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.annotation.RequiresRoles;
import cn.com.mfish.oauth.cache.temp.UserPermissionTempCache;
import cn.com.mfish.oauth.entity.SsoMenu;
import cn.com.mfish.oauth.req.ReqSsoMenu;
import cn.com.mfish.oauth.service.OAuth2Service;
import cn.com.mfish.oauth.service.SsoMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @Date: 2022-09-21
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "菜单权限表")
@RestController
@RequestMapping("/menu")
public class SsoMenuController {
    @Resource
    SsoMenuService ssoMenuService;
    @Resource
    OAuth2Service oAuth2Service;
    @Resource
    UserPermissionTempCache userPermissionTempCache;

    /**
     * 分页列表查询
     *
     * @param reqSsoMenu
     * @return
     */
    @ApiOperation(value = "菜单权限表-分页列表查询", notes = "菜单权限表-分页列表查询")
    @GetMapping
    public Result<List<SsoMenu>> queryList(ReqSsoMenu reqSsoMenu) {
        List<SsoMenu> list = ssoMenuService.queryMenu(reqSsoMenu, oAuth2Service.getCurrentUser());
        return Result.ok(list, "菜单权限表-查询成功!");
    }

    @ApiOperation(value = "获取菜单树")
    @GetMapping("/tree")
    @RequiresPermissions("sys:menu:query")
    @RequiresRoles("admin")
    public Result<List<SsoMenu>> queryMenuTree(ReqSsoMenu reqSsoMenu) {
        List<SsoMenu> list = ssoMenuService.queryMenu(reqSsoMenu, oAuth2Service.getCurrentUser());
        List<SsoMenu> menuTrees = new ArrayList<>();
        TreeUtils.buildTree("", list, menuTrees, SsoMenu.class);
        return Result.ok(menuTrees, "菜单权限表-查询成功!");
    }

    /**
     * 添加
     *
     * @param ssoMenu
     * @return
     */
    @Log(title = "菜单权限表-添加", operateType = OperateType.INSERT)
    @ApiOperation(value = "菜单权限表-添加", notes = "菜单权限表-添加")
    @PostMapping
    public Result<SsoMenu> add(@RequestBody SsoMenu ssoMenu) {
        if (ssoMenuService.insertMenu(ssoMenu)) {
            return Result.ok(ssoMenu, "菜单权限表-添加成功!");
        }
        return Result.fail("错误:菜单权限表-添加失败!");
    }

    /**
     * 编辑
     *
     * @param ssoMenu
     * @return
     */
    @Log(title = "菜单权限表-编辑", operateType = OperateType.UPDATE)
    @ApiOperation(value = "菜单权限表-编辑", notes = "菜单权限表-编辑")
    @PutMapping
    public Result<SsoMenu> edit(@RequestBody SsoMenu ssoMenu) {
        if (ssoMenuService.updateById(ssoMenu)) {
            removeCache(ssoMenu);
            return Result.ok(ssoMenu, "菜单权限表-编辑成功!");
        }
        return Result.fail("错误:菜单权限表-编辑失败!");
    }

    /**
     * 按钮修改移除缓存中按钮权限
     *
     * @param ssoMenu
     */
    private void removeCache(SsoMenu ssoMenu) {
        if (2 == ssoMenu.getMenuType()) {
            List<String> list = ssoMenuService.queryMenuUser(ssoMenu.getId());
            String clientId = AuthInfoUtils.getCurrentClientId();
            userPermissionTempCache.removeOneCache(list.stream().map(item -> item + clientId).toArray(String[]::new));
        }
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "菜单权限表-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "菜单权限表-通过id删除", notes = "菜单权限表-通过id删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (ssoMenuService.removeById(id)) {
            log.info(MessageFormat.format("删除菜单成功,菜单ID:{0}", id));
            return Result.ok("菜单权限表-删除成功!");
        }
        log.error(MessageFormat.format("错误:删除菜单失败,菜单ID:{0}", id));
        return Result.fail("错误:菜单权限表-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @Log(title = "菜单权限表-批量删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "菜单权限表-批量删除", notes = "菜单权限表-批量删除")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.ssoMenuService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok("菜单权限表-批量删除成功!");
        }
        return Result.fail("错误:菜单权限表-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "菜单权限表-通过id查询", notes = "菜单权限表-通过id查询")
    @GetMapping("/{id}")
    public Result<SsoMenu> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        SsoMenu ssoMenu = ssoMenuService.getById(id);
        return Result.ok(ssoMenu, "菜单权限表-查询成功!");
    }
}

package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.annotation.InnerUser;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.req.ReqSsoMenu;
import cn.com.mfish.common.oauth.service.SsoMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 菜单表
 * @Author: mfish
 * @date: 2022-09-21
 * @Version: V1.3.0
 */
@Slf4j
@Tag(name = "菜单表")
@RestController
@RequestMapping("/menu")
public class SsoMenuController {
    @Resource
    SsoMenuService ssoMenuService;

    /**
     * 分页列表查询
     *
     * @param reqSsoMenu
     * @return
     */
    @Operation(summary = "菜单表-分页列表查询", description = "菜单表-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:menu:query")
    public Result<List<SsoMenu>> queryList(ReqSsoMenu reqSsoMenu) {
        return ssoMenuService.queryMenuTree(reqSsoMenu, null);
    }

    @Operation(summary = "获取所有菜单树")
    @GetMapping("/tree")
    public Result<List<SsoMenu>> queryMenuTree(ReqSsoMenu reqSsoMenu) {
        return ssoMenuService.queryMenuTree(reqSsoMenu, null);
    }

    @Operation(summary = "获取角色树-左侧菜单")
    @GetMapping("/roleTree")
    public Result<List<SsoMenu>> queryRoleMenuTree() {
        return ssoMenuService.queryMenuTree(new ReqSsoMenu().setNoButton(true), AuthInfoUtils.getCurrentUserId());
    }

    /**
     * 添加
     *
     * @param ssoMenu
     * @return
     */
    @Log(title = "菜单表-添加", operateType = OperateType.INSERT)
    @Operation(summary = "菜单表-添加", description = "菜单表-添加")
    @PostMapping
    @RequiresPermissions("sys:menu:insert")
    public Result<SsoMenu> add(@RequestBody SsoMenu ssoMenu) {
        return ssoMenuService.insertMenu(ssoMenu);
    }

    /**
     * 编辑
     *
     * @param ssoMenu
     * @return
     */
    @Log(title = "菜单表-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "菜单表-编辑", description = "菜单表-编辑")
    @PutMapping
    @RequiresPermissions("sys:menu:update")
    public Result<SsoMenu> edit(@RequestBody SsoMenu ssoMenu) {
        return ssoMenuService.updateMenu(ssoMenu);
    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "菜单表-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "菜单表-通过id删除", description = "菜单表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:menu:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return ssoMenuService.deleteMenu(id);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @Operation(summary = "菜单表-通过id查询", description = "菜单表-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:menu:query")
    public Result<SsoMenu> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        SsoMenu ssoMenu = ssoMenuService.getById(id);
        return Result.ok(ssoMenu, "菜单表-查询成功!");
    }

    @Operation(summary = "判断路由是否存在")
    @GetMapping("/routeExist")
    @InnerUser
    @Parameter(name = "路由地址", required = true)
    public Result<Boolean> routeExist(@RequestParam String routePath) {
        return ssoMenuService.routeExist(routePath);
    }
}

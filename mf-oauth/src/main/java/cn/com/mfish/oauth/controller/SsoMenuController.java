package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.oauth.entity.SsoMenu;
import cn.com.mfish.oauth.req.ReqSsoMenu;
import cn.com.mfish.oauth.service.SsoMenuService;
import cn.com.mfish.oauth.vo.MenuTree;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@RequestMapping("/ssoMenu")
public class SsoMenuController {
    @Resource
    private SsoMenuService ssoMenuService;

    /**
     * 分页列表查询
     *
     * @param reqSsoMenu
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "菜单权限表-分页列表查询", notes = "菜单权限表-分页列表查询")
    @GetMapping
    public Result<IPage<SsoMenu>> queryPageList(ReqSsoMenu reqSsoMenu,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<SsoMenu> pageList = ssoMenuService.page(new Page<>(pageNo, pageSize), buildQueryCondition(reqSsoMenu));
        return Result.ok(pageList, "查询成功!");
    }

    @ApiOperation(value = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<MenuTree>> queryMenuTree(ReqSsoMenu reqSsoMenu) {
        //TODO 过滤查询需要递归查询上级树节点，后期优化
        List<SsoMenu> list = ssoMenuService.list(buildQueryCondition(reqSsoMenu));
        List<MenuTree> menuTrees = new ArrayList<>();
        buildMenuTree("", list, menuTrees);
        return Result.ok(menuTrees, "菜单权限表-查询成功!");
    }

    /**
     * 构建菜单树
     *
     * @param pId
     * @param menus
     * @param menuTrees
     */
    private void buildMenuTree(String pId, List<SsoMenu> menus, List<MenuTree> menuTrees) {
        if (menus == null || menus.size() == 0) {
            return;
        }
        for (SsoMenu menu : menus) {
            if (!menu.getParentId().equals(pId)) {
                continue;
            }
            MenuTree parent = new MenuTree();
            BeanUtils.copyProperties(menu, parent);
            menuTrees.add(parent);
            List<MenuTree> child = new ArrayList<>();
            parent.setChildren(child);
            if (menus.stream().anyMatch(p -> p.getParentId().equals(menu.getId()))) {
                buildMenuTree(menu.getId(), menus, child);
            }
            //没有child设置为空防止前端显示多一个折叠符号
            if (child.size() == 0) {
                parent.setChildren(null);
            }
        }
    }

    /**
     * 构建菜单查询条件
     *
     * @param reqSsoMenu
     * @return
     */
    private LambdaQueryWrapper<SsoMenu> buildQueryCondition(ReqSsoMenu reqSsoMenu) {
        return new LambdaQueryWrapper<SsoMenu>()
                .eq(reqSsoMenu.getClientId() != null, SsoMenu::getClientId, reqSsoMenu.getClientId())
                .like(reqSsoMenu.getMenuName() != null, SsoMenu::getMenuName, reqSsoMenu.getMenuName())
                .eq(reqSsoMenu.getMenuType() != null, SsoMenu::getMenuType, reqSsoMenu.getMenuType())
                .eq(reqSsoMenu.getIsVisible() != null, SsoMenu::getIsVisible, reqSsoMenu.getIsVisible())
                .like(reqSsoMenu.getPermissions() != null, SsoMenu::getPermissions, reqSsoMenu.getPermissions())
                .orderBy(true, true, SsoMenu::getMenuType)
                .orderBy(true, true, SsoMenu::getMenuSort);
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
        if (ssoMenuService.save(ssoMenu)) {
            return Result.ok(ssoMenu, "菜单权限表-添加成功!");
        }
        return Result.fail("错误:添加失败!");
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
            return Result.ok(ssoMenu, "菜单权限表-编辑成功!");
        }
        return Result.fail("错误:编辑失败!");
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
    public Result<?> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (ssoMenuService.removeById(id)) {
            return Result.ok("菜单权限表-删除成功!");
        }
        return Result.fail("错误:删除失败!");
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
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.ssoMenuService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok("菜单权限表-批量删除成功!");
        }
        return Result.fail("错误:批量删除失败!");
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
        return Result.ok(ssoMenu, "查询成功!");
    }
}

package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.annotation.InnerUser;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoTenant;
import cn.com.mfish.common.oauth.entity.SsoRole;
import cn.com.mfish.common.oauth.service.SsoRoleService;
import cn.com.mfish.oauth.mapper.SsoTenantMapper;
import cn.com.mfish.oauth.req.ReqSsoRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @version: V2.0.0
 */
@Slf4j
@Tag(name = "角色信息表")
@RestController
@RequestMapping("/role")
public class SsoRoleController {
    @Resource
    private SsoRoleService ssoRoleService;
    @Resource
    SsoTenantMapper ssoTenantMapper;

    @Operation(summary = "角色信息表-分页列表查询", description = "角色信息表-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:role:query")
    public Result<PageResult<SsoRole>> queryPageList(ReqSsoRole reqSsoRole, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        reqSsoRole.setTenantId(AuthInfoUtils.SUPER_TENANT_ID);
        return Result.ok(new PageResult<>(ssoRoleService.list(buildCondition(reqSsoRole))), "角色信息表-查询成功!");
    }

    @Operation(summary = "获取角色下的菜单ID")
    @GetMapping("/menus/{roleId}")
    @RequiresPermissions("sys:role:query")
    public Result<List<String>> getRoleMenuIds(@Parameter(name = "roleId", description = "角色ID") @PathVariable String roleId) {
        return Result.ok(ssoRoleService.getRoleMenus(roleId), "查询角色下菜单成功");
    }

    @Operation(summary = "角色信息表-列表查询", description = "角色信息表-列表查询")
    @GetMapping("/all")
    public Result<List<SsoRole>> queryList(ReqSsoRole reqSsoRole) {
        //组织参数不为空，获取组织所属租户的角色
        if (!StringUtils.isEmpty(reqSsoRole.getOrgIds())) {
            List<SsoTenant> tenants = ssoTenantMapper.getTenantByOrgId(reqSsoRole.getOrgIds().split(","));
            if (tenants == null || tenants.isEmpty()) {
                return Result.ok(new ArrayList<>(), "角色信息-查询成功");
            }
            reqSsoRole.setTenantId(tenants.stream().map(SsoTenant::getId).collect(Collectors.joining(",")));
        }
        return Result.ok(ssoRoleService.list(buildCondition(reqSsoRole)), "角色信息-查询成功!");
    }

    public static LambdaQueryWrapper<SsoRole> buildCondition(ReqSsoRole reqSsoRole) {
        LambdaQueryWrapper<SsoRole> wrapper = new LambdaQueryWrapper<SsoRole>()
                .eq(SsoRole::getDelFlag, 0)
                .eq(reqSsoRole.getStatus() != null, SsoRole::getStatus, reqSsoRole.getStatus())
                .like(reqSsoRole.getRoleCode() != null, SsoRole::getRoleCode, reqSsoRole.getRoleCode())
                .like(reqSsoRole.getRoleName() != null, SsoRole::getRoleName, reqSsoRole.getRoleName())
                .orderByAsc(SsoRole::getRoleSort);
        if (!StringUtils.isEmpty(reqSsoRole.getTenantId())) {
            String[] ids = reqSsoRole.getTenantId().split(",");
            wrapper.and(ssoRoleLambdaQueryWrapper -> {
                for (String id : ids) {
                    ssoRoleLambdaQueryWrapper.or().eq(!StringUtils.isEmpty(id), SsoRole::getTenantId, id);
                }
            });
        }
        return wrapper;
    }

    @Log(title = "角色信息表-添加", operateType = OperateType.INSERT)
    @Operation(summary = "角色信息表-添加", description = "角色信息表-添加")
    @PostMapping
    @RequiresPermissions("sys:role:insert")
    public Result<SsoRole> add(@RequestBody SsoRole ssoRole) {
        ssoRole.setTenantId(AuthInfoUtils.SUPER_TENANT_ID);
        return ssoRoleService.insertRole(ssoRole);
    }

    @Log(title = "角色信息表-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "角色信息表-编辑", description = "角色信息表-编辑")
    @PutMapping
    @RequiresPermissions("sys:role:update")
    public Result<SsoRole> edit(@RequestBody SsoRole ssoRole) {
        ssoRole.setTenantId(AuthInfoUtils.SUPER_TENANT_ID);
        return ssoRoleService.updateRole(ssoRole);
    }

    @Log(title = "角色信息表-设置状态", operateType = OperateType.UPDATE)
    @Operation(summary = "角色信息表-设置状态", description = "角色信息表-设置状态")
    @PutMapping("/status")
    public Result<Boolean> setStatus(@RequestBody SsoRole ssoRole) {
        if (ssoRoleService.updateById(new SsoRole().setId(ssoRole.getId()).setStatus(ssoRole.getStatus()))) {
            return Result.ok(true, "角色信息表-设置状态成功!");
        }
        return Result.fail(false, "错误:角色信息表-设置状态失败!");
    }

    @Log(title = "角色信息表-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "角色信息表-通过id删除", description = "角色信息表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:role:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (AuthInfoUtils.isSuperRole(id)) {
            return Result.fail(false, "错误:超户角色不允许删除!");
        }
        return ssoRoleService.deleteRole(id);
    }

    @Operation(summary = "角色信息表-通过id查询", description = "角色信息表-通过id查询")
    @GetMapping("/{ids}")
    @RequiresPermissions("sys:role:query")
    public Result<List<SsoRole>> queryByIds(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        return ssoRoleService.queryByIds(ids);
    }

    @Operation(summary = "获取角色id", description = "获取角色id-通过角色编码查询")
    @GetMapping("/ids")
    @InnerUser
    @Parameters({
            @Parameter(name = "tenantId", description = "租户ID"),
            @Parameter(name = "codes", description = "角色编码", required = true)
    })
    public Result<List<String>> getRoleIdsByCode(String tenantId, String codes) {
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = AuthInfoUtils.getCurrentTenantId();
        }
        return ssoRoleService.getRoleIdsByCode(tenantId, List.of(codes.split(",")));
    }

    @Operation(summary = "获取角色编码获取角色下所有用户id", description = "获取角色编码获取角色下所有用户id")
    @GetMapping("/users")
    @InnerUser
    @Parameters({
            @Parameter(name = "tenantId", description = "租户ID"),
            @Parameter(name = "codes", description = "角色编码", required = true)
    })
    public Result<List<String>> getRoleUsers(String tenantId, String codes) {
        if (StringUtils.isEmpty(tenantId)) {
            tenantId = AuthInfoUtils.getCurrentTenantId();
        }
        return ssoRoleService.getRoleUsers(tenantId, Arrays.asList(codes.split(",")));
    }
}

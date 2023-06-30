package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoTenant;
import cn.com.mfish.oauth.entity.SsoRole;
import cn.com.mfish.oauth.mapper.SsoTenantMapper;
import cn.com.mfish.oauth.req.ReqSsoRole;
import cn.com.mfish.oauth.service.SsoRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 角色信息表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.1
 */
@Slf4j
@Api(tags = "角色信息表")
@RestController
@RequestMapping("/role")
public class SsoRoleController {
    @Resource
    private SsoRoleService ssoRoleService;
    @Resource
    SsoTenantMapper ssoTenantMapper;
    /**
     * 分页列表查询
     *
     * @param reqSsoRole
     * @param reqPage
     * @return
     */
    @ApiOperation(value = "角色信息表-分页列表查询", notes = "角色信息表-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:role:query")
    public Result<PageResult<SsoRole>> queryPageList(ReqSsoRole reqSsoRole, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        reqSsoRole.setTenantId(AuthInfoUtils.SUPER_TENANT);
        return Result.ok(new PageResult<>(ssoRoleService.list(buildCondition(reqSsoRole))), "角色信息表-查询成功!");
    }

    @ApiOperation("获取角色下的菜单ID")
    @GetMapping("/menus/{roleId}")
    @RequiresPermissions("sys:role:query")
    public Result<List<String>> getRoleMenuIds(@ApiParam(name = "roleId", value = "角色ID") @PathVariable String roleId) {
        return Result.ok(ssoRoleService.getRoleMenus(roleId), "查询角色下菜单成功");
    }

    @ApiOperation(value = "角色信息表-列表查询", notes = "角色信息表-列表查询")
    @GetMapping("/all")
    public Result<List<SsoRole>> queryList(ReqSsoRole reqSsoRole) {
        //组织参数不为空，获取组织所属租户的角色
        if (!StringUtils.isEmpty(reqSsoRole.getOrgId())) {
            SsoTenant tenant = ssoTenantMapper.getTenantByOrgId(reqSsoRole.getOrgId());
            if(tenant == null){
                return Result.ok(new ArrayList<>(),"角色信息-查询成功");
            }
            reqSsoRole.setTenantId(tenant.getId());
        }
        return Result.ok(ssoRoleService.list(buildCondition(reqSsoRole)), "角色信息-查询成功!");
    }

    public static LambdaQueryWrapper<SsoRole> buildCondition(ReqSsoRole reqSsoRole) {
        return new LambdaQueryWrapper<SsoRole>()
                .eq(SsoRole::getDelFlag, 0)
                .eq(reqSsoRole.getTenantId() != null, SsoRole::getTenantId, reqSsoRole.getTenantId())
                .eq(reqSsoRole.getStatus() != null, SsoRole::getStatus, reqSsoRole.getStatus())
                .like(reqSsoRole.getRoleCode() != null, SsoRole::getRoleCode, reqSsoRole.getRoleCode())
                .like(reqSsoRole.getRoleName() != null, SsoRole::getRoleName, reqSsoRole.getRoleName())
                .orderByAsc(SsoRole::getRoleSort);
    }

    /**
     * 添加
     *
     * @param ssoRole
     * @return
     */
    @Log(title = "角色信息表-添加", operateType = OperateType.INSERT)
    @ApiOperation(value = "角色信息表-添加", notes = "角色信息表-添加")
    @PostMapping
    @RequiresPermissions("sys:role:insert")
    public Result<SsoRole> add(@RequestBody SsoRole ssoRole) {
        ssoRole.setTenantId(AuthInfoUtils.SUPER_TENANT);
        return ssoRoleService.insertRole(ssoRole);
    }

    /**
     * 编辑
     *
     * @param ssoRole
     * @return
     */
    @Log(title = "角色信息表-编辑", operateType = OperateType.UPDATE)
    @ApiOperation(value = "角色信息表-编辑", notes = "角色信息表-编辑")
    @PutMapping
    @RequiresPermissions("sys:role:update")
    public Result<SsoRole> edit(@RequestBody SsoRole ssoRole) {
        ssoRole.setTenantId(AuthInfoUtils.SUPER_TENANT);
        return ssoRoleService.updateRole(ssoRole);
    }

    @Log(title = "角色信息表-设置状态", operateType = OperateType.UPDATE)
    @ApiOperation(value = "角色信息表-设置状态", notes = "角色信息表-设置状态")
    @PutMapping("/status")
    public Result<Boolean> setStatus(@RequestBody SsoRole ssoRole) {
        if (ssoRoleService.updateById(new SsoRole().setId(ssoRole.getId()).setStatus(ssoRole.getStatus()))) {
            return Result.ok(true, "角色信息表-设置状态成功!");
        }
        return Result.fail(false, "错误:角色信息表-设置状态失败!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "角色信息表-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "角色信息表-通过id删除", notes = "角色信息表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:role:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (AuthInfoUtils.isSuperRole(id)) {
            return Result.fail(false, "错误:超户角色不允许删除!");
        }
        return ssoRoleService.deleteRole(id);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "角色信息表-通过id查询", notes = "角色信息表-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:role:query")
    public Result<SsoRole> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        SsoRole ssoRole = ssoRoleService.getById(id);
        return Result.ok(ssoRole, "角色信息表-查询成功!");
    }
}

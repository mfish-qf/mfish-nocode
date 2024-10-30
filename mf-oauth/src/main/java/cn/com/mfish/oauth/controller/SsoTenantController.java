package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.SsoTenant;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import cn.com.mfish.common.oauth.common.DataScopeType;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.entity.SsoRole;
import cn.com.mfish.common.oauth.entity.WeChatToken;
import cn.com.mfish.common.oauth.req.ReqSsoMenu;
import cn.com.mfish.common.oauth.req.ReqSsoOrg;
import cn.com.mfish.common.oauth.req.ReqSsoUser;
import cn.com.mfish.common.oauth.service.SsoMenuService;
import cn.com.mfish.common.oauth.service.SsoOrgService;
import cn.com.mfish.common.oauth.service.SsoRoleService;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.oauth.cache.common.ClearCache;
import cn.com.mfish.oauth.entity.UserOrg;
import cn.com.mfish.oauth.req.ReqSsoRole;
import cn.com.mfish.oauth.req.ReqSsoTenant;
import cn.com.mfish.oauth.service.SsoTenantService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.3.1
 */
@Slf4j
@Tag(name = "租户信息表")
@RestController
@RequestMapping("/ssoTenant")
public class SsoTenantController {
    @Resource
    SsoTenantService ssoTenantService;
    @Resource
    SsoOrgService ssoOrgService;
    @Resource
    SsoRoleService ssoRoleService;
    @Resource
    SsoMenuService ssoMenuService;
    @Resource
    SsoUserService ssoUserService;
    @Resource
    ClearCache clearCache;

    /**
     * 分页列表查询
     *
     * @param reqSsoTenant 租户信息表请求参数
     * @param reqPage      分页参数
     * @return 返回租户信息表-分页列表
     */
    @Operation(summary = "租户信息表-分页列表查询", description = "租户信息表-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:ssoTenant:query")
    public Result<PageResult<TenantVo>> queryPageList(ReqSsoTenant reqSsoTenant, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqSsoTenant, reqPage)), "租户信息表-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqSsoTenant 租户信息表请求参数
     * @param reqPage      分页参数
     * @return 返回租户信息表-分页列表
     */
    private List<TenantVo> queryList(ReqSsoTenant reqSsoTenant, ReqPage reqPage) {
        return ssoTenantService.queryList(reqSsoTenant, reqPage);
    }

    /**
     * 获取当前租户信息
     *
     * @return 租户信息
     */
    @Operation(summary = "获取当前租户信息", description = "获取当前租户信息")
    @GetMapping("/info")
    public Result<TenantVo> queryTenantInfo() {
        return Result.ok(ssoTenantService.queryInfo(AuthInfoUtils.getCurrentTenantId()), "租户信息-查询成功!");
    }

    /**
     * 添加
     *
     * @param ssoTenant 租户信息表对象
     * @return 返回租户信息表-添加结果
     */
    @Log(title = "租户信息表-添加", operateType = OperateType.INSERT)
    @Operation(summary = "租户信息表-添加")
    @PostMapping
    @RequiresPermissions("sys:ssoTenant:insert")
    public Result<SsoTenant> add(@RequestBody SsoTenant ssoTenant) {
        return ssoTenantService.insertTenant(ssoTenant);
    }

    /**
     * 编辑
     *
     * @param ssoTenant 租户信息表对象
     * @return 返回租户信息表-编辑结果
     */
    @Log(title = "租户信息表-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "租户信息表-编辑")
    @PutMapping
    @RequiresPermissions("sys:ssoTenant:update")
    public Result<SsoTenant> edit(@RequestBody SsoTenant ssoTenant) {
        return ssoTenantService.updateTenant(ssoTenant);
    }

    @Log(title = "管理员编辑自己租户信息", operateType = OperateType.UPDATE)
    @Operation(summary = "管理员编辑自己租户信息")
    @PutMapping("/me")
    public Result<SsoTenant> editMe(@RequestBody SsoTenant ssoTenant) {
        if (ssoTenantService.isTenantMaster(ssoTenant.getUserId(), ssoTenant.getId())) {
            //管理员不允许设置角色
            ssoTenant.setRoleIds(null);
            return ssoTenantService.updateTenant(ssoTenant);
        }
        return Result.fail(ssoTenant, "错误:只允许管理员修改");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回租户信息表-删除结果
     */
    @Log(title = "租户信息表-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "租户信息表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:ssoTenant:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return ssoTenantService.deleteTenant(id);
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回租户信息表对象
     */
    @Operation(summary = "租户信息表-通过id查询")
    @GetMapping("/{id}")
    public Result<SsoTenant> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        SsoTenant ssoTenant = ssoTenantService.getById(id);
        return Result.ok(ssoTenant, "租户信息表-查询成功!");
    }


    /**
     * 导出
     *
     * @param reqSsoTenant 租户信息表请求参数
     * @param reqPage      分页参数
     * @throws IOException 异常
     */
    @Operation(summary = "导出租户信息表", description = "导出租户信息表")
    @GetMapping("/export")
    @RequiresPermissions("sys:ssoTenant:export")
    public void export(ReqSsoTenant reqSsoTenant, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        ExcelUtils.write("SsoTenant", queryList(reqSsoTenant, reqPage));
    }

    /**
     * 租户切换
     *
     * @param tenantId 租户ID
     * @return 返回切换结果
     */
    @Operation(summary = "切换租户")
    @PutMapping("/change/{tenantId}")
    public Result<String> changeTenant(@PathVariable("tenantId") String tenantId) {
        if (StringUtils.isEmpty(tenantId)) {
            return Result.fail(tenantId, "错误:租户ID不允许为空");
        }
        List<TenantVo> list = OauthUtils.getTenants();
        if (list == null || list.stream().noneMatch((tenantVo -> tenantVo.getId().equals(tenantId)))) {
            return Result.fail(tenantId, "错误:该用户不属于此租户");
        }
        Object token = OauthUtils.getToken();
        switch (token) {
            case null -> {
                return Result.fail(tenantId, "错误:未找到token");
            }
            case RedisAccessToken rat -> {
                if (tenantId.equals(rat.getTenantId())) {
                    return Result.ok(tenantId, "切换租户成功");
                }
                rat.setTenantId(tenantId);
                OauthUtils.setToken(rat.getAccessToken(), rat);
                return Result.ok(tenantId, "切换租户成功");
            }
            case WeChatToken wct -> {
                if (tenantId.equals(wct.getTenantId())) {
                    return Result.ok(tenantId, "切换租户成功");
                }
                wct.setTenantId(tenantId);
                OauthUtils.setToken(wct.getAccess_token(), token);
                return Result.ok(tenantId, "切换租户成功");
            }
            default -> {
            }
        }
        return Result.fail(tenantId, "错误:未找到token");
    }

    /**
     * 查询租户组织树
     *
     * @param reqSsoOrg 请求参数
     * @return 返回结果
     */
    @Operation(summary = "获取租户组织树")
    @GetMapping("/org")
    @RequiresPermissions("sys:tenantOrg:query")
    public Result<List<SsoOrg>> queryOrgTree(ReqSsoOrg reqSsoOrg) {
        List<SsoOrg> list = ssoOrgService.queryOrg(reqSsoOrg.setTenantId(AuthInfoUtils.getCurrentTenantId()));
        List<SsoOrg> orgList = new ArrayList<>();
        TreeUtils.buildTree("", list, orgList, SsoOrg.class);
        return Result.ok(orgList, "组织结构表-查询成功!");
    }

    /**
     * 租户组织结构添加
     *
     * @param ssoOrg 请求参数
     * @return 返回结果
     */
    @Log(title = "租户组织结构-添加", operateType = OperateType.INSERT)
    @Operation(summary = "租户组织结构-添加", description = "租户组织结构-添加")
    @PostMapping("/org")
    @RequiresPermissions("sys:tenantOrg:insert")
    public Result<SsoOrg> orgAdd(@RequestBody SsoOrg ssoOrg) {
        Result<Boolean> result = verifyOrg(ssoOrg.getId());
        if (result.isSuccess()) {
            setParentOrg(ssoOrg);
            return ssoOrgService.insertOrg(ssoOrg);
        }
        return Result.fail(ssoOrg, result.getMsg());
    }

    /**
     * 租户组织结构编辑
     *
     * @param ssoOrg 请求参数
     * @return 返回结果
     */
    @Log(title = "租户组织结构-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "租户组织结构-编辑", description = "租户组织结构-编辑")
    @PutMapping("/org")
    @RequiresPermissions("sys:tenantOrg:update")
    public Result<SsoOrg> orgEdit(@RequestBody SsoOrg ssoOrg) {
        Result<Boolean> result = verifyOrg(ssoOrg.getId());
        if (result.isSuccess()) {
            setParentOrg(ssoOrg);
            //父节点为空的是租户，租户不允许修改自己的角色，防止越权修改
            //租户角色统一由系统管理员设置或修改
            if(StringUtils.isEmpty(ssoOrg.getParentId())){
                ssoOrg.setRoleIds(null);
            }
            return ssoOrgService.updateOrg(ssoOrg);
        }
        return Result.fail(ssoOrg, result.getMsg());
    }

    /**
     * 租户是否管理员
     *
     * @return 返回结果
     */
    private Result<Boolean> verifyTenant() {
//        if (!ssoTenantService.isTenantMaster(AuthInfoUtils.getCurrentUserId(), AuthInfoUtils.getCurrentTenantId())) {
//            return Result.fail(false, "错误:只允许管理员操作");
//        }
        return Result.ok();
    }

    /**
     * 校验租户组织
     *
     * @param id 组织ID
     * @return 返回结果
     */
    private Result<Boolean> verifyOrg(String id) {
        Result<Boolean> result = verifyTenant();
        if (!result.isSuccess()) {
            return result;
        }
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        if (!StringUtils.isEmpty(id) && !ssoOrgService.isTenantOrg(id, tenantId)) {
            return Result.fail(false, "错误:不允许操作非自己租户下的组织");
        }
        return Result.ok();
    }

    /**
     * 设置父组织
     *
     * @param ssoOrg 请求参数
     */
    private void setParentOrg(SsoOrg ssoOrg) {
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        SsoOrg org = ssoOrgService.getBaseMapper().selectOne(new LambdaQueryWrapper<SsoOrg>().eq(SsoOrg::getTenantId, tenantId));
        if (org == null) {
            throw new MyRuntimeException("错误:未找到租户父组织");
        }
        if (StringUtils.isEmpty(ssoOrg.getParentId()) && !ssoOrg.getId().equals(org.getId())) {
            ssoOrg.setParentId(org.getId());
        }
    }


    /**
     * 通过id删除组织
     *
     * @param id id
     * @return 返回结果
     */
    @Log(title = "组织结构表-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "组织结构表-通过id删除", description = "组织结构表-通过id删除")
    @DeleteMapping("/org/{id}")
    @RequiresPermissions("sys:tenantOrg:delete")
    public Result<Boolean> orgDelete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        Result<Boolean> result = verifyOrg(id);
        if (result.isSuccess()) {
            return ssoOrgService.removeOrg(id);
        }
        return result;
    }

    /**
     * 查询租户角色列表
     *
     * @param reqSsoRole 请求参数
     * @param reqPage    翻页参数
     * @return 返回结果
     */
    @Operation(summary = "租户角色信息-分页列表查询", description = "租户角色信息-分页列表查询")
    @GetMapping("/role")
    @RequiresPermissions("sys:tenantRole:query")
    @DataScope(table = "sso_role", type = DataScopeType.Tenant)
    public Result<PageResult<SsoRole>> queryRolePageList(ReqSsoRole reqSsoRole, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return Result.ok(new PageResult<>(ssoRoleService.list(SsoRoleController.buildCondition(reqSsoRole))), "角色信息表-查询成功!");
    }

    @Operation(summary = "角色信息表-列表查询", description = "角色信息表-列表查询")
    @GetMapping("/role/all")
    @RequiresPermissions("sys:tenantRole:query")
    @DataScope(table = "sso_role", type = DataScopeType.Tenant)
    public Result<List<SsoRole>> queryRoleList(ReqSsoRole reqSsoRole) {
        return Result.ok(ssoRoleService.list(SsoRoleController.buildCondition(reqSsoRole)), "角色信息表-查询成功!");
    }

    @Operation(summary = "获取租户角色下的菜单ID")
    @GetMapping("/role/menus/{roleId}")
    @RequiresPermissions("sys:tenantRole:query")
    public Result<List<String>> getRoleMenuIds(@Parameter(name = "roleId", description = "角色ID") @PathVariable String roleId) {
        Result<Boolean> result = verifyRole(roleId);
        if (result.isSuccess()) {
            return Result.ok(ssoRoleService.getRoleMenus(roleId), "查询租户菜单成功");
        }
        return Result.fail(new ArrayList<>(), result.getMsg());
    }

    @Operation(summary = "获取用户菜单树")
    @GetMapping("/menu/tree")
    @RequiresPermissions("sys:tenantRole:query")
    public Result<List<SsoMenu>> queryMenuTree(ReqSsoMenu reqSsoMenu) {
        return ssoMenuService.queryMenuTree(reqSsoMenu, AuthInfoUtils.getCurrentUserId());
    }

    /**
     * 租户角色信息添加
     *
     * @param ssoRole 请求参数
     * @return 返回结果
     */
    @Log(title = "租户角色信息-添加", operateType = OperateType.INSERT)
    @Operation(summary = "角色信息表-添加", description = "租户角色信息-添加")
    @PostMapping("/role")
    @RequiresPermissions("sys:tenantRole:insert")
    public Result<SsoRole> addRole(@RequestBody SsoRole ssoRole) {
        ssoRole.setTenantId(AuthInfoUtils.getCurrentTenantId());
        Result<Boolean> result = verifyRole(ssoRole.getId());
        if (result.isSuccess()) {
            return ssoRoleService.insertRole(ssoRole);
        }
        return Result.fail(ssoRole, result.getMsg());
    }

    /**
     * 租户角色信息编辑
     *
     * @param ssoRole 请求参数
     * @return 返回结果
     */
    @Log(title = "租户角色信息-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "角色信息表-编辑", description = "租户角色信息-编辑")
    @PutMapping("/role")
    @RequiresPermissions("sys:tenantRole:update")
    public Result<SsoRole> editRole(@RequestBody SsoRole ssoRole) {
        ssoRole.setTenantId(AuthInfoUtils.getCurrentTenantId());
        Result<Boolean> result = verifyRole(ssoRole.getId());
        if (result.isSuccess()) {
            return ssoRoleService.updateRole(ssoRole);
        }
        return Result.fail(ssoRole, result.getMsg());
    }

    @Log(title = "租户角色信息-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "租户角色信息-通过id删除", description = "租户角色信息-通过id删除")
    @DeleteMapping("/role/{id}")
    @RequiresPermissions("sys:tenantRole:delete")
    public Result<Boolean> deleteRole(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        Result<Boolean> result = verifyRole(id);
        if (result.isSuccess()) {
            return ssoRoleService.deleteRole(id);
        }
        return result;
    }

    @Operation(summary = "获取租户列表-通过角色编码查询")
    @GetMapping("/roleCode/{roleCode}")
    public Result<List<TenantVo>> queryByRoleCode(@Parameter(name = "roleCode", description = "角色编码") @PathVariable String roleCode) {
        return Result.ok(ssoTenantService.getTenantByRoleCode(roleCode), "获取租户列表成功!");
    }

    /**
     * 校验租户角色
     *
     * @param id id
     * @return 校验结果
     */
    private Result<Boolean> verifyRole(String id) {
        Result<Boolean> result = verifyTenant();
        if (!result.isSuccess()) {
            return result;
        }
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        if (!StringUtils.isEmpty(id) && !ssoRoleService.isTenantRole(id, tenantId)) {
            return Result.fail(false, "错误:不允许操作非自己租户下的角色");
        }
        return Result.ok();
    }

    @Operation(summary = "租户用户信息-分页列表查询", description = "租户用户信息-分页列表查询")
    @GetMapping("/user")
    @RequiresPermissions("sys:tenantUser:query")
    public Result<PageResult<UserInfo>> queryUserPageList(ReqSsoUser reqSsoUser, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        reqSsoUser.setTenantId(AuthInfoUtils.getCurrentTenantId());
        List<UserInfo> pageList = ssoUserService.getUserList(reqSsoUser);
        return Result.ok(new PageResult<>(pageList), "租户人员信息-查询成功!");
    }

    @Operation(summary = "用户组织关系绑定")
    @PostMapping("/user/org")
    @RequiresPermissions("sys:tenantUser:insert")
    public Result<Boolean> bindUserOrg(@RequestBody UserOrg userOrg) {
        Result<Boolean> result = verifyOrg(userOrg.getOrgId());
        if (!result.isSuccess()) {
            return result;
        }
        if (ssoUserService.isExistUserOrg(userOrg.getUserId(), userOrg.getOrgId())) {
            return Result.fail(false, "错误:用户已绑定该组织");
        }
        if (ssoUserService.insertUserOrg(userOrg.getUserId(), Collections.singletonList(userOrg.getOrgId())) > 0) {
            clearCache.removeUserCache(userOrg.getUserId());
            return Result.ok(true, "用户分配组织成功");
        }
        return Result.fail(false, "错误:用户分配组织失败");
    }

    @Operation(summary = "用户组织关系移除")
    @DeleteMapping("/user/org")
    @RequiresPermissions("sys:tenantUser:delete")
    public Result<Boolean> deleteUserOrg(@RequestBody UserOrg userOrg) {
        Result<Boolean> result = verifyOrg(userOrg.getOrgId());
        if (!result.isSuccess()) {
            return result;
        }
        if (ssoTenantService.isTenantMasterOrg(userOrg.getUserId(), userOrg.getOrgId())) {
            return Result.fail(false, "错误:不允许移除租户管理员");
        }
        if (ssoUserService.deleteUserOrg(userOrg.getUserId(), userOrg.getOrgId()) > 0) {
            clearCache.removeUserCache(userOrg.getUserId());
            return Result.ok(true, "用户移出组织成功");
        }
        return Result.fail(false, "错误:用户移出组织失败");
    }
}

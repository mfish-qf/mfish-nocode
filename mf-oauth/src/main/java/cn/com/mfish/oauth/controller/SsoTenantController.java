package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.common.OauthUtils;
import cn.com.mfish.oauth.entity.SsoTenant;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import cn.com.mfish.oauth.req.ReqSsoTenant;
import cn.com.mfish.oauth.service.SsoOrgService;
import cn.com.mfish.oauth.service.SsoTenantService;
import cn.com.mfish.oauth.vo.TenantVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.0.0
 */
@Slf4j
@Api(tags = "租户信息表")
@RestController
@RequestMapping("/ssoTenant")
public class SsoTenantController {
    @Resource
    SsoTenantService ssoTenantService;
    @Resource
    SsoOrgService ssoOrgService;

    /**
     * 分页列表查询
     *
     * @param reqSsoTenant 租户信息表请求参数
     * @param reqPage      分页参数
     * @return 返回租户信息表-分页列表
     */
    @ApiOperation(value = "租户信息表-分页列表查询", notes = "租户信息表-分页列表查询")
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
     * 添加
     *
     * @param ssoTenant 租户信息表对象
     * @return 返回租户信息表-添加结果
     */
    @Log(title = "租户信息表-添加", operateType = OperateType.INSERT)
    @ApiOperation("租户信息表-添加")
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
    @ApiOperation("租户信息表-编辑")
    @PutMapping
    @RequiresPermissions("sys:ssoTenant:update")
    public Result<SsoTenant> edit(@RequestBody SsoTenant ssoTenant) {
        return ssoTenantService.updateTenant(ssoTenant);
    }

    @Log(title = "管理员编辑自己租户信息", operateType = OperateType.UPDATE)
    @ApiOperation("管理员编辑自己租户信息")
    @PutMapping("/me")
    public Result<SsoTenant> editMe(@RequestBody SsoTenant ssoTenant) {
        if (ssoTenantService.isTenantMaster(ssoTenant.getUserId(), ssoTenant.getId())) {
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
    @ApiOperation("租户信息表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:ssoTenant:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        return ssoTenantService.deleteTenant(id);
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回租户信息表对象
     */
    @ApiOperation("租户信息表-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:ssoTenant:query")
    public Result<SsoTenant> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        SsoTenant ssoTenant = ssoTenantService.getById(id);
        return Result.ok(ssoTenant, "租户信息表-查询成功!");
    }

    /**
     * 导出
     *
     * @param reqSsoTenant 租户信息表请求参数
     * @param reqPage      分页参数
     * @throws IOException
     */
    @ApiOperation(value = "导出租户信息表", notes = "导出租户信息表")
    @GetMapping("/export")
    @RequiresPermissions("sys:ssoTenant:export")
    public void export(ReqSsoTenant reqSsoTenant, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        ExcelUtils.write("SsoTenant", queryList(reqSsoTenant, reqPage));
    }

    @ApiOperation(value = "获取租户组织树")
    @GetMapping("/org")
    public Result<List<SsoOrg>> queryOrgTree(ReqSsoOrg reqSsoOrg) {
        List<SsoOrg> list = ssoOrgService.queryOrg(reqSsoOrg.setTenantId(AuthInfoUtils.getCurrentTenantId()));
        List<SsoOrg> orgList = new ArrayList<>();
        TreeUtils.buildTree("", list, orgList, SsoOrg.class);
        return Result.ok(orgList, "组织结构表-查询成功!");
    }

    @Log(title = "组织结构表-添加", operateType = OperateType.INSERT)
    @ApiOperation(value = "组织结构表-添加", notes = "组织结构表-添加")
    @PostMapping("/org")
    public Result<SsoOrg> orgAdd(@RequestBody SsoOrg ssoOrg) {
        Result<SsoOrg> result = verifyOrg(ssoOrg);
        if (result.isSuccess()) {
            return ssoOrgService.insertOrg(ssoOrg);
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param ssoOrg
     * @return
     */
    @Log(title = "组织结构表-编辑", operateType = OperateType.UPDATE)
    @ApiOperation(value = "组织结构表-编辑", notes = "组织结构表-编辑")
    @PutMapping("/org")
    public Result<SsoOrg> orgEdit(@RequestBody SsoOrg ssoOrg) {
        Result<SsoOrg> result = verifyOrg(ssoOrg);
        if (result.isSuccess()) {
            return ssoOrgService.updateOrg(ssoOrg);
        }
        return result;
    }

    /**
     * 新增修改组织前校验
     *
     * @param ssoOrg
     * @return
     */
    private Result<SsoOrg> verifyOrg(SsoOrg ssoOrg) {
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        if (!ssoTenantService.isTenantMaster(AuthInfoUtils.getCurrentUserId(), tenantId)) {
            return Result.fail(ssoOrg, "错误:只允许管理员修改");
        }
        SsoOrg org = ssoOrgService.getBaseMapper().selectOne(new LambdaQueryWrapper<SsoOrg>().eq(SsoOrg::getTenantId, tenantId));
        if (org == null) {
            return Result.fail(ssoOrg, "错误:未找到租户父组织");
        }
        if (!StringUtils.isEmpty(ssoOrg.getId()) && !ssoOrgService.isTenantOrg(ssoOrg.getId(), tenantId)) {
            return Result.fail(ssoOrg, "错误:不允许操作非自己租户下的组织");
        }
        if (StringUtils.isEmpty(ssoOrg.getParentId()) && !ssoOrg.getId().equals(org.getId())) {
            ssoOrg.setParentId(org.getId());
        }
        return Result.ok();
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "组织结构表-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "组织结构表-通过id删除", notes = "组织结构表-通过id删除")
    @DeleteMapping("/org/{id}")
    public Result<Boolean> orgDelete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.fail(false, "错误:ID不允许为空");
        }
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        if (!ssoTenantService.isTenantMaster(AuthInfoUtils.getCurrentUserId(), tenantId)) {
            return Result.fail(false, "错误:只允许管理员修改");
        }
        if (!ssoOrgService.isTenantOrg(id, tenantId)) {
            return Result.fail(false, "错误:不允许操作非自己租户下的组织");
        }
        return ssoOrgService.removeOrg(id);
    }
}

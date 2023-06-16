package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.oauth.entity.SsoTenant;
import cn.com.mfish.oauth.req.ReqSsoTenant;
import cn.com.mfish.oauth.service.SsoTenantService;
import cn.com.mfish.oauth.vo.TenantVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
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
    private SsoTenantService ssoTenantService;

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
        if (ssoTenantService.save(ssoTenant)) {
            return Result.ok(ssoTenant, "租户信息表-添加成功!");
        }
        return Result.fail(ssoTenant, "错误:租户信息表-添加失败!");
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
        if (ssoTenantService.updateById(ssoTenant)) {
            return Result.ok(ssoTenant, "租户信息表-编辑成功!");
        }
        return Result.fail(ssoTenant, "错误:租户信息表-编辑失败!");
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
        if (ssoTenantService.removeById(id)) {
            return Result.ok(true, "租户信息表-删除成功!");
        }
        return Result.fail(false, "错误:租户信息表-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回租户信息表-删除结果
     */
    @Log(title = "租户信息表-批量删除", operateType = OperateType.DELETE)
    @ApiOperation("租户信息表-批量删除")
    @DeleteMapping("/batch")
    @RequiresPermissions("sys:ssoTenant:delete")
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.ssoTenantService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "租户信息表-批量删除成功!");
        }
        return Result.fail(false, "错误:租户信息表-批量删除失败!");
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
     * @param reqSsoTenant 租户信息表请求参数
     * @param reqPage 分页参数
     * @throws IOException
     */
    @ApiOperation(value = "导出租户信息表", notes = "导出租户信息表")
    @GetMapping("/export")
    @RequiresPermissions("sys:ssoTenant:export")
    public void export(ReqSsoTenant reqSsoTenant, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        ExcelUtils.write("SsoTenant", queryList(reqSsoTenant, reqPage));
    }
}

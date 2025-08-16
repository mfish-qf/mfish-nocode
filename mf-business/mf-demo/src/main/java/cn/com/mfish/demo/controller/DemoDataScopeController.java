package cn.com.mfish.demo.controller;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.DataScopes;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.common.DataScopeType;
import cn.com.mfish.demo.entity.DemoDataScope;
import cn.com.mfish.demo.req.ReqDemoDataScope;
import cn.com.mfish.demo.service.DemoDataScopeService;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: demo_data_scope
 * @author: mfish
 * @date: 2024-09-04
 * @version: V2.1.0
 */
@Slf4j
@Tag(name = "数据权限样例")
@RestController
@RequestMapping("/demoDataScope")
public class DemoDataScopeController {
    @Resource
    private DemoDataScopeService demoDataScopeService;

    /**
     * 分页列表查询
     *
     * @param reqDemoDataScope 数据权限样例请求参数
     * @param reqPage          分页参数
     * @return 返回demo_data_scope-分页列表
     */
    @Operation(summary = "demo_data_scope-分页列表查询", description = "demo_data_scope-分页列表查询")
    @GetMapping
    @RequiresPermissions("demo:demoDataScope:query")
    public Result<PageResult<DemoDataScope>> queryPageList(ReqDemoDataScope reqDemoDataScope, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoDataScope, reqPage)), "demo_data_scope-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqDemoDataScope 数据权限样例请求参数
     * @param reqPage          分页参数
     * @return 返回demo_data_scope-分页列表
     */
    private List<DemoDataScope> queryList(ReqDemoDataScope reqDemoDataScope, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return demoDataScopeService.list();
    }

    @Operation(summary = "查询当前租户数据", description = "查询当前租户数据")
    @GetMapping("/currentTenant")
    @RequiresPermissions("demo:demoDataScope:query")
    @DataScope(table = "demo_data_scope", type = DataScopeType.Tenant)
    public Result<PageResult<DemoDataScope>> currentTenant(ReqDemoDataScope reqDemoDataScope, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoDataScope, reqPage)), "查询当前租户数据成功!");
    }

    @Operation(summary = "查询当前组织及其子组织数据", description = "查询当前组织及其子组织数据")
    @GetMapping("/currentOrg")
    @RequiresPermissions("demo:demoDataScope:query")
    @DataScope(table = "demo_data_scope", type = DataScopeType.Org)
    public Result<PageResult<DemoDataScope>> currentOrg(ReqDemoDataScope reqDemoDataScope, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoDataScope, reqPage)), "查询当前组织及其子组织数据成功!");
    }

    @Operation(summary = "查询当前角色数据", description = "查询当前角色数据")
    @GetMapping("/currentRole")
    @RequiresPermissions("demo:demoDataScope:query")
    @DataScope(table = "demo_data_scope", type = DataScopeType.Role)
    public Result<PageResult<DemoDataScope>> currentRole(ReqDemoDataScope reqDemoDataScope, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoDataScope, reqPage)), "查询当前角色数据成功!");
    }

    @Operation(summary = "查询组织编码为admin或mfish及其下级组织数据", description = "查询组织编码为admin或mfish及其下级组织数据")
    @GetMapping("/adminOrg")
    @RequiresPermissions("demo:demoDataScope:query")
    @DataScope(table = "demo_data_scope", type = DataScopeType.Org, values = {"admin", "mfish"})
    public Result<PageResult<DemoDataScope>> adminOrg(ReqDemoDataScope reqDemoDataScope, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoDataScope, reqPage)), "查询组织编码为admin或mfish及其下级组织数据成功!");
    }

    @Operation(summary = "查询角色编码为manage或superAdmin数据", description = "查询角色编码为manage或superAdmin数据")
    @GetMapping("/fixRole")
    @RequiresPermissions("demo:demoDataScope:query")
    @DataScope(table = "demo_data_scope", type = DataScopeType.Role, values = {"manage", "superAdmin", "xxxgly"})
    public Result<PageResult<DemoDataScope>> fixRole(ReqDemoDataScope reqDemoDataScope, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoDataScope, reqPage)), "查询角色编码为manage或superAdmin数据成功!");
    }

    @Operation(summary = "查询当前租户下角色编码为manage或superAdmin数据", description = "查询当前租户下角色编码为manage或superAdmin数据")
    @GetMapping("/mixScope")
    @RequiresPermissions("demo:demoDataScope:query")
    @DataScopes({
            @DataScope(table = "demo_data_scope", type = DataScopeType.Tenant),
            @DataScope(table = "demo_data_scope", type = DataScopeType.Role, values = {"manage", "superAdmin", "xxxgly"})
    })
    public Result<PageResult<DemoDataScope>> mixScope(ReqDemoDataScope reqDemoDataScope, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoDataScope, reqPage)), "查询当前租户下角色编码为manage或superAdmin数据成功!");
    }
}

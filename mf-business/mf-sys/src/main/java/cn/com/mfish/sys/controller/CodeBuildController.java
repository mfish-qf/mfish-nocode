package cn.com.mfish.sys.controller;

import cn.com.mfish.common.code.vo.CodeVo;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoMenu;
import cn.com.mfish.common.oauth.common.DataScopeType;
import cn.com.mfish.sys.entity.CodeBuild;
import cn.com.mfish.sys.req.ReqCodeBuild;
import cn.com.mfish.sys.req.ReqMenuCreate;
import cn.com.mfish.sys.service.CodeBuildService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V2.0.0
 */
@Slf4j
@Tag(name = "代码构建")
@RestController
@RequestMapping("/codeBuild")
public class CodeBuildController {
    @Resource
    private CodeBuildService codeBuildService;

    /**
     * 分页列表查询
     *
     * @param reqCodeBuild 代码构建请求参数
     * @return 返回代码构建-分页列表
     */
    @Operation(summary = "代码构建-分页列表查询", description = "代码构建-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:codeBuild:query")
    public Result<PageResult<CodeBuild>> queryPageList(ReqCodeBuild reqCodeBuild, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return Result.ok(new PageResult<>(codeBuildService.list(new LambdaQueryWrapper<CodeBuild>()
                .like(reqCodeBuild.getTableName() != null, CodeBuild::getTableName, reqCodeBuild.getTableName())
                .like(reqCodeBuild.getApiPrefix() != null, CodeBuild::getApiPrefix, reqCodeBuild.getApiPrefix())
                .like(reqCodeBuild.getEntityName() != null, CodeBuild::getEntityName, reqCodeBuild.getEntityName())
                .orderByDesc(CodeBuild::getCreateTime))), "代码构建-查询成功!");
    }

    /**
     * 添加
     *
     * @param codeBuild 代码构建对象
     * @return 返回代码构建-添加结果
     */
    @Log(title = "代码构建-添加", operateType = OperateType.INSERT)
    @Operation(summary = "代码构建-添加")
    @PostMapping
    @RequiresPermissions("sys:codeBuild:insert")
    public Result<CodeBuild> add(@RequestBody CodeBuild codeBuild) {
        return codeBuildService.insertCodeBuild(codeBuild);
    }

    @Log(title = "代码构建-更新", operateType = OperateType.INSERT)
    @Operation(summary = "代码构建-更新")
    @PutMapping
    @RequiresPermissions("sys:codeBuild:insert")
    public Result<CodeBuild> update(@RequestBody CodeBuild codeBuild) {
        return codeBuildService.updateCodeBuild(codeBuild);
    }

    @Log(title = "查看代码", operateType = OperateType.QUERY)
    @Operation(summary = "查看代码")
    @GetMapping("/view/{id}")
    @RequiresPermissions("sys:codeBuild:query")
    @DataScope(table = "sys_db_connect", type = DataScopeType.Tenant, excludes = "is_public=1")
    public Result<List<CodeVo>> query(@PathVariable Long id) {
        return codeBuildService.getCode(id);
    }

    @Log(title = "下载代码", operateType = OperateType.EXPORT)
    @Operation(summary = "下载代码")
    @GetMapping("/download/{id}")
    @RequiresPermissions("sys:codeBuild:query")
    @DataScope(table = "sys_db_connect", type = DataScopeType.Tenant, excludes = "is_public=1")
    public void downloadCode(@PathVariable Long id, HttpServletResponse response) throws IOException {
        codeBuildService.downloadCode(id, response);
    }

    @Log(title = "保存代码到IDE", operateType = OperateType.QUERY)
    @Operation(summary = "保存代码到IDE")
    @GetMapping("/saveLocal/{id}")
    @RequiresPermissions("sys:codeBuild:query")
    public Result<Boolean> saveLocal(@PathVariable Long id) {
        return codeBuildService.saveLocal(id);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回代码构建-删除结果
     */
    @Log(title = "代码构建-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "代码构建-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:codeBuild:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable Long id) {
        if (codeBuildService.removeById(id)) {
            return Result.ok(true, "代码构建-删除成功!");
        }
        return Result.fail(false, "错误:代码构建-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回代码构建-删除结果
     */
    @Log(title = "代码构建-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "代码构建-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("sys:codeBuild:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        if (this.codeBuildService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "代码构建-批量删除成功!");
        }
        return Result.fail(false, "错误:代码构建-批量删除失败!");
    }

    /**
     * 创建菜单
     *
     * @param reqMenuCreate 请求参数
     * @return 返回菜单
     */
    @Log(title = "创建菜单", operateType = OperateType.INSERT)
    @Operation(summary = "创建菜单", description = "创建菜单")
    @PostMapping("/menu")
    @RequiresPermissions("sys:codeBuild:insert")
    public Result<SsoMenu> createMenu(@RequestBody ReqMenuCreate reqMenuCreate) {
        return codeBuildService.createMenu(reqMenuCreate);
    }

}

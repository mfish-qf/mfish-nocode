package cn.com.mfish.sys.controller;

import cn.com.mfish.common.code.api.remote.RemoteCodeService;
import cn.com.mfish.common.code.api.req.ReqCode;
import cn.com.mfish.common.code.api.req.ReqSearch;
import cn.com.mfish.common.code.api.vo.CodeVo;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.sys.entity.CodeBuild;
import cn.com.mfish.sys.req.ReqCodeBuild;
import cn.com.mfish.sys.service.CodeBuildService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 代码构建
 * @author: mfish
 * @date: 2023-04-11
 * @version: V1.0.0
 */
@Slf4j
@Api(tags = "代码构建")
@RestController
@RequestMapping("/codeBuild")
public class CodeBuildController {
    @Resource
    private CodeBuildService codeBuildService;
    @Resource
    private RemoteCodeService remoteCodeService;

    /**
     * 分页列表查询
     *
     * @param reqCodeBuild 代码构建请求参数
     * @return 返回代码构建-分页列表
     */
    @ApiOperation(value = "代码构建-分页列表查询", notes = "代码构建-分页列表查询")
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
    @ApiOperation("代码构建-添加")
    @PostMapping
    @RequiresPermissions("sys:codeBuild:insert")
    public Result<CodeBuild> add(@RequestBody CodeBuild codeBuild) {
        return codeBuildService.insertCodeBuild(codeBuild);
    }

    @Log(title = "查看代码", operateType = OperateType.QUERY)
    @ApiOperation("查看代码")
    @GetMapping("/view/{id}")
    @RequiresPermissions("sys:codeBuild:query")
    public Result<List<CodeVo>> query(@PathVariable String id) {
        return remoteCodeService.getCode(buildReqCode(id));
    }

    @Log(title = "下载代码", operateType = OperateType.EXPORT)
    @ApiOperation("下载代码")
    @GetMapping("/download/{id}")
    @RequiresPermissions("sys:codeBuild:query")
    public void downloadCode(@PathVariable String id, HttpServletResponse response) throws IOException {
        ReqCode reqCode = buildReqCode(id);
        Result<byte[]> result = remoteCodeService.downloadCode(reqCode);
        response.reset();
        if (!result.isSuccess()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(JSON.toJSONString(result));
            return;
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + reqCode.getTableName() + ".zip");
        response.addHeader("Content-Length", result.getData().length + "");
        response.setContentType("application/x-zip-compressed; charset=UTF-8");
        IOUtils.write(result.getData(), response.getOutputStream());
    }

    /**
     * 构建请求参数
     *
     * @param id 代码唯一id
     * @return 请求参数
     */
    private ReqCode buildReqCode(String id) {
        CodeBuild codeBuild = codeBuildService.getById(id);
        ReqCode reqCode = new ReqCode();
        BeanUtils.copyProperties(codeBuild, reqCode);
        if (!StringUtils.isEmpty(codeBuild.getQueryParams())) {
            reqCode.setSearches(JSON.parseArray(codeBuild.getQueryParams(), ReqSearch.class));
        }
        return reqCode;
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回代码构建-删除结果
     */
    @Log(title = "代码构建-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation("代码构建-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:codeBuild:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
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
    @ApiOperation("代码构建-批量删除")
    @DeleteMapping("/batch")
    @RequiresPermissions("sys:codeBuild:delete")
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.codeBuildService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "代码构建-批量删除成功!");
        }
        return Result.fail(false, "错误:代码构建-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回代码构建对象
     */
    @ApiOperation("代码构建-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:codeBuild:query")
    public Result<CodeBuild> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        CodeBuild codeBuild = codeBuildService.getById(id);
        return Result.ok(codeBuild, "代码构建-查询成功!");
    }
}

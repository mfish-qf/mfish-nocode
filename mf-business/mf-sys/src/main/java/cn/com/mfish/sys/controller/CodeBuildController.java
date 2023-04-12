package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.CodeBuild;
import cn.com.mfish.sys.req.ReqCodeBuild;
import cn.com.mfish.sys.service.CodeBuildService;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

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

    /**
     * 分页列表查询
     *
     * @param reqCodeBuild 代码构建请求参数
     * @return 返回代码构建-分页列表
     */
    @ApiOperation(value = "代码构建-分页列表查询", notes = "代码构建-分页列表查询")
    @GetMapping
    public Result<PageResult<CodeBuild>> queryPageList(ReqCodeBuild reqCodeBuild, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return Result.ok(new PageResult<>(codeBuildService.list()), "代码构建-查询成功!");
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
    public Result<CodeBuild> add(@RequestBody CodeBuild codeBuild) {
        Result<CodeBuild> result = validateCodeBuild(codeBuild);
        if (!result.isSuccess()) {
            return result;
        }
        if (codeBuildService.save(codeBuild)) {
            return Result.ok(codeBuild, "代码构建-添加成功!");
        }
        return Result.fail(codeBuild, "错误:代码构建-添加失败!");
    }

    private Result<CodeBuild> validateCodeBuild(CodeBuild codeBuild) {
        if (StringUtils.isEmpty(codeBuild.getConnectId()) || StringUtils.isEmpty(codeBuild.getTableName())) {
            return Result.fail(codeBuild, "错误:请选择数据库和表");
        }
        return Result.ok(codeBuild, "校验成功");
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
    public Result<CodeBuild> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        CodeBuild codeBuild = codeBuildService.getById(id);
        return Result.ok(codeBuild, "代码构建-查询成功!");
    }
}

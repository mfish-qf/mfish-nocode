package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.sys.entity.DictCategory;
import cn.com.mfish.sys.req.ReqDictCategory;
import cn.com.mfish.sys.service.DictCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 树行分类字典
 * @author: mfish
 * @date: 2024-03-12
 * @version: V1.2.0
 */
@Slf4j
@Api(tags = "树形分类")
@RestController
@RequestMapping("/dictCategory")
public class DictCategoryController {
    @Resource
    private DictCategoryService dictCategoryService;

    /**
     * 分页列表查询
     *
     * @param reqDictCategory 树形分类请求参数
     * @param reqPage             分页参数
     * @return 返回树形分类-分页列表
     */
    @ApiOperation(value = "树形分类-分页列表查询", notes = "树形分类-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:dictCategory:query")
    public Result<PageResult<DictCategory>> queryPageList(ReqDictCategory reqDictCategory, ReqPage reqPage) {
        return dictCategoryService.queryCategoryTree(reqDictCategory, reqPage);
    }

    @ApiOperation(value = "树形分类查询", notes = "树形分类")
    @GetMapping("/tree")
    @RequiresPermissions("sys:dictCategory:query")
    public Result<List<DictCategory>> queryList(ReqDictCategory reqDictCategory) {
        return dictCategoryService.queryCategoryTree(reqDictCategory);
    }

    /**
     * 添加
     *
     * @param dictCategory 树形分类对象
     * @return 返回树形分类-添加结果
     */
    @Log(title = "树形分类-添加", operateType = OperateType.INSERT)
    @ApiOperation("树形分类-添加")
    @PostMapping
    @RequiresPermissions("sys:dictCategory:insert")
    public Result<DictCategory> add(@RequestBody DictCategory dictCategory) {
        return dictCategoryService.insertCategory(dictCategory);
    }

    /**
     * 编辑
     *
     * @param dictCategory 树形分类对象
     * @return 返回树形分类-编辑结果
     */
    @Log(title = "树形分类-编辑", operateType = OperateType.UPDATE)
    @ApiOperation("树形分类-编辑")
    @PutMapping
    @RequiresPermissions("sys:dictCategory:update")
    public Result<DictCategory> edit(@RequestBody DictCategory dictCategory) {
        return dictCategoryService.updateCategory(dictCategory);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回树形分类-删除结果
     */
    @Log(title = "树形分类-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation("树形分类-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:dictCategory:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        return dictCategoryService.deleteCategory(id);
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回树形分类对象
     */
    @ApiOperation("树形分类-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:dictCategory:query")
    public Result<DictCategory> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        DictCategory dictCategory = dictCategoryService.getById(id);
        return Result.ok(dictCategory, "树形分类-查询成功!");
    }

}

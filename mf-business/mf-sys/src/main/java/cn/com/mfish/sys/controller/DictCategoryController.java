package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.sys.api.entity.DictCategory;
import cn.com.mfish.common.sys.req.ReqDictCategory;
import cn.com.mfish.common.sys.service.DictCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @description: 树行分类字典
 * @author: mfish
 * @date: 2024-03-12
 * @version: V2.1.0
 */
@Slf4j
@Tag(name = "树形分类")
@RestController
@RequestMapping("/dictCategory")
public class DictCategoryController {
    @Resource
    private DictCategoryService dictCategoryService;

    /**
     * 分页列表查询
     *
     * @param reqDictCategory 树形分类请求参数
     * @param reqPage         分页参数
     * @return 返回树形分类-分页列表
     */
    @Operation(summary = "树形分类-分页列表查询", description = "树形分类-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:dictCategory:query")
    public Result<PageResult<DictCategory>> queryPageList(ReqDictCategory reqDictCategory, ReqPage reqPage) {
        return dictCategoryService.queryCategoryTree(reqDictCategory, reqPage);
    }

    @Operation(summary = "树形分类查询", description = "树形分类")
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
    @Operation(summary = "树形分类-添加")
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
    @Operation(summary = "树形分类-编辑")
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
    @Operation(summary = "树形分类-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:dictCategory:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return dictCategoryService.deleteCategory(id);
    }

    /**
     * 通过id查询
     *
     * @param ids 唯一ID
     * @return 返回树形分类对象
     */
    @Operation(summary = "树形分类-通过id查询（多个逗号分割）")
    @GetMapping("/{ids}")
    @RequiresPermissions("sys:dictCategory:query")
    public Result<List<DictCategory>> queryByIds(@Parameter(name = "ids", description = "唯一性ID，多个逗号分割") @PathVariable String ids) {
        return Result.ok(dictCategoryService.listByIds(Arrays.asList(ids.split(","))), "树形分类-查询成功!");
    }

    @Operation(summary = "分类树-通过分类编码查询", description = "分类树-通过分类编码查询")
    @GetMapping("/tree/{code}")
    @Parameters({
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true),
    })
    public Result<List<DictCategory>> queryTreeByCode(@Parameter(name = "code", description = "分类") @PathVariable String code, @RequestParam String direction) {
        return Result.ok(dictCategoryService.queryCategoryTreeByCode(code, TreeDirection.getDirection(direction)), "分类树-查询成功!");
    }

    @Operation(summary = "分类列表-通过分类编码查询", description = "分类列表-通过分类编码查询")
    @GetMapping("/list/{code}")
    @Parameters({
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true),
    })
    public Result<List<DictCategory>> queryListByCode(@Parameter(name = "code", description = "分类") @PathVariable String code, @RequestParam String direction) {
        return Result.ok(dictCategoryService.queryCategoryListByCode(code, TreeDirection.getDirection(direction)), "分类列表-查询成功!");
    }

    @Operation(summary = "分类树-通过分类id查询", description = "分类树-通过分类id查询")
    @GetMapping("/tree/id/{id}")
    @Parameters({
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true),
    })
    public Result<List<DictCategory>> queryTreeById(@Parameter(name = "id", description = "唯一id") @PathVariable String id, @RequestParam String direction) {
        return Result.ok(dictCategoryService.queryCategoryTreeById(id, TreeDirection.getDirection(direction)), "分类树-查询成功!");
    }

    @Operation(summary = "分类列表-通过分类id查询", description = "分类列表-通过分类id查询")
    @GetMapping("/list/id/{id}")
    @Parameters({
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true),
    })
    public Result<List<DictCategory>> queryListById(@Parameter(name = "id", description = "唯一id") @PathVariable String id, @RequestParam String direction) {
        return Result.ok(dictCategoryService.queryCategoryListById(id, TreeDirection.getDirection(direction)), "分类列表-查询成功!");
    }

    @Operation(summary = "分类-通过编码查询")
    @GetMapping("/one/{code}")
    public Result<DictCategory> queryOneByCode(@Parameter(name = "code", description = "唯一性ID") @PathVariable String code) {
        return Result.ok(dictCategoryService.getOne(new LambdaQueryWrapper<DictCategory>()
                .eq(DictCategory::getCategoryCode, code)), "物料分类-查询成功!");
    }

    @Operation(summary = "判断分类id是否在指定固定编码分类范围中")
    @GetMapping("/include")
    @Parameters({
            @Parameter(name = "categoryId", description = "分类id", required = true),
            @Parameter(name = "fixCode", description = "固定编码", required = true),
            @Parameter(name = "direction", description = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", required = true),
    })
    public Result<Boolean> includeCategory(String categoryId, String fixCode, String direction) {
        return Result.ok(dictCategoryService.includeCategory(categoryId, fixCode, TreeDirection.getDirection(direction)));
    }
}

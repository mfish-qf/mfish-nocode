package cn.com.mfish.project.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.project.entity.PmDictCategory;
import cn.com.mfish.project.service.PmDictCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 树形业务字典与功能层级分类
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Tag(name = "分类字典管理")
// 显式指定 bean 名称，避免与 mf-sys 模块 cn.com.mfish.sys.controller.DictCategoryController 默认 bean 名冲突
@RestController("pmDictCategoryController")
@RequestMapping("/project/dict-category")
public class DictCategoryController {

    @Resource
    private PmDictCategoryService dictCategoryService;

    /**
     * 递归查询返回完整树形层级结构列表(用于模块、一级、二级、三级功能的级联选择)
     */
    @Operation(summary = "分类字典-树形结构查询", description = "递归返回完整树形层级结构列表")
    @GetMapping("/tree")
    @RequiresPermissions("project:dictCategory:query")
    public Result<List<PmDictCategory>> tree() {
        return dictCategoryService.tree();
    }

    /**
     * 新增节点(自动维护 treeCode 如 001、001001 和 treeLevel)
     */
    @Log(title = "分类字典-添加", operateType = OperateType.INSERT)
    @Operation(summary = "分类字典-添加节点")
    @PostMapping
    @RequiresPermissions("project:dictCategory:insert")
    public Result<PmDictCategory> add(@RequestBody PmDictCategory category) {
        return dictCategoryService.addCategory(category);
    }

    /**
     * 编辑节点
     */
    @Log(title = "分类字典-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "分类字典-编辑节点")
    @PutMapping
    @RequiresPermissions("project:dictCategory:update")
    public Result<PmDictCategory> edit(@RequestBody PmDictCategory category) {
        return dictCategoryService.editCategory(category);
    }

    /**
     * 删除节点(连同子节点)
     */
    @Log(title = "分类字典-删除", operateType = OperateType.DELETE)
    @Operation(summary = "分类字典-通过id删除节点", description = "删除节点及全部子孙节点")
    @DeleteMapping("/{id}")
    @RequiresPermissions("project:dictCategory:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "分类ID") @PathVariable String id) {
        return dictCategoryService.deleteCategory(id);
    }
}

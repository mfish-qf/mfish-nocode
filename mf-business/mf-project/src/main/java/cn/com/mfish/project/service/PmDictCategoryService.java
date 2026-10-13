package cn.com.mfish.project.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.entity.PmDictCategory;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

/**
 * @description: 树形业务字典与功能层级分类
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmDictCategoryService extends IService<PmDictCategory> {

    /**
     * 递归查询返回完整树形层级结构列表(用于模块、一级、二级、三级功能的级联选择)
     *
     * @return 树形结构列表
     */
    Result<List<PmDictCategory>> tree();

    /**
     * 新增节点(自动维护 treeCode 如 001、001001 和 treeLevel)
     *
     * @param category 字典分类对象
     * @return 添加结果
     */
    Result<PmDictCategory> addCategory(PmDictCategory category);

    /**
     * 编辑节点
     *
     * @param category 字典分类对象
     * @return 编辑结果
     */
    Result<PmDictCategory> editCategory(PmDictCategory category);

    /**
     * 删除节点(连同子节点)
     *
     * @param id 分类ID
     * @return 删除结果
     */
    Result<Boolean> deleteCategory(String id);
}

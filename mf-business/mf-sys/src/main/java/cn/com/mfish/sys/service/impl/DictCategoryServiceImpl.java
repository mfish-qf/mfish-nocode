package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.DictCategory;
import cn.com.mfish.sys.mapper.DictCategoryMapper;
import cn.com.mfish.sys.req.ReqDictCategory;
import cn.com.mfish.sys.service.DictCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 树形分类
 * @author: mfish
 * @date: 2024-03-12
 * @version: V1.3.0
 */
@Service
public class DictCategoryServiceImpl extends ServiceImpl<DictCategoryMapper, DictCategory> implements DictCategoryService {
    @Override
    public Result<PageResult<DictCategory>> queryCategoryTree(ReqDictCategory reqDictCategory, ReqPage reqPage) {
        List<Integer> levels = buildParentLevel(reqDictCategory);
        PageResult<DictCategory> result = queryOneLevelCategory(reqDictCategory, reqPage, levels);
        if (!result.getList().isEmpty()) {
            List<DictCategory> list = baseMapper.queryChildCategory(reqDictCategory, levels, result.getList().stream().map(DictCategory::getTreeCode).collect(Collectors.toList()));
            List<DictCategory> trees = new ArrayList<>();
            TreeUtils.buildTree("", list, trees, DictCategory.class);
            return Result.ok(new PageResult<>(trees, result.getPageNum(), result.getPageSize(), result.getTotal()), "分类树-查询成功!");
        }
        return Result.ok(result, "分类树-查询成功!");
    }

    @Override
    public Result<List<DictCategory>> queryCategoryTree(ReqDictCategory reqDictCategory) {
        List<DictCategory> list = queryCategory(reqDictCategory);
        List<DictCategory> trees = new ArrayList<>();
        TreeUtils.buildTree("", list, trees, DictCategory.class);
        return Result.ok(trees, "分类树-查询成功!");
    }

    @Override
    public List<DictCategory> queryCategory(ReqDictCategory reqDictCategory) {
        List<Integer> list = buildParentLevel(reqDictCategory);
        return baseMapper.queryCategory(reqDictCategory, list);
    }

    @Override
    public PageResult<DictCategory> queryOneLevelCategory(ReqDictCategory reqDictCategory, ReqPage reqPage) {
        List<Integer> list = buildParentLevel(reqDictCategory);
        return queryOneLevelCategory(reqDictCategory, reqPage, list);
    }

    public PageResult<DictCategory> queryOneLevelCategory(ReqDictCategory reqDictCategory, ReqPage reqPage, List<Integer> list) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return new PageResult<>(baseMapper.queryOneLevelCategory(reqDictCategory, list));
    }

    private List<Integer> buildParentLevel(ReqDictCategory reqDictCategory) {
        //查询满足条件的父级节点等级
        Integer level = baseMapper.queryMaxCategoryLevel(reqDictCategory);
        List<Integer> list = new ArrayList<>();
        if (level != null) {
            for (int i = 1; i < level; i++) {
                list.add(i);
            }
        }
        return list;
    }

    @Override
    public Result<DictCategory> insertCategory(DictCategory category) {
        Result<DictCategory> result = verifyCategory(category);
        if (!result.isSuccess()) {
            return result;
        }
        if (baseMapper.insertCategory(category) == 1) {
            return Result.ok(category, "分类-添加成功!");
        }
        return Result.fail("错误:分类-添加失败!");
    }

    private Result<DictCategory> verifyCategory(DictCategory dictCategory) {
        //parentId为空时，设置为空字符串
        if (StringUtils.isEmpty(dictCategory.getParentId())) {
            dictCategory.setParentId("");
        } else if (dictCategory.getParentId().equals(dictCategory.getId())) {
            return Result.fail("错误：父节点不允许设置自己");
        }
        if (!StringUtils.isEmpty(dictCategory.getCategoryCode()) && baseMapper.exists(new LambdaQueryWrapper<DictCategory>()
                .eq(DictCategory::getCategoryCode, dictCategory.getCategoryCode())
                .ne(!StringUtils.isEmpty(dictCategory.getId()), DictCategory::getId, dictCategory.getId()))) {
            return Result.fail("错误：分类编码已存在");
        }
        return Result.ok("分类校验成功");
    }

    @Override
    @Transactional
    public Result<Boolean> deleteCategory(String categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            return Result.fail(false, "错误:分类ID不允许为空");
        }
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<DictCategory>().eq(DictCategory::getParentId, categoryId));
        if (count > 0) {
            return Result.fail(false, "错误:分类包含子节点，不允许删除");
        }
        if (baseMapper.deleteById(categoryId) > 0) {
            return Result.ok("分类-删除成功!");
        }
        return Result.fail("错误:分类-删除失败!");
    }

    @Override
    @Transactional
    public Result<DictCategory> updateCategory(DictCategory category) {
        Result<DictCategory> result = verifyCategory(category);
        if (!result.isSuccess()) {
            return result;
        }
        DictCategory oldCategory = baseMapper.selectById(category.getId());
        if (oldCategory == null) {
            throw new MyRuntimeException("错误:未找到分类");
        }
        if ((StringUtils.isEmpty(oldCategory.getParentId()) && StringUtils.isEmpty(category.getParentId())) ||
                (!StringUtils.isEmpty(oldCategory.getParentId()) && oldCategory.getParentId().equals(category.getParentId()))) {
            if (baseMapper.updateById(category) <= 0) {
                throw new MyRuntimeException("错误:更新分类失败");
            }
        }
        List<DictCategory> list = baseMapper.selectList(new LambdaQueryWrapper<DictCategory>()
                .likeRight(DictCategory::getTreeCode, oldCategory.getTreeCode()).orderByAsc(DictCategory::getTreeCode));
        if (list == null || list.isEmpty()) {
            throw new MyRuntimeException("错误:未查询到分类");
        }
        list.set(0, category);
        //父节点发生变化，重新生成序列
        baseMapper.deleteBatchIds(list.stream().map(DictCategory::getId).collect(Collectors.toList()));
        for (DictCategory dictCategory : list) {
            if (baseMapper.insertCategory(dictCategory) <= 0) {
                throw new MyRuntimeException("错误:更新分类失败");
            }
        }
        return Result.ok(category, "分类-更新成功!");

    }

    @Override
    public List<DictCategory> queryCategoryTreeByCode(String fixCode, TreeDirection direction) {
        if (StringUtils.isEmpty(fixCode)) {
            throw new MyRuntimeException("错误:固定编码不允许为空");
        }
        DictCategory category = baseMapper.selectOne(new LambdaQueryWrapper<DictCategory>().eq(DictCategory::getCategoryCode, fixCode));
        List<DictCategory> categoryList = queryCategory(category, direction);
        List<DictCategory> categoryTree = new ArrayList<>();
        if (direction.equals(TreeDirection.向下)) {
            TreeUtils.buildTree(category.getParentId(), categoryList, categoryTree, DictCategory.class);
        } else {
            TreeUtils.buildTree("", categoryList, categoryTree, DictCategory.class);
        }
        return categoryTree;
    }

    @Override
    public List<DictCategory> queryCategoryListByCode(String fixCode, TreeDirection direction) {
        if (StringUtils.isEmpty(fixCode)) {
            throw new MyRuntimeException("错误:固定编码不允许为空");
        }
        DictCategory category = baseMapper.selectOne(new LambdaQueryWrapper<DictCategory>().eq(DictCategory::getCategoryCode, fixCode));
        return queryCategory(category, direction);

    }

    @Override
    public boolean includeCategory(String categoryId, String fixCode, TreeDirection direction) {
        List<DictCategory> list = queryCategoryListByCode(fixCode, direction);
        return list.stream().anyMatch((item) -> item.getId().equals(categoryId));
    }

    private List<DictCategory> queryCategory(DictCategory category, TreeDirection direction) {
        if (category == null) {
            return new ArrayList<>();
        }
        List<DictCategory> categoryList;
        switch (direction) {
            case 向下:
                categoryList = downCategory(category.getTreeCode());
                break;
            case 向上:
                categoryList = upCategory(category.getTreeCode(), category.getTreeLevel());
                break;
            default:
                categoryList = downCategory(category.getTreeCode());
                //向下已经查了自己，向上查询不查
                List<DictCategory> ups = upCategory(category.getTreeCode(), category.getTreeLevel() - 1);
                categoryList.addAll(0, ups);
                break;
        }
        return categoryList;
    }

    /**
     * 向下查询分类
     *
     * @param treeCode 树编码
     * @return
     */
    private List<DictCategory> downCategory(String treeCode) {
        return baseMapper.selectList(new LambdaQueryWrapper<DictCategory>().likeRight(DictCategory::getTreeCode, treeCode).orderByAsc(DictCategory::getTreeLevel, DictCategory::getSort));
    }

    /**
     * 向上查询分类
     *
     * @param treeCode 树编码
     * @param level    等级
     * @return
     */
    private List<DictCategory> upCategory(String treeCode, int level) {
        List<String> list = new ArrayList<>();
        if (level < 1) {
            return new ArrayList<>();
        }
        for (int i = 1; i <= level; i++) {
            list.add(treeCode.substring(0, i * 5));
        }
        return baseMapper.selectList(new LambdaQueryWrapper<DictCategory>().in(DictCategory::getTreeCode, list).orderByAsc(DictCategory::getTreeLevel, DictCategory::getSort));
    }
}

package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.sys.req.ReqDictCategory;
import cn.com.mfish.common.sys.service.DictCategoryService;
import cn.com.mfish.sys.api.entity.DictCategory;
import cn.com.mfish.sys.mapper.DictCategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 树形分类
 * @author: mfish
 * @date: 2024-03-12
 * @version: V2.3.1
 */
@Service
public class DictCategoryServiceImpl extends ServiceImpl<DictCategoryMapper, DictCategory> implements DictCategoryService {
    /**
     * 分页查询分类树（先查一级分类，再查子分类构建树）
     *
     * @param reqDictCategory 查询条件
     * @param reqPage         分页参数
     * @return 分类树分页结果
     */
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

    /**
     * 查询分类树（不分页）
     *
     * @param reqDictCategory 查询条件
     * @return 分类树列表
     */
    @Override
    public Result<List<DictCategory>> queryCategoryTree(ReqDictCategory reqDictCategory) {
        List<DictCategory> list = queryCategory(reqDictCategory);
        List<DictCategory> trees = new ArrayList<>();
        TreeUtils.buildTree("", list, trees, DictCategory.class);
        return Result.ok(trees, "分类树-查询成功!");
    }

    /**
     * 查询分类列表（扁平结构）
     *
     * @param reqDictCategory 查询条件
     * @return 分类列表
     */
    @Override
    public List<DictCategory> queryCategory(ReqDictCategory reqDictCategory) {
        List<Integer> list = buildParentLevel(reqDictCategory);
        return baseMapper.queryCategory(reqDictCategory, list);
    }

    /**
     * 分页查询一级分类列表
     *
     * @param reqDictCategory 查询条件
     * @param reqPage         分页参数
     * @return 一级分类分页结果
     */
    @Override
    public PageResult<DictCategory> queryOneLevelCategory(ReqDictCategory reqDictCategory, ReqPage reqPage) {
        List<Integer> list = buildParentLevel(reqDictCategory);
        return queryOneLevelCategory(reqDictCategory, reqPage, list);
    }

    /**
     * 根据父级等级列表分页查询一级分类
     *
     * @param reqDictCategory 查询条件
     * @param reqPage         分页参数
     * @param list            父级等级列表
     * @return 一级分类分页结果
     */
    public PageResult<DictCategory> queryOneLevelCategory(ReqDictCategory reqDictCategory, ReqPage reqPage, List<Integer> list) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return new PageResult<>(baseMapper.queryOneLevelCategory(reqDictCategory, list));
    }

    /**
     * 构建父级等级列表，用于查询满足条件的父级节点
     *
     * @param reqDictCategory 查询条件
     * @return 父级等级列表
     */
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

    /**
     * 新增分类
     *
     * @param category 分类对象
     * @return 新增结果
     */
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

    /**
     * 校验分类信息（父节点不能设为自身，分类编码不允许重复）
     *
     * @param dictCategory 分类对象
     * @return 校验结果
     */
    private Result<DictCategory> verifyCategory(DictCategory dictCategory) {
        //parentId为空时，设置为空字符串
        if (StringUtils.isEmpty(dictCategory.getParentId())) {
            dictCategory.setParentId("");
        } else if (dictCategory.getId().equals(dictCategory.getParentId())) {
            return Result.fail("错误：父节点不允许设置自己");
        }
        if (!StringUtils.isEmpty(dictCategory.getCategoryCode()) && baseMapper.exists(new LambdaQueryWrapper<DictCategory>()
                .eq(DictCategory::getCategoryCode, dictCategory.getCategoryCode())
                .ne(!StringUtils.isEmpty(dictCategory.getId()), DictCategory::getId, dictCategory.getId()))) {
            return Result.fail("错误：分类编码已存在");
        }
        return Result.ok("分类校验成功");
    }

    /**
     * 删除分类（包含子节点时不允许删除）
     *
     * @param categoryId 分类ID
     * @return 删除结果
     */
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

    /**
     * 更新分类（父节点变更时重新生成树编码序列）
     *
     * @param category 分类对象
     * @return 更新结果
     */
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
            return Result.ok(category, "分类-更新成功!");
        }
        List<DictCategory> list = baseMapper.selectList(new LambdaQueryWrapper<DictCategory>()
                .likeRight(DictCategory::getTreeCode, oldCategory.getTreeCode()).orderByAsc(DictCategory::getTreeCode));
        if (list == null || list.isEmpty()) {
            throw new MyRuntimeException("错误:未查询到分类");
        }
        list.set(0, category);
        //父节点发生变化，重新生成序列
        baseMapper.deleteByIds(list.stream().map(DictCategory::getId).collect(Collectors.toList()));
        for (DictCategory dictCategory : list) {
            if (baseMapper.insertCategory(dictCategory) <= 0) {
                throw new MyRuntimeException("错误:更新分类失败");
            }
        }
        return Result.ok(category, "分类-更新成功!");

    }

    /**
     * 通过分类编码查询分类树
     *
     * @param fixCode   固定编码
     * @param direction 查询方向
     * @return 分类树列表
     */
    @Override
    public List<DictCategory> queryCategoryTreeByCode(String fixCode, TreeDirection direction) {
        if (StringUtils.isEmpty(fixCode)) {
            throw new MyRuntimeException("错误:固定编码不允许为空");
        }
        DictCategory category = baseMapper.selectOne(new LambdaQueryWrapper<DictCategory>().eq(DictCategory::getCategoryCode, fixCode));
        return queryCategoryTree(category, direction);
    }

    /**
     * 通过分类编码查询分类列表（扁平结构）
     *
     * @param fixCode   固定编码
     * @param direction 查询方向
     * @return 分类列表
     */
    @Override
    public List<DictCategory> queryCategoryListByCode(String fixCode, TreeDirection direction) {
        if (StringUtils.isEmpty(fixCode)) {
            throw new MyRuntimeException("错误:固定编码不允许为空");
        }
        DictCategory category = baseMapper.selectOne(new LambdaQueryWrapper<DictCategory>().eq(DictCategory::getCategoryCode, fixCode));
        return queryCategory(category, direction);

    }

    /**
     * 通过分类ID查询分类树
     *
     * @param id        分类ID
     * @param direction 查询方向
     * @return 分类树列表
     */
    @Override
    public List<DictCategory> queryCategoryTreeById(String id, TreeDirection direction) {
        if (StringUtils.isEmpty(id)) {
            throw new MyRuntimeException("错误:id不允许为空");
        }
        DictCategory category = baseMapper.selectOne(new LambdaQueryWrapper<DictCategory>().eq(DictCategory::getId, id));
        return queryCategoryTree(category, direction);
    }

    /**
     * 通过分类ID查询分类列表（扁平结构）
     *
     * @param id        分类ID
     * @param direction 查询方向
     * @return 分类列表
     */
    @Override
    public List<DictCategory> queryCategoryListById(String id, TreeDirection direction) {
        if (StringUtils.isEmpty(id)) {
            throw new MyRuntimeException("错误:id不允许为空");
        }
        DictCategory category = baseMapper.selectOne(new LambdaQueryWrapper<DictCategory>().eq(DictCategory::getId, id));
        return queryCategory(category, direction);
    }

    /**
     * 判断分类ID是否在指定编码的分类范围内
     *
     * @param categoryId 分类ID
     * @param fixCode    固定编码
     * @param direction  查询方向
     * @return 是否包含
     */
    @Override
    public boolean includeCategory(String categoryId, String fixCode, TreeDirection direction) {
        List<DictCategory> list = queryCategoryListByCode(fixCode, direction);
        return list.stream().anyMatch((item) -> item.getId().equals(categoryId));
    }

    /**
     * 查询分类树（根据方向构建不同范围的树结构）
     *
     * @param category  分类对象
     * @param direction 查询方向
     * @return 分类树列表
     */
    private List<DictCategory> queryCategoryTree(DictCategory category, TreeDirection direction) {
        if (category == null) {
            return new ArrayList<>();
        }
        List<DictCategory> categoryList = queryCategory(category, direction);
        List<DictCategory> categoryTree = new ArrayList<>();
        if (TreeDirection.向下.equals(direction)) {
            TreeUtils.buildTree(category.getParentId(), categoryList, categoryTree, DictCategory.class);
        } else {
            TreeUtils.buildTree("", categoryList, categoryTree, DictCategory.class);
        }
        return categoryTree;
    }

    /**
     * 根据方向查询分类列表（支持向上、向下、当前、全部方向）
     *
     * @param category  分类对象
     * @param direction 查询方向
     * @return 分类列表
     */
    private List<DictCategory> queryCategory(DictCategory category, TreeDirection direction) {
        if (category == null) {
            return new ArrayList<>();
        }
        List<DictCategory> categoryList;
        switch (direction) {
            case 当前:
                return Collections.singletonList(category);
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
     * @return 返回分类列表
     */
    private List<DictCategory> downCategory(String treeCode) {
        return baseMapper.selectList(new LambdaQueryWrapper<DictCategory>().likeRight(DictCategory::getTreeCode, treeCode).orderByAsc(DictCategory::getTreeLevel, DictCategory::getSort));
    }

    /**
     * 向上查询分类
     *
     * @param treeCode 树编码
     * @param level    等级
     * @return 返回分类列表
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

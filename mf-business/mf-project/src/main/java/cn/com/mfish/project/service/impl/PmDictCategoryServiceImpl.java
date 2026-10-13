package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.entity.PmDictCategory;
import cn.com.mfish.project.mapper.PmDictCategoryMapper;
import cn.com.mfish.project.service.PmDictCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 树形业务字典与功能层级分类
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Service
public class PmDictCategoryServiceImpl extends ServiceImpl<PmDictCategoryMapper, PmDictCategory> implements PmDictCategoryService {

    @Override
    public Result<List<PmDictCategory>> tree() {
        List<PmDictCategory> all = list(new LambdaQueryWrapper<PmDictCategory>()
                .orderByAsc(PmDictCategory::getTreeCode));
        // 以父ID分组
        Map<String, List<PmDictCategory>> childrenMap = new LinkedHashMap<>();
        for (PmDictCategory node : all) {
            String parentKey = StringUtils.isEmpty(node.getParentId()) ? "" : node.getParentId();
            childrenMap.computeIfAbsent(parentKey, k -> new ArrayList<>()).add(node);
        }
        // 递归组装树
        List<PmDictCategory> tree = buildTree("", childrenMap);
        return Result.ok(tree, "树形字典-查询成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmDictCategory> addCategory(PmDictCategory category) {
        verifyCategory(category);
        PmDictCategory parent = null;
        if (StringUtils.isNotEmpty(category.getParentId())) {
            parent = getById(category.getParentId());
            if (parent == null) {
                throw new MyRuntimeException("错误:父分类不存在!");
            }
        }
        // 自动维护 treeLevel 与 treeCode(如 001、001001)
        int treeLevel = parent == null ? 1 : parent.getTreeLevel() + 1;
        String parentTreeCode = parent == null ? "" : parent.getTreeCode();
        String treeCode = parentTreeCode + nextSiblingSeq(category.getParentId(), parentTreeCode);
        category.setTreeLevel(treeLevel);
        category.setTreeCode(treeCode);
        if (category.getSort() == null) {
            category.setSort((int) count(new LambdaQueryWrapper<PmDictCategory>()
                    .eq(StringUtils.isNotEmpty(category.getParentId()), PmDictCategory::getParentId, category.getParentId())) + 1);
        }
        category.setCreateBy(safeGetCurrentUser());
        category.setCreateTime(new Date());
        if (save(category)) {
            return Result.ok(category, "分类-创建成功!");
        }
        return Result.fail(category, "错误:分类-创建失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmDictCategory> editCategory(PmDictCategory category) {
        if (category == null || StringUtils.isEmpty(category.getId())) {
            throw new MyRuntimeException("错误:分类ID不能为空!");
        }
        PmDictCategory exist = getById(category.getId());
        if (exist == null) {
            throw new MyRuntimeException("错误:分类不存在!");
        }
        verifyCategory(category);
        // 不允许将节点移动到自己或自己的子孙节点下
        if (StringUtils.isNotEmpty(category.getParentId())
                && (category.getId().equals(category.getParentId())
                || isDescendant(category.getParentId(), category.getId()))) {
            throw new MyRuntimeException("错误:不能将分类移动到自身或其子节点下!");
        }
        category.setUpdateBy(safeGetCurrentUser());
        category.setUpdateTime(new Date());
        // 父节点未变化时不重新计算树编码
        if (StringUtils.isEmpty(category.getParentId()) && StringUtils.isNotEmpty(exist.getParentId())
                || StringUtils.isNotEmpty(category.getParentId())
                && !category.getParentId().equals(StringUtils.isEmpty(exist.getParentId()) ? "" : exist.getParentId())) {
            PmDictCategory parent = StringUtils.isEmpty(category.getParentId()) ? null : getById(category.getParentId());
            int treeLevel = parent == null ? 1 : parent.getTreeLevel() + 1;
            String parentTreeCode = parent == null ? "" : parent.getTreeCode();
            category.setTreeLevel(treeLevel);
            category.setTreeCode(parentTreeCode + nextSiblingSeq(category.getParentId(), parentTreeCode));
        } else {
            category.setTreeLevel(exist.getTreeLevel());
            category.setTreeCode(exist.getTreeCode());
        }
        if (updateById(category)) {
            return Result.ok(category, "分类-更新成功!");
        }
        return Result.fail(category, "错误:分类-更新失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteCategory(String id) {
        PmDictCategory exist = getById(id);
        if (exist == null) {
            throw new MyRuntimeException("错误:分类不存在!");
        }
        // 收集自身及所有子孙节点后统一删除
        List<String> ids = new ArrayList<>();
        collectDescendantIds(id, ids);
        if (removeByIds(ids)) {
            return Result.ok(true, "分类删除成功(含 " + ids.size() + " 个节点)!");
        }
        return Result.fail(false, "错误:分类-删除失败!");
    }

    /**
     * 递归组装树形结构
     */
    private List<PmDictCategory> buildTree(String parentId, Map<String, List<PmDictCategory>> childrenMap) {
        List<PmDictCategory> children = childrenMap.getOrDefault(parentId, new ArrayList<>());
        for (PmDictCategory child : children) {
            child.setChildren(buildTree(child.getId(), childrenMap));
        }
        return children;
    }

    /**
     * 计算同级节点中的下一个三位序号(如 001、002)
     */
    private String nextSiblingSeq(String parentId, String parentTreeCode) {
        List<PmDictCategory> siblings = list(new LambdaQueryWrapper<PmDictCategory>()
                .eq(StringUtils.isNotEmpty(parentId), PmDictCategory::getParentId, parentId)
                .isNull(StringUtils.isEmpty(parentId), PmDictCategory::getParentId)
                .likeRight(PmDictCategory::getTreeCode, parentTreeCode));
        int maxSeq = 0;
        for (PmDictCategory sibling : siblings) {
            String treeCode = sibling.getTreeCode();
            if (StringUtils.isEmpty(treeCode) || treeCode.length() < parentTreeCode.length() + 3) {
                continue;
            }
            String suffix = treeCode.substring(parentTreeCode.length(), parentTreeCode.length() + 3);
            try {
                int seq = Integer.parseInt(suffix);
                if (seq > maxSeq) {
                    maxSeq = seq;
                }
            } catch (NumberFormatException ignored) {
                // 非数字编码跳过
            }
        }
        return String.format("%03d", maxSeq + 1);
    }

    /**
     * 递归收集自身及所有子孙节点ID
     */
    private void collectDescendantIds(String id, List<String> ids) {
        ids.add(id);
        List<PmDictCategory> children = list(new LambdaQueryWrapper<PmDictCategory>()
                .eq(PmDictCategory::getParentId, id));
        for (PmDictCategory child : children) {
            collectDescendantIds(child.getId(), ids);
        }
    }

    /**
     * 判断目标节点是否为指定节点的子孙节点
     */
    private boolean isDescendant(String targetId, String ancestorId) {
        List<String> descendants = new ArrayList<>();
        collectDescendantIds(ancestorId, descendants);
        return descendants.contains(targetId);
    }

    /**
     * 校验分类必填信息
     */
    private void verifyCategory(PmDictCategory category) {
        if (category == null) {
            throw new MyRuntimeException("错误:分类对象不能为空!");
        }
        if (StringUtils.isEmpty(category.getCategoryName())) {
            throw new MyRuntimeException("错误:分类名称不能为空!");
        }
    }

    /**
     * 安全获取当前登录用户ID(无请求上下文时返回空)
     */
    private String safeGetCurrentUser() {
        try {
            String userId = AuthInfoUtils.getCurrentUserId();
            return StringUtils.isEmpty(userId) ? AuthInfoUtils.getCurrentAccount() : userId;
        } catch (Exception e) {
            log.warn("获取当前登录用户失败: {}", e.getMessage());
            return null;
        }
    }
}

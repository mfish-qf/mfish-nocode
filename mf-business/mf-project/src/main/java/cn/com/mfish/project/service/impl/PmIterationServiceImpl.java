package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.entity.PmIteration;
import cn.com.mfish.project.entity.PmIterationTestNode;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.mapper.PmIterationMapper;
import cn.com.mfish.project.mapper.PmIterationTestNodeMapper;
import cn.com.mfish.project.mapper.PmRequirementMapper;
import cn.com.mfish.project.service.PmIterationService;
import cn.com.mfish.project.utils.ScheduleDateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @description: 迭代版本
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Service
public class PmIterationServiceImpl extends ServiceImpl<PmIterationMapper, PmIteration> implements PmIterationService {

    /**
     * 归档/发布标记: 版本状态为"已发布"即视为已归档(不新增字段, 复用 status)
     * 与 PmRequirementServiceImpl.ARCHIVED_STATUS 约定一致
     */
    private static final String ARCHIVED_STATUS = "已发布";

    @Resource
    private PmIterationTestNodeMapper testNodeMapper;
    @Resource
    private PmRequirementMapper requirementMapper;

    @Override
    public Result<List<PmIteration>> queryList() {
        List<PmIteration> list = list(new LambdaQueryWrapper<PmIteration>()
                .orderByDesc(PmIteration::getStartDate));
        for (PmIteration iteration : list) {
            fillIterationDetails(iteration);
        }
        return Result.ok(list, "迭代版本-查询成功!");
    }

    @Override
    public Result<PageResult<PmIteration>> queryPageList(Integer pageNum, Integer pageSize) {
        // 分页参数兜底(与回收站 listDeleted 同口径): 页码<1 按 1, 每页<1 按 10
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        // 排序与 queryList 保持一致(研发开始时间倒序), 保证第 1 页即当前全量列表最新的若干条
        PageHelper.startPage(num, size);
        List<PmIteration> list = list(new LambdaQueryWrapper<PmIteration>()
                .orderByDesc(PmIteration::getStartDate));
        for (PmIteration iteration : list) {
            fillIterationDetails(iteration);
        }
        return Result.ok(new PageResult<>(list), "迭代版本-查询成功!");
    }

    @Override
    public Result<PmIteration> queryByIdOrCode(String idOrCode) {
        PmIteration iteration = getOne(new LambdaQueryWrapper<PmIteration>()
                .eq(PmIteration::getId, idOrCode)
                .or()
                .eq(PmIteration::getCode, idOrCode)
                .last("limit 1"));
        if (iteration == null) {
            return Result.fail("错误:未找到迭代 " + idOrCode);
        }
        fillIterationDetails(iteration);
        return Result.ok(iteration, "迭代版本-查询成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmIteration> addIteration(PmIteration iteration) {
        verifyIteration(iteration);
        Date now = new Date();
        iteration.setTenantId(safeGetCurrentTenantId());
        iteration.setCreateBy(safeGetCurrentUser());
        iteration.setCreateTime(now);
        if (save(iteration)) {
            insertTestNodes(iteration.getTestNodes(), iteration.getId());
            fillIterationDetails(iteration);
            return Result.ok(iteration, "迭代版本-添加成功!");
        }
        return Result.fail(iteration, "错误:迭代版本-添加失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmIteration> editIteration(PmIteration iteration) {
        if (iteration == null || StringUtils.isEmpty(iteration.getId())) {
            throw new MyRuntimeException("错误:迭代ID不能为空!");
        }
        PmIteration exist = getById(iteration.getId());
        if (exist == null) {
            throw new MyRuntimeException("错误:迭代不存在!");
        }
        verifyIteration(iteration);
        iteration.setUpdateBy(safeGetCurrentUser());
        iteration.setUpdateTime(new Date());
        // 租户ID 由库内已有值兜底, 前端不传, 防止误清空
        iteration.setTenantId(exist.getTenantId());
        if (updateById(iteration)) {
            syncTestNodes(iteration);
            fillIterationDetails(iteration);
            return Result.ok(iteration, "迭代版本-编辑成功!");
        }
        return Result.fail(iteration, "错误:迭代版本-编辑失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteIteration(String id) {
        PmIteration iteration = getById(id);
        if (iteration == null) {
            throw new MyRuntimeException("错误:迭代不存在!");
        }
        // 已发布(含归档)的版本不允许删除: 发布记录需留存作为版本追溯, 与前端隐藏删除入口双向兜底
        if (ARCHIVED_STATUS.equals(iteration.getStatus())) {
            throw new MyRuntimeException("错误:已发布的版本不允许删除!");
        }
        // 已绑定需求的版本不允许删除：避免需求失去版本归属成为孤儿
        long boundReqCount = requirementMapper.selectCount(new LambdaQueryWrapper<PmRequirement>()
                .eq(PmRequirement::getIterationId, id));
        if (boundReqCount > 0) {
            throw new MyRuntimeException("错误:该版本已绑定 " + boundReqCount + " 条需求, 不允许删除!");
        }
        if (removeById(id)) {
            testNodeMapper.delete(new LambdaQueryWrapper<PmIterationTestNode>()
                    .eq(PmIterationTestNode::getIterationId, id));
            return Result.ok(true, "迭代版本-删除成功!");
        }
        return Result.fail(false, "错误:迭代版本-删除失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmIterationTestNode> addTestNode(String iterationId, PmIterationTestNode node) {
        PmIteration iteration = getById(iterationId);
        if (iteration == null) {
            throw new MyRuntimeException("错误:未找到迭代 " + iterationId);
        }
        if (node == null || StringUtils.isEmpty(node.getName()) || node.getNodeDate() == null) {
            throw new MyRuntimeException("错误:转测节点名称与转测时间不能为空!");
        }
        node.setIterationId(iterationId);
        node.setCreateBy(safeGetCurrentUser());
        node.setCreateTime(new Date());
        if (testNodeMapper.insert(node) > 0) {
            return Result.ok(node, "已成功添加转测节点: " + node.getName());
        }
        return Result.fail(node, "错误:添加转测节点失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteTestNode(String nodeId) {
        PmIterationTestNode node = testNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new MyRuntimeException("错误:转测节点不存在!");
        }
        if (testNodeMapper.deleteById(nodeId) > 0) {
            return Result.ok(true, "转测节点已删除!");
        }
        return Result.fail(false, "错误:删除转测节点失败!");
    }

    @Override
    public Result<Boolean> archiveIteration(String id) {
        return updateIterationStatus(id, "已发布", "归档");
    }

    @Override
    public Result<Boolean> unarchiveIteration(String id) {
        return updateIterationStatus(id, "进行中", "取消归档");
    }

    /**
     * 归档/取消归档: 仅更新版本状态(以"已发布"作为归档标记)
     * 刻意不走 editIteration —— 避免部分字段更新时 syncTestNodes 将转测节点清空
     */
    private Result<Boolean> updateIterationStatus(String id, String status, String action) {
        if (StringUtils.isEmpty(id)) {
            throw new MyRuntimeException("错误:迭代ID不能为空!");
        }
        PmIteration exist = getById(id);
        if (exist == null) {
            throw new MyRuntimeException("错误:迭代不存在!");
        }
        if (status.equals(exist.getStatus())) {
            return Result.ok(true, "迭代版本已处于" + action + "状态");
        }
        boolean updated = update(new LambdaUpdateWrapper<PmIteration>()
                .eq(PmIteration::getId, id)
                .set(PmIteration::getStatus, status)
                .set(PmIteration::getUpdateBy, safeGetCurrentUser())
                .set(PmIteration::getUpdateTime, new Date()));
        if (updated) {
            return Result.ok(true, "迭代版本" + action + "成功!");
        }
        return Result.fail(false, "错误:迭代版本" + action + "失败!");
    }

    /**
     * 校验迭代必填信息
     */
    private void verifyIteration(PmIteration iteration) {
        if (iteration == null) {
            throw new MyRuntimeException("错误:迭代对象不能为空!");
        }
        if (StringUtils.isEmpty(iteration.getCode())) {
            throw new MyRuntimeException("错误:迭代版本号不能为空!");
        }
        // 版本号(code)允许重复: 多个相同版本号可并存, 增改一律以主键 id 操作, 不做版本号唯一性校验
    }

    /**
     * 填充迭代明细(转测节点列表 + 跨月标签)
     */
    private void fillIterationDetails(PmIteration iteration) {
        // 转测节点列表
        List<PmIterationTestNode> nodes = testNodeMapper.selectList(
                new LambdaQueryWrapper<PmIterationTestNode>()
                        .eq(PmIterationTestNode::getIterationId, iteration.getId())
                        .orderByAsc(PmIterationTestNode::getNodeDate));
        iteration.setTestNodes(nodes);
        // 自动计算跨月标签
        iteration.setSpannedMonths(ScheduleDateUtils.getSpannedMonths(
                iteration.getStartDate() == null ? null : toLocalDate(iteration.getStartDate()),
                iteration.getEndDate() == null ? null : toLocalDate(iteration.getEndDate())));
    }

    /**
     * 编辑时同步子表转测节点(新增新节点、删除被移除的节点)
     */
    private void syncTestNodes(PmIteration iteration) {
        List<PmIterationTestNode> incoming = iteration.getTestNodes() == null
                ? new ArrayList<>() : iteration.getTestNodes();
        List<PmIterationTestNode> existing = testNodeMapper.selectList(
                new LambdaQueryWrapper<PmIterationTestNode>()
                        .eq(PmIterationTestNode::getIterationId, iteration.getId()));
        // 库表中已存在的节点 id 集合(用于区分「更新已有节点」与「新增节点」)
        Set<String> existingIds = new HashSet<>();
        for (PmIterationTestNode existNode : existing) {
            if (StringUtils.isNotEmpty(existNode.getId())) {
                existingIds.add(existNode.getId());
            }
        }
        // 删除已移除的节点
        Set<String> incomingIds = new HashSet<>();
        for (PmIterationTestNode node : incoming) {
            if (StringUtils.isNotEmpty(node.getId())) {
                incomingIds.add(node.getId());
            }
        }
        for (PmIterationTestNode existNode : existing) {
            if (!incomingIds.contains(existNode.getId())) {
                testNodeMapper.deleteById(existNode.getId());
            }
        }
        // 新增或更新节点：仅当 id 真实存在于库表时才走更新，否则一律当作新节点插入
        // (前端在编辑页新增的节点会带 tn- 临时 id，不能误判为「更新已有记录」，否则 updateById 影响 0 行导致节点丢失)
        for (PmIterationTestNode node : incoming) {
            boolean isKnownExisting = StringUtils.isNotEmpty(node.getId())
                    && existingIds.contains(node.getId());
            if (isKnownExisting) {
                node.setIterationId(iteration.getId());
                node.setUpdateBy(safeGetCurrentUser());
                node.setUpdateTime(new Date());
                testNodeMapper.updateById(node);
            } else {
                node.setId(null);
                node.setIterationId(iteration.getId());
                node.setCreateBy(safeGetCurrentUser());
                node.setCreateTime(new Date());
                testNodeMapper.insert(node);
            }
        }
    }

    /**
     * 批量插入转测节点
     */
    private void insertTestNodes(List<PmIterationTestNode> nodes, String iterationId) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (PmIterationTestNode node : nodes) {
            node.setId(null);
            node.setIterationId(iterationId);
            node.setCreateBy(safeGetCurrentUser());
            node.setCreateTime(new Date());
            testNodeMapper.insert(node);
        }
    }

    /**
     * Date 转 LocalDate(兼容 java.sql.Date)
     */
    private java.time.LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
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

    /**
     * 安全获取当前租户ID(无请求上下文时返回空, 不抛异常)
     */
    private String safeGetCurrentTenantId() {
        try {
            return AuthInfoUtils.getCurrentTenantId();
        } catch (Exception e) {
            log.warn("获取当前租户ID失败: {}", e.getMessage());
            return null;
        }
    }
}

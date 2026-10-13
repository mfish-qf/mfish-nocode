package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.dto.ScheduleConfigDTO;
import cn.com.mfish.project.entity.PmIteration;
import cn.com.mfish.project.entity.PmIterationTestNode;
import cn.com.mfish.project.entity.PmPersonnel;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.entity.PmRequirementHistory;
import cn.com.mfish.project.mapper.PmIterationMapper;
import cn.com.mfish.project.mapper.PmIterationTestNodeMapper;
import cn.com.mfish.project.mapper.PmPersonnelMapper;
import cn.com.mfish.project.mapper.PmRequirementHistoryMapper;
import cn.com.mfish.project.mapper.PmRequirementMapper;
import cn.com.mfish.project.req.BalancingPlanItem;
import cn.com.mfish.project.req.ReqPmRequirement;
import cn.com.mfish.project.req.ReqReassign;
import cn.com.mfish.project.req.ReqWorkload;
import cn.com.mfish.project.service.PmRequirementService;
import cn.com.mfish.project.service.PmScheduleConfigService;
import cn.com.mfish.project.utils.PersonnelNameUtils;
import cn.com.mfish.project.utils.ScheduleDateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description: 核心需求
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Service
public class PmRequirementServiceImpl extends ServiceImpl<PmRequirementMapper, PmRequirement> implements PmRequirementService {
    /** 已完成开发的状态集合(进入该状态后开发进度视为100%) */
    private static final Set<String> POST_DEV_STATUSES = Set.of("待转测", "测试中", "验收中", "已上线");

    @Resource
    private PmIterationMapper iterationMapper;
    @Resource
    private PmIterationTestNodeMapper testNodeMapper;
    @Resource
    private PmPersonnelMapper personnelMapper;
    @Resource
    private PmRequirementHistoryMapper historyMapper;
    @Resource
    private PmScheduleConfigService scheduleConfigService;

    @Override
    public Result<PageResult<PmRequirement>> queryPageList(ReqPmRequirement reqPmRequirement, Integer pageNum, Integer pageSize) {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        PageHelper.startPage(num, size);
        List<PmRequirement> list = queryList(reqPmRequirement);
        return Result.ok(new PageResult<>(list), "需求列表-查询成功!");
    }

    /**
     * 多维条件过滤查询(不分页)
     */
    private List<PmRequirement> queryList(ReqPmRequirement req) {
        if (req == null) {
            return list(new LambdaQueryWrapper<PmRequirement>().orderByAsc(PmRequirement::getReqNo));
        }
        String month = StringUtils.isEmpty(req.getMonth()) || "ALL".equals(req.getMonth()) ? null : req.getMonth();
        LambdaQueryWrapper<PmRequirement> wrapper = new LambdaQueryWrapper<PmRequirement>()
                // month 支持按月份代码或迭代ID匹配
                .and(month != null && !"UNASSIGNED".equals(month), w -> w
                        .eq(PmRequirement::getMonth, month)
                        .or()
                        .eq(PmRequirement::getIterationId, month))
                .eq(StringUtils.isNotEmpty(req.getIterationId()), PmRequirement::getIterationId, req.getIterationId())
                .eq(StringUtils.isNotEmpty(req.getStatus()), PmRequirement::getStatus, req.getStatus())
                .eq(StringUtils.isNotEmpty(req.getPriority()), PmRequirement::getPriority, req.getPriority())
                .like(StringUtils.isNotEmpty(req.getFrontendDev()), PmRequirement::getFrontendDev, req.getFrontendDev())
                .like(StringUtils.isNotEmpty(req.getBackendDev()), PmRequirement::getBackendDev, req.getBackendDev())
                .like(StringUtils.isNotEmpty(req.getQaTester()), PmRequirement::getQaTester, req.getQaTester())
                // 关键词模糊匹配(模块/功能名)
                .and(StringUtils.isNotEmpty(req.getKeyword()), w -> w
                        .like(PmRequirement::getModule, req.getKeyword())
                        .or().like(PmRequirement::getLevel1Feature, req.getKeyword())
                        .or().like(PmRequirement::getLevel2Feature, req.getKeyword())
                        .or().like(PmRequirement::getLevel3Feature, req.getKeyword())
                        .or().like(PmRequirement::getFeatureDescription, req.getKeyword()))
                .orderByAsc(PmRequirement::getReqNo);
        return list(wrapper);
    }

    @Override
    public Result<PmRequirement> queryById(String id) {
        PmRequirement requirement = getById(id);
        if (requirement == null) {
            return Result.fail("错误:未找到需求 " + id);
        }
        return Result.ok(requirement, "需求-查询成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmRequirement> add(PmRequirement requirement) {
        if (requirement == null) {
            throw new MyRuntimeException("错误:需求对象不能为空!");
        }
        verifyRequirement(requirement);
        Date now = new Date();
        requirement.setCreateBy(safeGetCurrentUser());
        requirement.setCreateTime(now);
        // 未传序号时自动生成递增 req_no
        if (requirement.getReqNo() == null) {
            requirement.setReqNo(nextReqNo());
        }
        // 归属迭代信息联动(月份代码与发版时间)
        syncIterationInfo(requirement, requirement);
        // 状态与进度默认值
        if (StringUtils.isEmpty(requirement.getStatus())) {
            requirement.setStatus("待评审");
        }
        if (requirement.getDevProgress() == null) {
            requirement.setDevProgress(resolveDefaultProgress(requirement.getStatus()));
        }
        // 人员、工时与日历排期纠偏
        sanitizeRequirement(requirement, null);
        if (save(requirement)) {
            recordHistory(requirement, "INSERT", null, null, null, null, null, "创建需求 #" + requirement.getReqNo());
            return Result.ok(requirement, "需求 #" + requirement.getReqNo() + " 创建成功!");
        }
        return Result.fail(requirement, "错误:需求-添加失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmRequirement> edit(PmRequirement requirement) {
        if (requirement == null || StringUtils.isEmpty(requirement.getId())) {
            throw new MyRuntimeException("错误:需求ID不能为空!");
        }
        PmRequirement exist = getById(requirement.getId());
        if (exist == null) {
            throw new MyRuntimeException("错误:需求不存在!");
        }
        verifyRequirement(requirement);
        requirement.setUpdateBy(safeGetCurrentUser());
        requirement.setUpdateTime(new Date());
        // 归属迭代信息联动
        syncIterationInfo(requirement, exist);
        // 状态流转联动开发进度
        String newStatus = StringUtils.isEmpty(requirement.getStatus()) ? exist.getStatus() : requirement.getStatus();
        Integer newProgress = requirement.getDevProgress() != null ? requirement.getDevProgress() : exist.getDevProgress();
        requirement.setDevProgress(resolveStatusProgress(newStatus, newProgress, exist.getStatus()));
        // 修改工时或开始时间时联动重算截止时间
        sanitizeRequirement(requirement, exist);
        if (updateById(requirement)) {
            recordHistory(requirement, "UPDATE", null, null, null, exist.getStatus(), newStatus, "更新需求 #" + exist.getReqNo());
            PmRequirement updated = getById(requirement.getId());
            return Result.ok(updated, "需求 #" + exist.getReqNo() + " 更新成功，日历排期已同步至 " + formatDate(updated.getDevEndDate()));
        }
        return Result.fail(requirement, "错误:需求-编辑失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(String id) {
        PmRequirement exist = getById(id);
        if (exist == null) {
            throw new MyRuntimeException("错误:需求不存在!");
        }
        if (removeById(id)) {
            recordHistory(exist, "DELETE", null, null, null, exist.getStatus(), null, "删除需求 #" + exist.getReqNo());
            return Result.ok(true, "需求 #" + exist.getReqNo() + " 删除成功!");
        }
        return Result.fail(false, "错误:需求-删除失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteBatch(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        if (removeByIds(ids)) {
            List<PmRequirement> removed = list(new LambdaQueryWrapper<PmRequirement>().in(PmRequirement::getId, ids));
            for (PmRequirement req : removed) {
                recordHistory(req, "DELETE", null, null, null, req.getStatus(), null, "批量删除需求 #" + req.getReqNo());
            }
            return Result.ok(true, "已成功批量删除 " + ids.size() + " 项需求!");
        }
        return Result.fail(false, "错误:需求-批量删除失败!");
    }

    @Override
    public Result<PmRequirement> getDoc(String id) {
        PmRequirement requirement = getById(id);
        if (requirement == null) {
            return Result.fail("错误:未找到需求 " + id);
        }
        return Result.ok(requirement, "获取需求 #" + requirement.getReqNo() + " 文档成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmRequirement> saveDoc(String id, String prdDocContent, String docAuthor) {
        PmRequirement exist = getById(id);
        if (exist == null) {
            throw new MyRuntimeException("错误:未找到需求 " + id);
        }
        PmRequirement update = new PmRequirement();
        update.setId(id);
        update.setPrdDocContent(prdDocContent);
        update.setDocLastModified(new Date());
        update.setDocAuthor(StringUtils.isEmpty(docAuthor)
                ? (StringUtils.isEmpty(exist.getDocAuthor()) ? exist.getRequester() : exist.getDocAuthor())
                : docAuthor);
        update.setUpdateBy(safeGetCurrentUser());
        update.setUpdateTime(new Date());
        if (updateById(update)) {
            recordHistory(exist, "DOC", null, null, null, null, null, "保存需求 #" + exist.getReqNo() + " PRD详细文档");
            return Result.ok(getById(id), "需求 #" + exist.getReqNo() + " 详细 PRD 文档已保存");
        }
        return Result.fail("错误:保存需求文档失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<List<PmRequirement>> batchCreate(List<PmRequirement> requirements) {
        if (requirements == null || requirements.isEmpty()) {
            throw new MyRuntimeException("错误:导入列表不能为空!");
        }
        List<PmRequirement> created = new ArrayList<>();
        for (PmRequirement item : requirements) {
            item.setId(null);
            add(item);
            created.add(item);
        }
        return Result.ok(created, "成功批量导入 " + created.size() + " 项需求!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchUpdateStatus(List<String> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        if (StringUtils.isEmpty(status)) {
            throw new MyRuntimeException("错误:目标状态不能为空!");
        }
        List<PmRequirement> targets = list(new LambdaQueryWrapper<PmRequirement>().in(PmRequirement::getId, ids));
        Date now = new Date();
        for (PmRequirement req : targets) {
            PmRequirement update = new PmRequirement();
            update.setId(req.getId());
            update.setStatus(status);
            update.setDevProgress(resolveStatusProgress(status, req.getDevProgress(), req.getStatus()));
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(now);
            updateById(update);
            recordHistory(req, "STATUS", null, null, null, req.getStatus(), status,
                    "需求 #" + req.getReqNo() + " 状态流转: " + req.getStatus() + " ➔ " + status);
        }
        return Result.ok(targets.size(), "已成功将 " + targets.size() + " 项需求状态批量更新为【" + status + "】!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchUpdateIteration(List<String> ids, String iterationId) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        PmIteration iteration = StringUtils.isEmpty(iterationId) ? null
                : iterationMapper.selectById(iterationId);
        if (iteration == null && StringUtils.isNotEmpty(iterationId)) {
            // 支持按版本号匹配
            iteration = iterationMapper.selectOne(new LambdaQueryWrapper<PmIteration>()
                    .eq(PmIteration::getCode, iterationId).last("limit 1"));
        }
        if (iteration == null && StringUtils.isNotEmpty(iterationId)) {
            throw new MyRuntimeException("错误:目标迭代不存在!");
        }
        List<PmRequirement> targets = list(new LambdaQueryWrapper<PmRequirement>().in(PmRequirement::getId, ids));
        Date now = new Date();
        for (PmRequirement req : targets) {
            PmRequirement update = new PmRequirement();
            update.setId(req.getId());
            update.setIterationId(iteration == null ? "" : iteration.getId());
            update.setMonth(iteration == null ? "" : iteration.getCode());
            if (iteration != null && iteration.getReleaseDate() != null) {
                update.setReleaseTime(iteration.getReleaseDate());
            }
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(now);
            updateById(update);
            recordHistory(req, "BATCH", null, null, null, req.getIterationId(),
                    iteration == null ? "" : iteration.getId(),
                    "需求 #" + req.getReqNo() + " 划拨至版本【" + (iteration == null ? "未分配" : iteration.getCode()) + "】");
        }
        return Result.ok(targets.size(), "已成功将 " + targets.size() + " 项需求批量划分至版本【"
                + (iteration == null ? "未分配" : iteration.getCode()) + "】!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchUpdateReview(List<String> ids, String reviewResult, Date reviewDate, String reviewNotes) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        List<PmRequirement> targets = list(new LambdaQueryWrapper<PmRequirement>().in(PmRequirement::getId, ids));
        Date now = new Date();
        for (PmRequirement req : targets) {
            PmRequirement update = new PmRequirement();
            update.setId(req.getId());
            update.setReviewResult(reviewResult);
            if (reviewDate != null) {
                update.setReviewDate(reviewDate);
            }
            if (StringUtils.isNotEmpty(reviewNotes)) {
                update.setReviewNotes(reviewNotes);
            }
            // 评审通过后待评审需求自动进入开发中
            if (reviewResult != null && reviewResult.contains("通过") && !"未通过".equals(reviewResult)
                    && "待评审".equals(req.getStatus())) {
                update.setStatus("开发中");
                update.setDevProgress(resolveStatusProgress("开发中", req.getDevProgress(), req.getStatus()));
            }
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(now);
            updateById(update);
            recordHistory(req, "BATCH", null, null, null, req.getReviewResult(), reviewResult,
                    "需求 #" + req.getReqNo() + " 评审结论更新为【" + reviewResult + "】");
        }
        return Result.ok(targets.size(), "成功批量更新 " + targets.size() + " 项需求的评审结论!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchAssign(List<String> ids, String personnelType, String names) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        if (!List.of("frontendDev", "backendDev", "qaTester", "owner").contains(personnelType)) {
            throw new MyRuntimeException("错误:人员类型不合法，仅支持 frontendDev/backendDev/qaTester/owner!");
        }
        String formatted = PersonnelNameUtils.formatPersonnelNames(names);
        List<PmRequirement> targets = list(new LambdaQueryWrapper<PmRequirement>().in(PmRequirement::getId, ids));
        Date now = new Date();
        for (PmRequirement req : targets) {
            PmRequirement update = new PmRequirement();
            update.setId(req.getId());
            switch (personnelType) {
                case "frontendDev" -> update.setFrontendDev(formatted);
                case "backendDev" -> update.setBackendDev(formatted);
                case "qaTester" -> update.setQaTester(formatted);
                default -> update.setOwner(formatted);
            }
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(now);
            updateById(update);
            recordHistory(req, "BATCH", personnelType, currentPersonnel(req, personnelType), formatted,
                    currentPersonnel(req, personnelType), formatted,
                    "需求 #" + req.getReqNo() + " 批量指派人员【" + formatted + "】");
        }
        return Result.ok(targets.size(), "成功批量更新 " + targets.size() + " 项需求的人员分配!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchBindTestNode(List<String> ids, String nodeId) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        PmIterationTestNode node = StringUtils.isEmpty(nodeId) ? null : testNodeMapper.selectById(nodeId);
        if (node == null && StringUtils.isNotEmpty(nodeId)) {
            throw new MyRuntimeException("错误:转测节点不存在!");
        }
        List<PmRequirement> targets = list(new LambdaQueryWrapper<PmRequirement>().in(PmRequirement::getId, ids));
        Date now = new Date();
        for (PmRequirement req : targets) {
            PmRequirement update = new PmRequirement();
            update.setId(req.getId());
            if (node == null) {
                update.setTestNodeId("");
                update.setTestNodeName("");
                update.setTestNodeDate(null);
            } else {
                update.setTestNodeId(node.getId());
                update.setTestNodeName(node.getName());
                update.setTestNodeDate(node.getNodeDate());
                update.setTestPhase(node.getName());
            }
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(now);
            updateById(update);
            recordHistory(req, "BATCH", null, null, null, req.getTestNodeId(), node == null ? "" : node.getId(),
                    "需求 #" + req.getReqNo() + " 绑定转测节点【" + (node == null ? "解绑" : node.getName()) + "】");
        }
        return Result.ok(targets.size(), "已成功将 " + targets.size() + " 项需求批量绑定至【"
                + (node == null ? "解绑" : node.getName()) + "】!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmRequirement> reassign(ReqReassign reqReassign) {
        PmRequirement req = getById(reqReassign.getReqId());
        if (req == null) {
            throw new MyRuntimeException("错误:需求 " + reqReassign.getReqId() + " 不存在!");
        }
        String cleanFrom = PersonnelNameUtils.cleanPersonnelName(reqReassign.getFromPerson());
        String cleanTo = PersonnelNameUtils.cleanPersonnelName(reqReassign.getToPerson());
        // 查询目标人员在系统成员库中的岗位属性
        PmPersonnel targetMember = personnelMapper.selectOne(new LambdaQueryWrapper<PmPersonnel>()
                .eq(PmPersonnel::getName, cleanTo)
                .or()
                .eq(PmPersonnel::getDisplayName, cleanTo)
                .last("limit 1"));
        String targetRole = targetMember != null && "backend".equals(targetMember.getRole()) ? "backend" : "frontend";
        List<String> feList = new ArrayList<>(PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev()));
        List<String> beList = new ArrayList<>(PersonnelNameUtils.parsePersonnelNames(req.getBackendDev()));
        String newFe;
        String newBe;
        if (Boolean.TRUE.equals(reqReassign.getTransferAll())) {
            // 独占整单转派：将整个需求完全移交给目标人员独占，原所有其他分配人员全部清空
            if ("backend".equals(targetRole)) {
                newBe = cleanTo;
                newFe = PersonnelNameUtils.UNASSIGNED;
            } else {
                newFe = cleanTo;
                newBe = PersonnelNameUtils.UNASSIGNED;
            }
        } else {
            // 角色精细化调配：彻底从原需求的前后端名单中剔除转派人
            feList.removeIf(p -> p.equals(cleanFrom));
            beList.removeIf(p -> p.equals(cleanFrom));
            boolean wasInFe = PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev()).contains(cleanFrom);
            boolean wasInBe = PersonnelNameUtils.parsePersonnelNames(req.getBackendDev()).contains(cleanFrom);
            boolean isFrontendTask = "frontend".equals(reqReassign.getRole()) || (wasInFe && !wasInBe);
            boolean isBackendTask = "backend".equals(reqReassign.getRole()) || (wasInBe && !wasInFe);
            if (isFrontendTask) {
                feList = new ArrayList<>(List.of(cleanTo));
                beList.removeIf(p -> p.equals(cleanTo));
            } else if (isBackendTask) {
                beList = new ArrayList<>(List.of(cleanTo));
                feList.removeIf(p -> p.equals(cleanTo));
            } else if ("backend".equals(targetRole)) {
                beList = new ArrayList<>(List.of(cleanTo));
                feList.removeIf(p -> p.equals(cleanTo));
            } else {
                feList = new ArrayList<>(List.of(cleanTo));
                beList.removeIf(p -> p.equals(cleanTo));
            }
            newFe = feList.isEmpty() ? PersonnelNameUtils.UNASSIGNED : String.join(PersonnelNameUtils.SEPARATOR, feList);
            newBe = beList.isEmpty() ? PersonnelNameUtils.UNASSIGNED : String.join(PersonnelNameUtils.SEPARATOR, beList);
        }
        PmRequirement update = new PmRequirement();
        update.setId(req.getId());
        update.setFrontendDev(newFe);
        update.setBackendDev(newBe);
        update.setUpdateBy(safeGetCurrentUser());
        update.setUpdateTime(new Date());
        updateById(update);
        // 记录审计日志
        recordHistory(req, "REASSIGN",
                StringUtils.isEmpty(reqReassign.getRole()) ? targetRole : reqReassign.getRole(),
                cleanFrom, cleanTo, req.getFrontendDev() + " / " + req.getBackendDev(), newFe + " / " + newBe,
                Boolean.TRUE.equals(reqReassign.getTransferAll())
                        ? "需求 #" + req.getReqNo() + " 整单转派给【" + cleanTo + "】独占承接"
                        : "需求 #" + req.getReqNo() + " 的【" + ("backend".equals(reqReassign.getRole()) ? "后端" : "前端")
                        + "任务】从【" + cleanFrom + "】转派给【" + cleanTo + "】");
        return Result.ok(getById(req.getId()), Boolean.TRUE.equals(reqReassign.getTransferAll())
                ? "已将需求 #" + req.getReqNo() + " 整单转派给【" + cleanTo + "】独占承接（原负责人已全部清除）"
                : "已将需求 #" + req.getReqNo() + " 的【" + ("backend".equals(reqReassign.getRole()) ? "后端" : "前端")
                + "任务】从【" + cleanFrom + "】成功转派给【" + cleanTo + "】");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmRequirement> updateWorkload(ReqWorkload reqWorkload) {
        PmRequirement req = getById(reqWorkload.getReqId());
        if (req == null) {
            throw new MyRuntimeException("错误:需求 " + reqWorkload.getReqId() + " 不存在!");
        }
        BigDecimal newWorkload = reqWorkload.getWorkload() == null
                ? BigDecimal.ONE : reqWorkload.getWorkload().max(new BigDecimal("0.1")).setScale(1, RoundingMode.HALF_UP);
        LocalDate startDate = req.getDevStartDate() == null ? ScheduleDateUtils.DEFAULT_START_DATE : toLocalDate(req.getDevStartDate());
        LocalDate newEndDate = ScheduleDateUtils.calculateDevEndDate(startDate, newWorkload, currentConfig());
        PmRequirement update = new PmRequirement();
        update.setId(req.getId());
        update.setWorkload(newWorkload);
        update.setDevStartDate(toDate(startDate));
        update.setDevEndDate(toDate(newEndDate));
        update.setUpdateBy(safeGetCurrentUser());
        update.setUpdateTime(new Date());
        updateById(update);
        recordHistory(req, "WORKLOAD", null, null, null,
                req.getWorkload() == null ? null : req.getWorkload().toPlainString(), newWorkload.toPlainString(),
                "需求 #" + req.getReqNo() + " 预估工时调整为 " + newWorkload + " 人/天，日历排期同步至 " + ScheduleDateUtils.FORMATTER.format(newEndDate));
        return Result.ok(getById(req.getId()), "已将需求 #" + req.getReqNo() + " 预估工时调整为 "
                + newWorkload + " 人/天，日历排期已同步至 " + ScheduleDateUtils.FORMATTER.format(newEndDate));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> batchBalancing(List<BalancingPlanItem> plan) {
        if (plan == null || plan.isEmpty()) {
            return Result.ok(0, "调配方案为空!");
        }
        int affected = 0;
        for (BalancingPlanItem item : plan) {
            PmRequirement req = getById(item.getReqId());
            if (req == null) {
                log.warn("负载均衡调配跳过，需求不存在: {}", item.getReqId());
                continue;
            }
            String cleanFrom = PersonnelNameUtils.cleanPersonnelName(item.getFromPerson());
            String cleanTo = PersonnelNameUtils.cleanPersonnelName(item.getToPerson());
            List<String> feList = new ArrayList<>(PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev()));
            List<String> beList = new ArrayList<>(PersonnelNameUtils.parsePersonnelNames(req.getBackendDev()));
            feList.removeIf(p -> p.equals(cleanFrom));
            beList.removeIf(p -> p.equals(cleanFrom));
            if ("backend".equals(item.getRole())) {
                beList = new ArrayList<>(List.of(cleanTo));
                feList.removeIf(p -> p.equals(cleanTo));
            } else {
                feList = new ArrayList<>(List.of(cleanTo));
                beList.removeIf(p -> p.equals(cleanTo));
            }
            String newFe = feList.isEmpty() ? PersonnelNameUtils.UNASSIGNED : String.join(PersonnelNameUtils.SEPARATOR, feList);
            String newBe = beList.isEmpty() ? PersonnelNameUtils.UNASSIGNED : String.join(PersonnelNameUtils.SEPARATOR, beList);
            PmRequirement update = new PmRequirement();
            update.setId(req.getId());
            update.setFrontendDev(newFe);
            update.setBackendDev(newBe);
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(new Date());
            updateById(update);
            recordHistory(req, "BALANCING", item.getRole(), cleanFrom, cleanTo,
                    req.getFrontendDev() + " / " + req.getBackendDev(), newFe + " / " + newBe,
                    "负载均衡: 需求 #" + req.getReqNo() + "【" + (StringUtils.isEmpty(item.getReqTitle()) ? "" : item.getReqTitle())
                            + "】由【" + cleanFrom + "】调配给【" + cleanTo + "】");
            affected++;
        }
        return Result.ok(affected, "⚡ 智能负载均衡调配方案已成功执行，已自动调配 " + affected + " 项需求!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> autoSchedule(List<String> ids) {
        // 筛选待排期需求
        List<PmRequirement> targets = (ids == null || ids.isEmpty())
                ? list(new LambdaQueryWrapper<PmRequirement>().orderByAsc(PmRequirement::getReqNo))
                : list(new LambdaQueryWrapper<PmRequirement>().in(PmRequirement::getId, ids).orderByAsc(PmRequirement::getReqNo));
        if (targets.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("scheduledCount", 0);
            empty.put("updatedList", new ArrayList<>());
            return Result.ok(empty, "无待排期需求!");
        }
        // 迭代版本起始日期映射(支持按ID与版本号查找)
        Map<String, PmIteration> iterationMap = new HashMap<>();
        for (PmIteration iteration : iterationMapper.selectList(new LambdaQueryWrapper<>())) {
            iterationMap.put(iteration.getId(), iteration);
            if (StringUtils.isNotEmpty(iteration.getCode())) {
                iterationMap.put(iteration.getCode(), iteration);
            }
        }
        ScheduleConfigDTO config = currentConfig();
        LocalDate defaultStart = ScheduleDateUtils.getWorkdayStartDate(ScheduleDateUtils.DEFAULT_START_DATE, config);
        // 维护每位人员在每个版本下的下一个可用工作日游标: key = version__personName
        Map<String, LocalDate> personNextAvailableDate = new HashMap<>();
        List<PmRequirement> updatedList = new ArrayList<>();
        for (PmRequirement req : targets) {
            String versionKey = StringUtils.isEmpty(req.getIterationId())
                    ? (StringUtils.isEmpty(req.getMonth()) ? "default" : req.getMonth())
                    : req.getIterationId();
            PmIteration iteration = StringUtils.isEmpty(req.getIterationId()) ? null : iterationMap.get(req.getIterationId());
            if (iteration == null && StringUtils.isNotEmpty(req.getMonth())) {
                iteration = iterationMap.get(req.getMonth());
            }
            LocalDate versionStartDate = ScheduleDateUtils.getWorkdayStartDate(
                    iteration != null && iteration.getStartDate() != null
                            ? toLocalDate(iteration.getStartDate()) : defaultStart, config);
            // 前后端人员合并(去重)
            List<String> feDevs = PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev());
            List<String> beDevs = PersonnelNameUtils.parsePersonnelNames(req.getBackendDev());
            Set<String> allDevs = new java.util.LinkedHashSet<>(feDevs);
            allDevs.addAll(beDevs);
            // 计算当前任务的开始日期(取该组人员排期队列的最晚可用日期)
            LocalDate taskStartDate = versionStartDate;
            if (!allDevs.isEmpty()) {
                LocalDate latest = versionStartDate;
                for (String dev : allDevs) {
                    LocalDate cursor = personNextAvailableDate.getOrDefault(versionKey + "__" + dev, versionStartDate);
                    if (cursor.isAfter(latest)) {
                        latest = cursor;
                    }
                }
                taskStartDate = latest;
            } else {
                taskStartDate = personNextAvailableDate.getOrDefault(versionKey + "__UNASSIGNED", versionStartDate);
            }
            taskStartDate = ScheduleDateUtils.getWorkdayStartDate(taskStartDate, config);
            // 计算工时与结束日期
            BigDecimal workload = (req.getWorkload() != null && req.getWorkload().compareTo(BigDecimal.ZERO) > 0)
                    ? req.getWorkload() : BigDecimal.ONE;
            LocalDate taskEndDate = ScheduleDateUtils.calculateDevEndDate(taskStartDate, workload, config);
            LocalDate nextWorkdayForDevs = ScheduleDateUtils.getNextWorkday(taskEndDate, config);
            // 更新人员游标
            if (!allDevs.isEmpty()) {
                for (String dev : allDevs) {
                    personNextAvailableDate.put(versionKey + "__" + dev, nextWorkdayForDevs);
                }
            } else {
                personNextAvailableDate.put(versionKey + "__UNASSIGNED", nextWorkdayForDevs);
            }
            // 持久化新排期
            PmRequirement update = new PmRequirement();
            update.setId(req.getId());
            update.setDevStartDate(toDate(taskStartDate));
            update.setDevEndDate(toDate(taskEndDate));
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(new Date());
            updateById(update);
            PmRequirement updated = getById(req.getId());
            updatedList.add(updated);
            recordHistory(req, "SCHEDULE", null, null, null,
                    formatDate(req.getDevStartDate()) + "~" + formatDate(req.getDevEndDate()),
                    ScheduleDateUtils.FORMATTER.format(taskStartDate) + "~" + ScheduleDateUtils.FORMATTER.format(taskEndDate),
                    "自动排期: 需求 #" + req.getReqNo() + " 排期至 "
                            + ScheduleDateUtils.FORMATTER.format(taskStartDate) + " ~ " + ScheduleDateUtils.FORMATTER.format(taskEndDate));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scheduledCount", targets.size());
        result.put("updatedList", updatedList);
        return Result.ok(result, "⚡ 已根据版本起始日期，为每位开发人员按工时顺序顺延自动排期 " + targets.size() + " 项任务!");
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 需求信息纠偏：工时基准、人员字段标准化、日历排期推导
     * 严防1人天任务在甘特图上跨度达十几天、以及后续任务挤压等非理性排期
     */
    private void sanitizeRequirement(PmRequirement requirement, PmRequirement exist) {
        // 工时基准(默认1人天)
        BigDecimal workload = requirement.getWorkload() != null ? requirement.getWorkload()
                : (exist != null ? exist.getWorkload() : null);
        if (workload == null || workload.compareTo(BigDecimal.ZERO) <= 0) {
            workload = BigDecimal.ONE;
        }
        workload = workload.setScale(1, RoundingMode.HALF_UP);
        requirement.setWorkload(workload);
        // 开始日期(默认基准开始日期)
        LocalDate startDate = requirement.getDevStartDate() != null ? toLocalDate(requirement.getDevStartDate())
                : (exist != null && exist.getDevStartDate() != null ? toLocalDate(exist.getDevStartDate())
                : ScheduleDateUtils.DEFAULT_START_DATE);
        // 截止日期推导：未指定、或实际跨度异常远大于合理工时时必须校正
        LocalDate endDate = requirement.getDevEndDate() != null ? toLocalDate(requirement.getDevEndDate())
                : (exist != null && exist.getDevEndDate() != null ? toLocalDate(exist.getDevEndDate()) : null);
        if (endDate == null) {
            endDate = ScheduleDateUtils.calculateDevEndDate(startDate, workload, currentConfig());
        } else {
            long rawSpanDays = ScheduleDateUtils.calculateDays(startDate, endDate);
            long reasonableSpan = Math.max(4, Math.round(workload.doubleValue() * 2.5 + 2));
            if (rawSpanDays > reasonableSpan) {
                endDate = ScheduleDateUtils.calculateDevEndDate(startDate, workload, currentConfig());
            }
        }
        requirement.setDevStartDate(toDate(startDate));
        requirement.setDevEndDate(toDate(endDate));
        // 人员字段标准化
        requirement.setFrontendDev(defaultPersonnel(requirement.getFrontendDev(), exist == null ? null : exist.getFrontendDev(), PersonnelNameUtils.UNASSIGNED));
        requirement.setBackendDev(defaultPersonnel(requirement.getBackendDev(), exist == null ? null : exist.getBackendDev(), PersonnelNameUtils.UNASSIGNED));
        requirement.setQaTester(defaultPersonnel(requirement.getQaTester(), exist == null ? null : exist.getQaTester(), PersonnelNameUtils.UNASSIGNED));
        requirement.setOwner(defaultPersonnel(requirement.getOwner(), exist == null ? null : exist.getOwner(), "PMO"));
        requirement.setRequester(defaultPersonnel(requirement.getRequester(), exist == null ? null : exist.getRequester(), "产品团队"));
    }

    /**
     * 人员字段默认值处理：新值为空时取旧值，仍为空时取默认值，并格式化为标准展示串
     */
    private String defaultPersonnel(String newValue, String oldValue, String defaultVal) {
        String target = StringUtils.isNotEmpty(newValue) ? newValue : oldValue;
        String formatted = PersonnelNameUtils.formatPersonnelNames(target);
        return StringUtils.isEmpty(formatted) ? defaultVal : formatted;
    }

    /**
     * 归属迭代信息联动：设置月份代码与研发确认发版时间
     */
    private void syncIterationInfo(PmRequirement requirement, PmRequirement exist) {
        String iterationId = StringUtils.isNotEmpty(requirement.getIterationId())
                ? requirement.getIterationId()
                : (exist != null ? exist.getIterationId() : null);
        String month = StringUtils.isNotEmpty(requirement.getMonth())
                ? requirement.getMonth()
                : (exist != null ? exist.getMonth() : null);
        if (StringUtils.isEmpty(iterationId)) {
            return;
        }
        PmIteration iteration = iterationMapper.selectById(iterationId);
        if (iteration == null) {
            iteration = iterationMapper.selectOne(new LambdaQueryWrapper<PmIteration>()
                    .eq(PmIteration::getCode, iterationId)
                    .or()
                    .eq(PmIteration::getCode, month)
                    .last("limit 1"));
        }
        if (iteration == null) {
            return;
        }
        requirement.setIterationId(iteration.getId());
        requirement.setMonth(iteration.getCode());
        if (iteration.getReleaseDate() != null && requirement.getReleaseTime() == null) {
            requirement.setReleaseTime(iteration.getReleaseDate());
        }
    }

    /**
     * 状态流转联动开发进度
     */
    private Integer resolveStatusProgress(String status, Integer currentProgress, String oldStatus) {
        int progress = currentProgress == null ? 0 : currentProgress;
        if (POST_DEV_STATUSES.contains(status)) {
            return 100;
        }
        if ("待评审".equals(status)) {
            return 0;
        }
        if ("开发中".equals(status)) {
            if (progress == 0) {
                return 30;
            }
            // 从测试等状态回退至开发中，进度回退至90%继续开发
            if (POST_DEV_STATUSES.contains(oldStatus) && progress >= 100) {
                return 90;
            }
        }
        return progress;
    }

    /**
     * 创建需求时的默认进度
     */
    private Integer resolveDefaultProgress(String status) {
        if (POST_DEV_STATUSES.contains(status)) {
            return 100;
        }
        if ("开发中".equals(status)) {
            return 30;
        }
        return 0;
    }

    /**
     * 生成下一个递增序号 req_no
     */
    private Integer nextReqNo() {
        PmRequirement max = getOne(new LambdaQueryWrapper<PmRequirement>()
                .orderByDesc(PmRequirement::getReqNo)
                .last("limit 1"));
        return max == null || max.getReqNo() == null ? 1 : max.getReqNo() + 1;
    }

    /**
     * 记录需求审计日志
     */
    private void recordHistory(PmRequirement req, String actionType, String role, String fromPerson, String toPerson,
                               String oldValue, String newValue, String detail) {
        try {
            PmRequirementHistory history = new PmRequirementHistory();
            history.setReqId(req.getId());
            history.setReqNo(req.getReqNo());
            history.setActionType(actionType);
            history.setRole(role);
            history.setFromPerson(fromPerson);
            history.setToPerson(toPerson);
            history.setOldValue(oldValue);
            history.setNewValue(newValue);
            history.setDetail(detail);
            history.setCreateBy(safeGetCurrentUser());
            history.setCreateTime(new Date());
            historyMapper.insert(history);
        } catch (Exception e) {
            // 审计日志失败不影响主流程
            log.error("记录需求审计日志失败: {}", e.getMessage());
        }
    }

    /**
     * 获取需求上对应人员类型的当前值
     */
    private String currentPersonnel(PmRequirement req, String personnelType) {
        return switch (personnelType) {
            case "frontendDev" -> req.getFrontendDev();
            case "backendDev" -> req.getBackendDev();
            case "qaTester" -> req.getQaTester();
            default -> req.getOwner();
        };
    }

    /**
     * 校验需求必填信息
     */
    private void verifyRequirement(PmRequirement requirement) {
        if (StringUtils.isEmpty(requirement.getModule())) {
            throw new MyRuntimeException("错误:需求所属模块不能为空!");
        }
        if (StringUtils.isEmpty(requirement.getLevel1Feature())) {
            throw new MyRuntimeException("错误:一级功能点不能为空!");
        }
    }

    /**
     * 获取当前排期配置
     */
    private ScheduleConfigDTO currentConfig() {
        return scheduleConfigService.getConfigData();
    }

    /**
     * Date 转 LocalDate(兼容 java.sql.Date)
     */
    private LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * LocalDate 转 Date
     */
    private Date toDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    /**
     * 格式化日期为 yyyy-MM-dd
     */
    private String formatDate(Date date) {
        return date == null ? "" : ScheduleDateUtils.FORMATTER.format(toLocalDate(date));
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

package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.dict.PmDictCodes;
import cn.com.mfish.project.dict.PmDictService;
import cn.com.mfish.project.dto.ScheduleConfigDTO;
import cn.com.mfish.project.entity.PmDevSchedule;
import cn.com.mfish.project.entity.PmIteration;
import cn.com.mfish.project.entity.PmIterationTestNode;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.entity.PmRequirementHistory;
import cn.com.mfish.project.mapper.PmDevScheduleMapper;
import cn.com.mfish.project.mapper.PmIterationMapper;
import cn.com.mfish.project.mapper.PmIterationTestNodeMapper;
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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 核心需求
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Service
public class PmRequirementServiceImpl extends ServiceImpl<PmRequirementMapper, PmRequirement> implements PmRequirementService {
    /** 归档标记: 版本状态为"已发布"即视为已归档(不新增字段, 复用 status) */
    private static final String ARCHIVED_STATUS = "已发布";

    @Resource
    private PmIterationMapper iterationMapper;
    @Resource
    private PmIterationTestNodeMapper testNodeMapper;
    @Resource
    private PmRequirementHistoryMapper historyMapper;
    @Resource
    private PmScheduleConfigService scheduleConfigService;
    @Resource
    private PmDevScheduleMapper devScheduleMapper;
    @Resource
    private PmDictService pmDictService;

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
     * 版本筛选语义(前端传勾选的版本 iterationIds, 查询逻辑全部在后端):
     * - 纯汇总(iterationIds/codes 为空且 includeUnassigned 非 true): 排除已归档(状态=已发布)版本的需求,
     *   避免全部版本汇总拉回大量已归档数据;
     * - 勾选版本(单选/多选): 优先按 iterationIds → iteration_id IN(ids) 精确匹配,
     *   不做前缀模糊匹配(勾选 v1.3.7 不会带出 v1.3.7.3), 已归档版本同样可查;
     *   同版本号多版本并存时按主键区分不串台; 老数据无 iteration_id 时按 month IN(选中版本代号) 回退;
     * - codes(旧参数兼容): 版本代号 → 迭代ID, iteration_id IN(ids) OR month IN(codes);
     * - includeUnassigned: 追加"未纳入版本"(无迭代归属且月份为空)的条件, 可与版本条件叠加
     */
    private List<PmRequirement> queryList(ReqPmRequirement req) {
        if (req == null) {
            // 无条件全量汇总: 排除已归档(状态=已发布)版本的需求
            return list(buildSummaryWrapper());
        }
        String month = StringUtils.isEmpty(req.getMonth()) || "ALL".equals(req.getMonth()) ? null : req.getMonth();
        List<String> codes = req.getCodes() == null ? List.of()
                : req.getCodes().stream().filter(StringUtils::isNotEmpty).map(String::trim).distinct().toList();
        // 勾选的版本ID列表(优先于 codes): 同版本号多版本并存时按主键精确区分, 避免串台
        List<String> iterationIds = req.getIterationIds() == null ? List.of()
                : req.getIterationIds().stream().filter(StringUtils::isNotEmpty).map(String::trim).distinct().toList();
        boolean includeUnassigned = Boolean.TRUE.equals(req.getIncludeUnassigned());
        boolean isSummary = month == null && StringUtils.isEmpty(req.getIterationId())
                && codes.isEmpty() && iterationIds.isEmpty() && !includeUnassigned;
        LambdaQueryWrapper<PmRequirement> wrapper = new LambdaQueryWrapper<PmRequirement>()
                // selectGridList 为自定义 @Select SQL, @TableLogic 不会自动注入 del_flag 过滤, 需手动补上
                .eq(PmRequirement::getDelFlag, 0)
                // 旧参数 month 兼容(月份代码或迭代ID匹配)
                .and(month != null && !"UNASSIGNED".equals(month), w -> w
                        .eq(PmRequirement::getMonth, month)
                        .or()
                        .eq(PmRequirement::getIterationId, month))
                .eq(StringUtils.isNotEmpty(req.getIterationId()), PmRequirement::getIterationId, req.getIterationId())
                // 勾选版本精确查询(优先按版本ID):
                // - iterationIds: iteration_id IN(版本ID) 精确命中, 同版本号多版本不串台;
                //   老数据无 iteration_id 时按 month IN(选中版本的代号) 回退(代号由版本ID反查, 仅回退选中版本)
                // - codes(旧参数兼容): 版本代号 → 迭代ID(仅未删除迭代)
                // 注意: MP 嵌套 consumer 内 or() 无参会被丢弃(实测 3.5.17), 组间 OR 必须用 or(consumer),
                // 且 consumer 内每个子组用 nested() 显式包括号, 防止 AND/OR 优先级导致语义漂移
                .and(!codes.isEmpty() || !iterationIds.isEmpty() || includeUnassigned, w -> {
                    if (!iterationIds.isEmpty()) {
                        // 版本ID → 代号(仅未删除迭代), 供无 iteration_id 的老数据回退匹配
                        List<String> iterCodes = iterationMapper.selectList(new LambdaQueryWrapper<PmIteration>()
                                .select(PmIteration::getId, PmIteration::getCode)
                                .in(PmIteration::getId, iterationIds)
                                .eq(PmIteration::getDelFlag, 0)).stream()
                                .map(PmIteration::getCode)
                                .filter(StringUtils::isNotEmpty)
                                .toList();
                        // 现存版本ID集合: 识别孤儿 iteration_id(引用了已删除/从未存在的版本, 如旧版前端临时 id 落库)
                        List<String> existingIds = iterationMapper.selectList(new LambdaQueryWrapper<PmIteration>()
                                .select(PmIteration::getId)
                                .eq(PmIteration::getDelFlag, 0)).stream()
                                .map(PmIteration::getId)
                                .filter(StringUtils::isNotEmpty)
                                .toList();
                        w.and(x -> {
                            x.in(PmRequirement::getIterationId, iterationIds);
                            // 老数据/孤儿数据回退: 无迭代归属或其指向的版本已不存在, 且月份代号命中选中版本
                            // (仅当能反查到代号时才追加, 避免空 in)
                            if (!iterCodes.isEmpty()) {
                                x.or(y -> y
                                        .nested(z -> z.isNull(PmRequirement::getIterationId).or().eq(PmRequirement::getIterationId, "")
                                                .or().notIn(PmRequirement::getIterationId, existingIds.isEmpty() ? List.of("__nonexistent__") : existingIds))
                                        .in(PmRequirement::getMonth, iterCodes));
                            }
                        });
                    }
                    if (!codes.isEmpty()) {
                        // 版本代号 → 迭代ID(仅未删除迭代)
                        List<String> iterIds = iterationMapper.selectList(new LambdaQueryWrapper<PmIteration>()
                                .select(PmIteration::getId)
                                .in(PmIteration::getCode, codes)
                                .eq(PmIteration::getDelFlag, 0)).stream()
                                .map(PmIteration::getId)
                                .filter(StringUtils::isNotEmpty)
                                .toList();
                        w.and(x -> {
                            if (!iterIds.isEmpty()) {
                                x.in(PmRequirement::getIterationId, iterIds);
                            }
                            x.or().in(PmRequirement::getMonth, codes);
                        });
                    }
                    if (includeUnassigned) {
                        // 未纳入版本: (无迭代归属) AND (月份为空/占位), 与版本条件之间是 OR
                        w.or(x -> x
                                .nested(y -> y.isNull(PmRequirement::getIterationId).or().eq(PmRequirement::getIterationId, ""))
                                .nested(y -> y.isNull(PmRequirement::getMonth).or().eq(PmRequirement::getMonth, "")
                                        .or().eq(PmRequirement::getMonth, "UNASSIGNED").or().eq(PmRequirement::getMonth, "未纳入版本")));
                    }
                })
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
        if (isSummary) {
            excludeArchivedIterations(wrapper);
        }
        // 使用网格专用查询：刻意不包含 prd_doc_content 大文本，并派生 has_prd_doc 标记
        return baseMapper.selectGridList(wrapper);
    }

    /**
     * 按版本筛选条件解析需求ID列表(复用 queryList 的版本过滤语义, 供排期明细等下游聚合查询复用)
     * 仅透传版本筛选字段, 其余状态/人员/关键词过滤一律留空, 故结果即"当前版本视图下的全部需求ID"
     *
     * @param iterationIds      勾选的版本ID列表(为空/null 表示纯汇总/ALL)
     * @param includeUnassigned 是否叠加"未纳入版本"
     * @return 命中的需求ID列表
     */
    @Override
    public List<String> listReqIdsBySelection(List<String> iterationIds, Boolean includeUnassigned) {
        ReqPmRequirement req = new ReqPmRequirement();
        if (iterationIds != null) {
            req.setIterationIds(iterationIds.stream()
                    .filter(StringUtils::isNotEmpty)
                    .map(String::trim)
                    .distinct()
                    .toList());
        }
        req.setIncludeUnassigned(Boolean.TRUE.equals(includeUnassigned));
        return queryList(req).stream().map(PmRequirement::getId).toList();
    }

    /**
     * 无条件全量汇总查询条件(del_flag + 排除已归档版本 + 排序)
     */
    private LambdaQueryWrapper<PmRequirement> buildSummaryWrapper() {
        return excludeArchivedIterations(new LambdaQueryWrapper<PmRequirement>()
                .eq(PmRequirement::getDelFlag, 0)
                .orderByAsc(PmRequirement::getReqNo));
    }

    /**
     * 在查询条件上追加"排除已归档(状态=已发布)版本的需求"
     * 无归档版本时不做处理, 避免生成空 not in 条件
     */
    private LambdaQueryWrapper<PmRequirement> excludeArchivedIterations(LambdaQueryWrapper<PmRequirement> wrapper) {
        List<String> archivedIds = iterationMapper.selectList(new LambdaQueryWrapper<PmIteration>()
                .select(PmIteration::getId)
                .eq(PmIteration::getStatus, ARCHIVED_STATUS)
                .eq(PmIteration::getDelFlag, 0)).stream()
                .map(PmIteration::getId)
                .filter(StringUtils::isNotEmpty)
                .toList();
        if (!archivedIds.isEmpty()) {
            wrapper.notIn(PmRequirement::getIterationId, archivedIds);
        }
        return wrapper;
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
        requirement.setTenantId(resolveTenantId(requirement));
        requirement.setCreateBy(safeGetCurrentUser());
        requirement.setCreateTime(now);
        // 未传序号时自动生成递增 req_no
        if (requirement.getReqNo() == null) {
            requirement.setReqNo(nextReqNo());
        }
        // 归属迭代信息联动(月份代码与发版时间)
        syncIterationInfo(requirement, requirement);
        // 状态与进度默认值(默认状态取字典中第一个开发前阶段状态, 如"待评审")
        if (StringUtils.isEmpty(requirement.getStatus())) {
            requirement.setStatus(pmDictService.defaultStatus());
        }
        if (requirement.getDevProgress() == null) {
            requirement.setDevProgress(resolveDefaultProgress(requirement.getStatus()));
        }
        // 人员、工时与日历排期纠偏
        sanitizeRequirement(requirement, null);
        if (save(requirement)) {
            // 生成排期明细(需求×人员×岗位)
            persistScheduleRowsForReq(requirement);
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
        // 租户ID 由库内已有值兜底, 前端不传, 防止误清空(依赖 MP NOT_NULL 策略 + 此处显式赋值双保险)
        requirement.setTenantId(exist.getTenantId());
        // 归属迭代信息联动 (前端传空串=显式清空版本，同步清空月份/发版时间/转测节点)
        boolean versionCleared = syncIterationInfo(requirement, exist);
        // 状态流转联动开发进度
        String newStatus = StringUtils.isEmpty(requirement.getStatus()) ? exist.getStatus() : requirement.getStatus();
        Integer newProgress = requirement.getDevProgress() != null ? requirement.getDevProgress() : exist.getDevProgress();
        requirement.setDevProgress(resolveStatusProgress(newStatus, newProgress, exist.getStatus()));
        // 修改工时或开始时间时联动重算截止时间
        sanitizeRequirement(requirement, exist);
        boolean success;
        if (versionCleared) {
            // 清空版本时转测时间/发版时间为日期字段，updateById 默认 NOT_NULL 策略会忽略 null，
            // 须通过 UpdateWrapper 显式 SET NULL 落库
            LambdaUpdateWrapper<PmRequirement> clearWrapper = new LambdaUpdateWrapper<PmRequirement>()
                    .eq(PmRequirement::getId, requirement.getId())
                    .set(PmRequirement::getTestNodeDate, null)
                    .set(PmRequirement::getReleaseTime, null);
            success = update(requirement, clearWrapper);
        } else {
            success = updateById(requirement);
        }
        if (success) {
            recordHistory(requirement, "UPDATE", null, null, null, exist.getStatus(), newStatus, "更新需求 #" + exist.getReqNo());
            // 工时/人员/开始日变化后重建排期明细(钉住行保持不动)
            rebuildScheduleForReq(requirement.getId());
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
            deleteScheduleRows(List.of(id));
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
            deleteScheduleRows(ids);
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
        // prd_doc_content 标记了 select=false，需在此显式查询该字段；其余仅取文档相关标识字段
        PmRequirement requirement = lambdaQuery()
                .select(PmRequirement::getId, PmRequirement::getReqNo, PmRequirement::getModule,
                        PmRequirement::getLevel1Feature, PmRequirement::getLevel2Feature,
                        PmRequirement::getPrdDocContent, PmRequirement::getDocLastModified,
                        PmRequirement::getDocAuthor)
                .eq(PmRequirement::getId, id)
                .one();
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
    public Result<PageResult<PmRequirement>> queryDocBatch(List<String> ids, Integer pageNum, Integer pageSize) {
        if (ids == null || ids.isEmpty()) {
            return Result.ok(new PageResult<>(new ArrayList<>(), 1, 10, 0L), "无数据");
        }
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 50 : pageSize;
        long total = ids.size();
        int from = (num - 1) * size;
        if (from >= total) {
            return Result.ok(new PageResult<>(new ArrayList<>(), num, size, total), "无数据");
        }
        // 在传入 ID 列表内按页截取，再回库查询该页需求的详细文档内容，避免一次性传输超大文本
        List<String> pageIds = ids.subList(from, (int) Math.min((long) from + size, total));
        List<PmRequirement> list = lambdaQuery()
                .select(PmRequirement::getId, PmRequirement::getReqNo, PmRequirement::getModule,
                        PmRequirement::getLevel1Feature, PmRequirement::getPrdDocContent,
                        PmRequirement::getDocLastModified, PmRequirement::getDocAuthor)
                .in(PmRequirement::getId, pageIds)
                .eq(PmRequirement::getDelFlag, 0)
                .list();
        return Result.ok(new PageResult<>(list, num, size, total), "查询成功");
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
        if (!pmDictService.isValidStatus(status)) {
            throw new MyRuntimeException("错误:目标状态【" + status + "】不在字典 pm_requirement_status 中, 请先在字典管理中维护!");
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
        // 版本归属变化: 同步排期明细上的 iteration_id(钉住行保持不动)
        for (PmRequirement req : targets) {
            rebuildScheduleForReq(req.getId());
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
        // 评审结论取值必须来自字典 pm_review_result(前后端统一字典管理, 禁止硬编码字符串)
        if (StringUtils.isNotEmpty(reviewResult) && !pmDictService.isValidReviewResult(reviewResult)) {
            throw new MyRuntimeException("错误:评审结论【" + reviewResult + "】不在字典 pm_review_result 中, 请先在字典管理中维护!");
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
            // 评审结论驱动状态流转: 字典中该结论配置了 flow 目标(如"评审通过 → 开发中"),
            // 且需求尚处开发前阶段(字典 phase=pre)时自动流转;
            // 已进入研发/测试/上线, 或已取消的需求保持原状, 不被回退
            String flowTarget = pmDictService.reviewFlowTarget(reviewResult);
            if (StringUtils.isNotEmpty(flowTarget) && pmDictService.isPreDevStatus(req.getStatus())) {
                update.setStatus(flowTarget);
                update.setDevProgress(resolveStatusProgress(flowTarget, req.getDevProgress(), req.getStatus()));
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
            // 汇总本轮生效后的拆分工时与人员, 用于总工时(显示口径)重算
            BigDecimal newFeWl = req.getFrontendWorkload();
            BigDecimal newBeWl = req.getBackendWorkload();
            String newFeDev = req.getFrontendDev();
            String newBeDev = req.getBackendDev();
            switch (personnelType) {
                case "frontendDev" -> {
                    update.setFrontendDev(formatted);
                    newFeDev = formatted;
                }
                case "backendDev" -> {
                    update.setBackendDev(formatted);
                    newBeDev = formatted;
                }
                case "qaTester" -> update.setQaTester(formatted);
                default -> update.setOwner(formatted);
            }
            // 前后端人员变动: 按"生效人员"统一纠偏两侧工时(与 sanitizeRequirement 完全同口径)
            // 无有效人员(空/不涉及) → 0 人天; 有人员但工时为 null 或 0(历史"不涉及"残留) → 默认 1 人天
            // 注意: 必须两侧都纠, 否则未被指派的那一侧残留的旧值会继续污染总工时
            if ("frontendDev".equals(personnelType) || "backendDev".equals(personnelType)) {
                if (PersonnelNameUtils.parsePersonnelNames(newFeDev).isEmpty()) {
                    newFeWl = BigDecimal.ZERO;
                } else if (newFeWl == null || newFeWl.compareTo(BigDecimal.ZERO) == 0) {
                    newFeWl = BigDecimal.ONE;
                }
                if (PersonnelNameUtils.parsePersonnelNames(newBeDev).isEmpty()) {
                    newBeWl = BigDecimal.ZERO;
                } else if (newBeWl == null || newBeWl.compareTo(BigDecimal.ZERO) == 0) {
                    newBeWl = BigDecimal.ONE;
                }
                update.setFrontendWorkload(newFeWl);
                update.setBackendWorkload(newBeWl);
                // 总工时(仅显示口径)随拆分值实时重算: 防止批量置"不涉及/清空"后总工时残留旧值(如仍显示 2)
                update.setWorkload(headcountTotalWorkload(newFeWl, newBeWl, newFeDev, newBeDev));
            }
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(now);
            updateById(update);
            recordHistory(req, "BATCH", personnelType, currentPersonnel(req, personnelType), formatted,
                    currentPersonnel(req, personnelType), formatted,
                    "需求 #" + req.getReqNo() + " 批量指派人员【" + formatted + "】");
        }
        // 前后端人员变动: 重建受影响需求的排期明细(钉住行保持不动)
        if ("frontendDev".equals(personnelType) || "backendDev".equals(personnelType)) {
            for (PmRequirement req : targets) {
                rebuildScheduleForReq(req.getId());
            }
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
        List<String> feList = new ArrayList<>(PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev()));
        List<String> beList = new ArrayList<>(PersonnelNameUtils.parsePersonnelNames(req.getBackendDev()));
        // 目标人员岗位判定：优先采用前端显式传入的角色(由统一人员缓存按组织/角色名称派生)，
        // 其次按目标人员在原需求前后端名单中的位置推断，最后默认前端
        // 注意：历史 pm_personnel 人员表已废弃，人员统一来自 /oauth2/ssoTenant/user
        String targetRole;
        if ("backend".equals(reqReassign.getRole()) || "frontend".equals(reqReassign.getRole())) {
            targetRole = reqReassign.getRole();
        } else {
            boolean toInBe = beList.contains(cleanTo);
            boolean toInFe = feList.contains(cleanTo);
            targetRole = toInBe && !toInFe ? "backend" : "frontend";
        }
        String newFe;
        String newBe;
        if (Boolean.TRUE.equals(reqReassign.getTransferAll())) {
            // 独占整单转派：将整个需求完全移交给目标人员独占，另一端无需开发置"不涉及"，原所有其他分配人员全部清空
            if ("backend".equals(targetRole)) {
                newBe = cleanTo;
                newFe = PersonnelNameUtils.NOT_INVOLVED;
            } else {
                newFe = cleanTo;
                newBe = PersonnelNameUtils.NOT_INVOLVED;
            }
        } else {
            // 角色精细化调配：仅将"转派人"替换为"目标人"，同侧其他成员全部保留
            // (例: 前端 A、B，A 转派给 C → 前端变为 C、B；绝非清空整侧只留 C)
            feList.removeIf(p -> p.equals(cleanFrom));
            beList.removeIf(p -> p.equals(cleanFrom));
            feList.removeIf(p -> p.equals(cleanTo));
            beList.removeIf(p -> p.equals(cleanTo));
            boolean wasInFe = PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev()).contains(cleanFrom);
            boolean wasInBe = PersonnelNameUtils.parsePersonnelNames(req.getBackendDev()).contains(cleanFrom);
            boolean isFrontendTask = "frontend".equals(reqReassign.getRole()) || (wasInFe && !wasInBe);
            boolean isBackendTask = "backend".equals(reqReassign.getRole()) || (wasInBe && !wasInFe);
            if (isBackendTask || (!isFrontendTask && "backend".equals(targetRole))) {
                beList.add(cleanTo);
            } else {
                feList.add(cleanTo);
            }
            // 名单为空则落空串(空=待分配)，不再落"待分配"占位词
            newFe = feList.isEmpty() ? "" : String.join(PersonnelNameUtils.SEPARATOR, feList);
            newBe = beList.isEmpty() ? "" : String.join(PersonnelNameUtils.SEPARATOR, beList);
        }
        PmRequirement update = new PmRequirement();
        update.setId(req.getId());
        update.setFrontendDev(newFe);
        update.setBackendDev(newBe);
        update.setUpdateBy(safeGetCurrentUser());
        update.setUpdateTime(new Date());
        updateById(update);
        // 人员变动: 重建排期明细(钉住行保持不动)
        rebuildScheduleForReq(req.getId());
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
        String devType = reqWorkload.getDevType();
        boolean frontend = "frontend".equalsIgnoreCase(devType);
        boolean backend = "backend".equalsIgnoreCase(devType);
        BigDecimal curFe = req.getFrontendWorkload();
        BigDecimal curBe = req.getBackendWorkload();
        BigDecimal curTotal = req.getWorkload() == null ? BigDecimal.ZERO : req.getWorkload();
        BigDecimal newFe = curFe;
        BigDecimal newBe = curBe;
        BigDecimal newTotal = reqWorkload.getWorkload();

        if (frontend || backend) {
            // 按当前人员的岗位(前端/后端)写入对应工时段; 另一侧严格保持原值不臆造
            // (历史数据无拆分时另一侧为NULL, 用差额推断会把它错误物化成0, 如总工时2.0调前端2.0→后端被清0)
            BigDecimal normalized = normalizeWorkload(newTotal);
            if (normalized == null) {
                normalized = BigDecimal.ONE;
            }
            if (frontend) {
                newFe = normalized;
            } else {
                newBe = normalized;
            }
        } else {
            if (reqWorkload.getFrontendWorkload() != null) {
                newFe = normalizeWorkload(reqWorkload.getFrontendWorkload());
            }
            if (reqWorkload.getBackendWorkload() != null) {
                newBe = normalizeWorkload(reqWorkload.getBackendWorkload());
            }
        }

        // 总工时: 存在任一拆分值时按人数加权(各角色工时×人数, 仅显示口径)；否则沿用传入的总工时
        if (newFe != null || newBe != null) {
            newTotal = headcountTotalWorkload(newFe, newBe, req.getFrontendDev(), req.getBackendDev());
            if (newTotal.compareTo(new BigDecimal("0.1")) < 0) {
                newTotal = new BigDecimal("0.1");
            }
        } else {
            newTotal = normalizeWorkload(newTotal);
            if (newTotal == null) {
                newTotal = BigDecimal.ONE;
            }
        }

        LocalDate startDate = req.getDevStartDate() == null ? ScheduleDateUtils.DEFAULT_START_DATE : toLocalDate(req.getDevStartDate());
        // 排期时长: 前后端并行推进，取两岗位单人工时的较大者(与人数/总工时无关)
        BigDecimal feDur = positiveWorkload(newFe);
        BigDecimal beDur = positiveWorkload(newBe);
        BigDecimal scheduleWorkload = (feDur != null || beDur != null)
                ? (feDur == null ? BigDecimal.ZERO : feDur).max(beDur == null ? BigDecimal.ZERO : beDur)
                : newTotal;
        LocalDate newEndDate = ScheduleDateUtils.calculateDevEndDate(startDate, scheduleWorkload, currentConfig());
        PmRequirement update = new PmRequirement();
        update.setId(req.getId());
        update.setWorkload(newTotal);
        update.setFrontendWorkload(newFe);
        update.setBackendWorkload(newBe);
        update.setDevStartDate(toDate(startDate));
        update.setDevEndDate(toDate(newEndDate));
        update.setUpdateBy(safeGetCurrentUser());
        update.setUpdateTime(new Date());
        updateById(update);
        // 工时/开始日变化: 重建排期明细(钉住行保持不动)
        rebuildScheduleForReq(req.getId());
        String scopeText = frontend ? "前端" : (backend ? "后端" : "");
        recordHistory(req, "WORKLOAD", null, null, null,
                req.getWorkload() == null ? null : req.getWorkload().toPlainString(), newTotal.toPlainString(),
                "需求 #" + req.getReqNo() + " " + scopeText + "预估工时调整为 " + newTotal + " 人/天，日历排期同步至 " + ScheduleDateUtils.FORMATTER.format(newEndDate));
        return Result.ok(getById(req.getId()), "已将需求 #" + req.getReqNo() + scopeText + "预估工时调整为 "
                + newTotal + " 人/天，日历排期已同步至 " + ScheduleDateUtils.FORMATTER.format(newEndDate));
    }

    /**
     * 工时应力量化: 最小0.1人天, 保留1位小数
     */
    private BigDecimal normalizeWorkload(BigDecimal workload) {
        if (workload == null) {
            return null;
        }
        return workload.max(new BigDecimal("0.1")).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 有效工时段(正数)才参与拆分装箱；0/负数/缺失返回 null
     */
    private BigDecimal positiveWorkload(BigDecimal workload) {
        if (workload == null || workload.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return workload;
    }

    /**
     * 人员字段中的实际参与人数(剔除 待分配/不涉及 等占位名)；无有效人员时按 1 人计
     */
    private int personnelCount(String personnelField) {
        List<String> names = PersonnelNameUtils.parsePersonnelNames(personnelField);
        return names.isEmpty() ? 1 : names.size();
    }

    /**
     * 总工时(仅显示口径) = 前端单人工时 × 前端人数 + 后端单人工时 × 后端人数。
     * 多人并行承接同一岗位时不延长工期，总工时只用于展示，不参与排期运算。
     */
    private BigDecimal headcountTotalWorkload(BigDecimal feWorkload, BigDecimal beWorkload, String feDev, String beDev) {
        BigDecimal fe = feWorkload == null ? BigDecimal.ZERO : feWorkload.max(BigDecimal.ZERO);
        BigDecimal be = beWorkload == null ? BigDecimal.ZERO : beWorkload.max(BigDecimal.ZERO);
        return fe.multiply(BigDecimal.valueOf(personnelCount(feDev)))
                .add(be.multiply(BigDecimal.valueOf(personnelCount(beDev))))
                .setScale(1, RoundingMode.HALF_UP);
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
            // 名单为空则落空串(空=待分配)，不再落"待分配"占位词
            String newFe = feList.isEmpty() ? "" : String.join(PersonnelNameUtils.SEPARATOR, feList);
            String newBe = beList.isEmpty() ? "" : String.join(PersonnelNameUtils.SEPARATOR, beList);
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
        // 人员变动: 重建受影响需求的排期明细(钉住行保持不动)
        for (BalancingPlanItem item : plan) {
            rebuildScheduleForReq(item.getReqId());
        }
        return Result.ok(affected, "⚡ 智能负载均衡调配方案已成功执行，已自动调配 " + affected + " 项需求!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> autoSchedule(List<String> iterationIds, boolean includeUnassigned) {
        // 迭代版本起始日期映射(支持按ID与版本号查找): 版本解析与归档判定均依赖, 需先构建
        Map<String, PmIteration> iterationMap = new HashMap<>();
        for (PmIteration iteration : iterationMapper.selectList(new LambdaQueryWrapper<>())) {
            iterationMap.put(iteration.getId(), iteration);
            if (StringUtils.isNotEmpty(iteration.getCode())) {
                iterationMap.put(iteration.getCode(), iteration);
            }
        }
        // 排期范围两种口径:
        // 1) 未显式传版本(顶部未选版本, 或只选了非归档版本): 未归档版本的需求统一排期(不按选中版本单独排), 已归档版本不参与;
        //    该口径下无归属版本的需求(iteration==null)本就包含在内
        // 2) 显式传版本(顶部选中了归档版本): 只排这些版本的需求(含归档版本, 用于针对性重排历史版本);
        //    无归属版本的需求无ID可传, 由 includeUnassigned 特殊标识声明是否合并排期
        Set<String> scopeVersionIds = iterationIds == null ? new HashSet<>()
                : iterationIds.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toSet());
        boolean scoped = !scopeVersionIds.isEmpty();
        List<PmRequirement> targets = list(new LambdaQueryWrapper<PmRequirement>().orderByAsc(PmRequirement::getReqNo))
                .stream()
                .filter(req -> {
                    PmIteration iteration = resolveIterationForReq(req, iterationMap);
                    if (scoped) {
                        return iteration == null ? includeUnassigned : scopeVersionIds.contains(iteration.getId());
                    }
                    return iteration == null || !ARCHIVED_STATUS.equals(iteration.getStatus());
                })
                .collect(Collectors.toList());
        if (targets.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("scheduledCount", 0);
            empty.put("updatedList", new ArrayList<>());
            empty.put("scheduleRows", new ArrayList<>());
            return Result.ok(empty, scoped ? "无待排期需求(选中版本下没有需求)!" : "无待排期需求(已归档版本不参与自动排期)!");
        }
        ScheduleConfigDTO config = currentConfig();
        // 未纳入版本(无归属版本)需求起点: 取所有版本中最大的截至日期(排在所有版本工作之后)
        LocalDate defaultStart = resolveDefaultScheduleStart(config);
        // 人员容量日历(逐日装箱, 跨版本全局共享): key = personName -> (日期 -> 已用人天)
        // 每人每天容量 1 人天: 工时不足 1 天的多个任务可共享同一天(如 0.2+0.3+0.3=0.8 全排当天), 不再整日互斥
        Map<String, Map<LocalDate, BigDecimal>> personDayUsage = new HashMap<>();
        // 预填历史排期占用(优先读排期明细表 pm_dev_schedule 的按人占用, 无明细的历史需求按需求级区间兜底)
        prefillPersonBusyDates(targets, config, personDayUsage);
        // 目标任务已有排期明细中的钉住行: 重算时日期保持不动, 仅登记占用
        Map<String, Map<String, PmDevSchedule>> pinnedByReq = loadPinnedRowsByReq(targets);
        List<PmRequirement> updatedList = new ArrayList<>();
        // 本次重建后的排期明细(含钉住行): 随响应返回, 前端无需再查一次明细接口
        List<PmDevSchedule> scheduleRows = new ArrayList<>();
        for (PmRequirement req : targets) {
            PmIteration iteration = resolveIterationForReq(req, iterationMap);
            LocalDate versionStartDate = ScheduleDateUtils.getWorkdayStartDate(
                    iteration != null && iteration.getStartDate() != null
                            ? toLocalDate(iteration.getStartDate()) : defaultStart, config);
            // 前后端人员(已自动剔除"待分配/不涉及"等占位标识)
            List<String> feDevs = PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev());
            List<String> beDevs = PersonnelNameUtils.parsePersonnelNames(req.getBackendDev());
            // 工时: 无拆分时用总工时；有拆分时各岗位按各自单人工时并行占用
            BigDecimal totalWorkload = (req.getWorkload() != null && req.getWorkload().compareTo(BigDecimal.ZERO) > 0)
                    ? req.getWorkload() : BigDecimal.ONE;
            BigDecimal feWorkload = positiveWorkload(req.getFrontendWorkload());
            BigDecimal beWorkload = positiveWorkload(req.getBackendWorkload());
            boolean hasSplit = feWorkload != null || beWorkload != null;
            Map<String, PmDevSchedule> pinned = pinnedByReq.getOrDefault(req.getId(), new HashMap<>());
            // 按人逐个装箱: 每个人员从自己最早空闲日起, 按各自工时连续占用工作日(前后端/多人互不等待)
            List<PmDevSchedule> rows = new ArrayList<>();
            if (hasSplit) {
                if (feWorkload != null && feWorkload.compareTo(BigDecimal.ZERO) > 0) {
                    for (String person : feDevs) {
                        packScheduleRow(req, person, "frontend", feWorkload, versionStartDate, config, personDayUsage, pinned, rows);
                    }
                }
                if (beWorkload != null && beWorkload.compareTo(BigDecimal.ZERO) > 0) {
                    for (String person : beDevs) {
                        packScheduleRow(req, person, "backend", beWorkload, versionStartDate, config, personDayUsage, pinned, rows);
                    }
                }
            } else {
                for (String person : mergeDevs(feDevs, beDevs)) {
                    String role = feDevs.contains(person) && !beDevs.contains(person) ? "frontend"
                            : (beDevs.contains(person) && !feDevs.contains(person) ? "backend" : "fullstack");
                    packScheduleRow(req, person, role, totalWorkload, versionStartDate, config, personDayUsage, pinned, rows);
                }
            }
            // 需求整体区间: 两岗位均无有效人员/工时时段落版本首日(1人天占位), 否则取所有任务行 min/max
            LocalDate taskStartDate;
            LocalDate taskEndDate;
            if (rows.isEmpty()) {
                taskStartDate = versionStartDate;
                taskEndDate = ScheduleDateUtils.calculateDevEndDate(taskStartDate, BigDecimal.ONE, config);
            } else {
                taskStartDate = rows.stream().map(r -> toLocalDate(r.getDevStartDate())).min(LocalDate::compareTo).orElse(versionStartDate);
                taskEndDate = rows.stream().map(r -> toLocalDate(r.getDevEndDate())).max(LocalDate::compareTo).orElse(taskStartDate);
            }
            // 持久化新排期: 需求整体区间 + 排期明细(钉住行原样保留, 其余删除后按人重建)
            PmRequirement update = new PmRequirement();
            update.setId(req.getId());
            update.setDevStartDate(toDate(taskStartDate));
            update.setDevEndDate(toDate(taskEndDate));
            update.setUpdateBy(safeGetCurrentUser());
            update.setUpdateTime(new Date());
            updateById(update);
            replaceScheduleRows(req.getId(), rows);
            scheduleRows.addAll(rows);
            PmRequirement updated = getById(req.getId());
            updatedList.add(updated);
            recordHistory(req, "SCHEDULE", null, null, null,
                    formatDate(req.getDevStartDate()) + "~" + formatDate(req.getDevEndDate()),
                    ScheduleDateUtils.FORMATTER.format(taskStartDate) + "~" + ScheduleDateUtils.FORMATTER.format(taskEndDate),
                    "自动排期: 需求 #" + req.getReqNo() + " 排期至 "
                            + ScheduleDateUtils.FORMATTER.format(taskStartDate) + " ~ " + ScheduleDateUtils.FORMATTER.format(taskEndDate));
        }
        // 同一人员任务顺序seq: 跨需求按开始日全局重排(升序即执行顺序)
        Set<String> scheduledPersons = new HashSet<>();
        for (PmRequirement req : targets) {
            scheduledPersons.addAll(personsOf(req));
        }
        reassignSeq(scheduledPersons);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("scheduledCount", targets.size());
        result.put("updatedList", updatedList);
        result.put("scheduleRows", scheduleRows);
        return Result.ok(result, "⚡ 已按人员工作量逐日装箱并生成排期明细 " + targets.size()
                + " 项需求(前后端各自独立起算, 钉住任务保持不动, 可一键重算)!");
    }

    // ==================== 回收站(逻辑删除需求的恢复与彻底删除) ====================

    @Override
    public Result<PageResult<PmRequirement>> listDeleted(Integer pageNum, Integer pageSize) {
        // 自定义 @Select 直接查 del_flag=1, 不受 @TableLogic 过滤影响; 已按删除时间倒序(新删在前)
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        PageHelper.startPage(num, size);
        List<PmRequirement> deleted = baseMapper.selectDeletedList();
        return Result.ok(new PageResult<>(deleted), "回收站列表-查询成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> restore(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new MyRuntimeException("错误:需求ID不能为空!");
        }
        // 仅回收站内(del_flag=1)可恢复: 原生 UPDATE 带 del_flag=1 条件, 重复恢复返回 0 行
        int restored = baseMapper.restoreDeleted(id, safeGetCurrentUser());
        if (restored <= 0) {
            throw new MyRuntimeException("错误:需求不存在或已恢复!");
        }
        PmRequirement req = baseMapper.selectById(id);
        recordHistory(req, "RESTORE", null, null, null, null, null,
                "从回收站恢复需求 #" + (req != null ? req.getReqNo() : id));
        return Result.ok(true, "需求 #" + (req != null ? req.getReqNo() : id) + " 已恢复!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> restoreBatch(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        List<String> validIds = ids.stream().filter(StringUtils::isNotEmpty).distinct().toList();
        if (validIds.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        int count = 0;
        for (String id : validIds) {
            if (baseMapper.restoreDeleted(id, safeGetCurrentUser()) > 0) {
                count++;
            }
        }
        if (count == 0) {
            throw new MyRuntimeException("错误:选中需求均不存在或已恢复!");
        }
        return Result.ok(count, "已成功恢复 " + count + " 项需求!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deletePermanently(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new MyRuntimeException("错误:需求ID不能为空!");
        }
        // 物理删除前取快照(删后查不到), 用于历史记录描述
        PmRequirement exist = baseMapper.selectById(id);
        purgeRelatedRows(id);
        if (baseMapper.physicalDeleteById(id) <= 0) {
            throw new MyRuntimeException("错误:需求不存在或已被彻底删除!");
        }
        if (exist != null) {
            recordHistory(exist, "PURGE", null, null, null, null, null,
                    "彻底删除需求 #" + exist.getReqNo() + "(不可恢复)");
        }
        return Result.ok(true, "需求 #" + (exist != null ? exist.getReqNo() : id) + " 已彻底删除!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> deletePermanentlyBatch(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        List<String> validIds = ids.stream().filter(StringUtils::isNotEmpty).distinct().toList();
        if (validIds.isEmpty()) {
            throw new MyRuntimeException("错误:需求ID列表不能为空!");
        }
        int count = 0;
        for (String id : validIds) {
            purgeRelatedRows(id);
            if (baseMapper.physicalDeleteById(id) > 0) {
                count++;
            }
        }
        if (count == 0) {
            throw new MyRuntimeException("错误:选中需求均不存在或已被彻底删除!");
        }
        return Result.ok(count, "已成功彻底删除 " + count + " 项需求!");
    }

    /**
     * 彻底删除前的级联清理: 排期明细与审计日志物理删除(两者其一有 @TableLogic, MP 删除只会软删, 故走原生 DELETE)
     */
    private void purgeRelatedRows(String reqId) {
        devScheduleMapper.physicalDeleteByReqId(reqId);
        historyMapper.physicalDeleteByReqId(reqId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 解析需求归属的迭代版本: iterationId 优先, 为空时回退 month(版本号)
     * 排期主循环与全局排期的归档剔除共用同一解析口径, 避免两处判断漂移
     */
    private PmIteration resolveIterationForReq(PmRequirement req, Map<String, PmIteration> iterationMap) {
        PmIteration iteration = StringUtils.isEmpty(req.getIterationId()) ? null : iterationMap.get(req.getIterationId());
        if (iteration == null && StringUtils.isNotEmpty(req.getMonth())) {
            iteration = iterationMap.get(req.getMonth());
        }
        return iteration;
    }

    /**
     * 预填历史排期占用: 将本次重排范围之外、已排期需求的开发人员占用登记进全局占用日历
     * 保证自动排期不会把新任务叠到该人员既有任务上(跨版本生效)——
     * 若前序版本任务排到了当前版本开始日之后，涉及他的任务将顺延到其最早空闲日
     *
     * @param targets        本次排期目标(其旧排期将被覆盖，不参与预填)
     * @param config         排期配置
     * @param personDayUsage 全局人员容量日历(人 -> 日期 -> 已用人天)
     */
    private void prefillPersonBusyDates(List<PmRequirement> targets, ScheduleConfigDTO config,
                                        Map<String, Map<LocalDate, BigDecimal>> personDayUsage) {
        Set<String> targetIds = new HashSet<>();
        targets.forEach(t -> targetIds.add(t.getId()));
        // 1. 排期明细(按人精确占用): 本次重排范围之外的全部明细行, 逐人逐日登记
        List<PmDevSchedule> scheduleRows = targetIds.isEmpty()
                ? devScheduleMapper.selectList(new LambdaQueryWrapper<PmDevSchedule>())
                : devScheduleMapper.selectList(new LambdaQueryWrapper<PmDevSchedule>()
                        .notIn(PmDevSchedule::getReqId, targetIds));
        // 防御: 过滤孤儿明细行(需求已删除但明细残留, 如手工清库/历史级联遗漏),
        // 否则脏占用会永久顶后这些人员的新排期
        Set<String> validReqIds = list(new LambdaQueryWrapper<PmRequirement>().select(PmRequirement::getId))
                .stream().map(PmRequirement::getId).collect(Collectors.toSet());
        scheduleRows = scheduleRows.stream()
                .filter(row -> validReqIds.contains(row.getReqId()))
                .toList();
        Set<String> reqsWithSchedule = new HashSet<>();
        for (PmDevSchedule row : scheduleRows) {
            if (row.getDevStartDate() == null || row.getDevEndDate() == null) {
                continue;
            }
            reqsWithSchedule.add(row.getReqId());
            LocalDate start = toLocalDate(row.getDevStartDate());
            LocalDate end = toLocalDate(row.getDevEndDate());
            if (end.isBefore(start)) {
                continue;
            }
            // 按该行工时摊入区间各有效工作日(工时缺失时整日占满), 保留同日剩余容量供小任务共享
            spreadRowLoad(row.getPersonName(), start, end, row.getWorkload(), config, personDayUsage);
        }
        // 2. 兜底(迁移兼容): 尚无排期明细的历史需求, 按需求级区间占用(粗粒度, 首跑后排期明细即接管)
        List<PmRequirement> history = list(new LambdaQueryWrapper<PmRequirement>()
                .isNotNull(PmRequirement::getDevStartDate)
                .isNotNull(PmRequirement::getDevEndDate));
        for (PmRequirement req : history) {
            if (targetIds.contains(req.getId()) || reqsWithSchedule.contains(req.getId())) {
                continue;
            }
            Set<String> devs = new java.util.LinkedHashSet<>();
            devs.addAll(PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev()));
            devs.addAll(PersonnelNameUtils.parsePersonnelNames(req.getBackendDev()));
            if (devs.isEmpty()) {
                continue;
            }
            LocalDate start = toLocalDate(req.getDevStartDate());
            LocalDate end = toLocalDate(req.getDevEndDate());
            if (start == null || end == null || end.isBefore(start)) {
                continue;
            }
            LocalDate cursor = start;
            while (!cursor.isAfter(end)) {
                if (!ScheduleDateUtils.isDateSkipped(cursor, config)) {
                    for (String dev : devs) {
                        // 粗粒度兜底: 无明细时按整日占满(容量记满 1), 首跑后排期明细即接管为精确摊入
                        personDayUsage.computeIfAbsent(dev, k -> new HashMap<>())
                                .merge(cursor, BigDecimal.ONE, BigDecimal::max);
                    }
                }
                cursor = cursor.plusDays(1);
            }
        }
    }

    /**
     * 单个人员任务装箱: 钉住行日期保持不动(仅按工时登记占用),
     * 其余从该人员最早可行日起做容量装箱——每人每天容量 1 人天,
     * 工时不足 1 天的任务优先填入当天剩余容量(多个小任务可共享同一天), 当天装不下才顺延
     */
    private void packScheduleRow(PmRequirement req, String person, String role, BigDecimal workload,
                                 LocalDate versionStartDate, ScheduleConfigDTO config,
                                 Map<String, Map<LocalDate, BigDecimal>> personDayUsage,
                                 Map<String, PmDevSchedule> pinned, List<PmDevSchedule> rows) {
        PmDevSchedule pin = pinned.get(person + "|" + role);
        if (pin != null) {
            LocalDate pinStart = toLocalDate(pin.getDevStartDate());
            LocalDate pinEnd = toLocalDate(pin.getDevEndDate());
            if (pinEnd != null && !pinEnd.isBefore(pinStart)) {
                BigDecimal pinLoad = positiveWorkload(pin.getWorkload()) != null ? pin.getWorkload() : workload;
                spreadRowLoad(person, pinStart, pinEnd, pinLoad, config, personDayUsage);
            }
            pin.setReqNo(req.getReqNo());
            pin.setIterationId(req.getIterationId());
            rows.add(pin);
            return;
        }
        LocalDate start = findEarliestFeasibleStart(versionStartDate, person, workload, config, personDayUsage);
        // 容量装箱: 从 start 起逐日消耗剩余容量(每天至多 1), 返回任务实际触达的最后一天
        LocalDate end = applyTaskLoad(person, start, workload, config, personDayUsage);
        PmDevSchedule row = new PmDevSchedule();
        row.setReqId(req.getId());
        row.setReqNo(req.getReqNo());
        row.setIterationId(req.getIterationId());
        row.setPersonName(person);
        row.setDevRole(role);
        row.setWorkload(workload);
        row.setDevStartDate(toDate(start));
        row.setDevEndDate(toDate(end));
        row.setIsPinned(false);
        row.setTenantId(resolveTenantId(req));
        rows.add(row);
    }

    /**
     * 查询指定需求的钉住排期行: key = personName|devRole
     */
    private Map<String, PmDevSchedule> loadPinnedRows(String reqId) {
        List<PmDevSchedule> rows = devScheduleMapper.selectList(new LambdaQueryWrapper<PmDevSchedule>()
                .eq(PmDevSchedule::getReqId, reqId)
                .eq(PmDevSchedule::getIsPinned, true));
        Map<String, PmDevSchedule> map = new HashMap<>();
        rows.forEach(r -> map.put(r.getPersonName() + "|" + r.getDevRole(), r));
        return map;
    }

    /**
     * 批量查询目标需求的钉住排期行: reqId -> (personName|devRole -> row)
     */
    private Map<String, Map<String, PmDevSchedule>> loadPinnedRowsByReq(List<PmRequirement> targets) {
        Map<String, Map<String, PmDevSchedule>> result = new HashMap<>();
        if (targets.isEmpty()) {
            return result;
        }
        List<String> ids = targets.stream().map(PmRequirement::getId).toList();
        List<PmDevSchedule> rows = devScheduleMapper.selectList(new LambdaQueryWrapper<PmDevSchedule>()
                .in(PmDevSchedule::getReqId, ids)
                .eq(PmDevSchedule::getIsPinned, true));
        for (PmDevSchedule row : rows) {
            result.computeIfAbsent(row.getReqId(), k -> new HashMap<>())
                    .put(row.getPersonName() + "|" + row.getDevRole(), row);
        }
        return result;
    }

    /**
     * 替换某需求的排期明细: 带 ID 的行为钉住保留行(更新), 其余旧行物理删除, 新行插入
     */
    private void replaceScheduleRows(String reqId, List<PmDevSchedule> rows) {
        List<PmDevSchedule> existing = devScheduleMapper.selectList(new LambdaQueryWrapper<PmDevSchedule>()
                .eq(PmDevSchedule::getReqId, reqId));
        Set<String> keepIds = rows.stream()
                .map(PmDevSchedule::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<String> removeIds = existing.stream()
                .map(PmDevSchedule::getId)
                .filter(id -> !keepIds.contains(id))
                .toList();
        if (!removeIds.isEmpty()) {
            // 物理删除: 本表重算即全量替换, 不走逻辑删除(无@TableLogic拦截)
            devScheduleMapper.delete(new LambdaQueryWrapper<PmDevSchedule>()
                    .in(PmDevSchedule::getId, removeIds));
        }
        Date now = new Date();
        String currentUser = safeGetCurrentUser();
        for (PmDevSchedule row : rows) {
            if (row.getId() != null) {
                row.setUpdateBy(currentUser);
                row.setUpdateTime(now);
                devScheduleMapper.updateById(row);
            } else {
                row.setCreateBy(currentUser);
                row.setCreateTime(now);
                devScheduleMapper.insert(row);
            }
        }
    }

    /**
     * 同一人员任务顺序seq: 跨需求按开始日全局重排(升序即执行顺序)
     */
    private void reassignSeq(Set<String> persons) {
        if (persons == null || persons.isEmpty()) {
            return;
        }
        List<PmDevSchedule> all = devScheduleMapper.selectList(new LambdaQueryWrapper<PmDevSchedule>()
                .in(PmDevSchedule::getPersonName, persons));
        Map<String, List<PmDevSchedule>> byPerson = new HashMap<>();
        all.forEach(r -> byPerson.computeIfAbsent(r.getPersonName(), k -> new ArrayList<>()).add(r));
        for (List<PmDevSchedule> personRows : byPerson.values()) {
            personRows.sort(Comparator
                    .comparing((PmDevSchedule r) -> toLocalDate(r.getDevStartDate()))
                    .thenComparing(r -> r.getReqNo() == null ? 0 : r.getReqNo()));
            int seq = 1;
            for (PmDevSchedule row : personRows) {
                if (row.getSeq() == null || !row.getSeq().equals(seq)) {
                    row.setSeq(seq);
                    devScheduleMapper.updateById(row);
                }
                seq++;
            }
        }
    }

    /**
     * 需求全部开发人员(前后端合并去重)
     */
    private Set<String> personsOf(PmRequirement req) {
        Set<String> persons = new HashSet<>();
        persons.addAll(PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev()));
        persons.addAll(PersonnelNameUtils.parsePersonnelNames(req.getBackendDev()));
        return persons;
    }

    /**
     * 前后端人员合并去重(保持前端优先顺序)
     */
    private List<String> mergeDevs(List<String> feDevs, List<String> beDevs) {
        Set<String> merged = new java.util.LinkedHashSet<>(feDevs);
        merged.addAll(beDevs);
        return new ArrayList<>(merged);
    }

    /**
     * 按需求当前状态重建排期明细(单条编辑/人员调配/工时调整后调用):
     * 已有排期的需求才重建(无排期的不凭空生成), 钉住行保持不动
     */
    private void rebuildScheduleForReq(String reqId) {
        PmRequirement req = getById(reqId);
        if (req == null || req.getDevStartDate() == null) {
            return;
        }
        ScheduleConfigDTO config = currentConfig();
        Map<String, PmDevSchedule> pinned = loadPinnedRows(reqId);
        List<String> feDevs = PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev());
        List<String> beDevs = PersonnelNameUtils.parsePersonnelNames(req.getBackendDev());
        BigDecimal feWorkload = positiveWorkload(req.getFrontendWorkload());
        BigDecimal beWorkload = positiveWorkload(req.getBackendWorkload());
        BigDecimal totalWorkload = (req.getWorkload() != null && req.getWorkload().compareTo(BigDecimal.ZERO) > 0)
                ? req.getWorkload() : BigDecimal.ONE;
        boolean hasSplit = feWorkload != null || beWorkload != null;
        LocalDate baseStart = toLocalDate(req.getDevStartDate());
        List<PmDevSchedule> rows = new ArrayList<>();
        if (hasSplit) {
            if (feWorkload != null && feWorkload.compareTo(BigDecimal.ZERO) > 0) {
                for (String person : feDevs) {
                    rows.add(buildScheduleRow(req, person, "frontend", feWorkload, baseStart, config, pinned));
                }
            }
            if (beWorkload != null && beWorkload.compareTo(BigDecimal.ZERO) > 0) {
                for (String person : beDevs) {
                    rows.add(buildScheduleRow(req, person, "backend", beWorkload, baseStart, config, pinned));
                }
            }
        } else {
            for (String person : mergeDevs(feDevs, beDevs)) {
                String role = feDevs.contains(person) && !beDevs.contains(person) ? "frontend"
                        : (beDevs.contains(person) && !feDevs.contains(person) ? "backend" : "fullstack");
                rows.add(buildScheduleRow(req, person, role, totalWorkload, baseStart, config, pinned));
            }
        }
        replaceScheduleRows(reqId, rows);
        reassignSeq(personsOf(req));
        // 钉住行可能超出原需求区间: 用全部明细行回刷需求整体起止日
        if (!rows.isEmpty()) {
            LocalDate min = rows.stream().map(r -> toLocalDate(r.getDevStartDate())).min(LocalDate::compareTo).orElse(null);
            LocalDate max = rows.stream().map(r -> toLocalDate(r.getDevEndDate())).max(LocalDate::compareTo).orElse(null);
            if (min != null && max != null) {
                PmRequirement rangeUpdate = new PmRequirement();
                rangeUpdate.setId(reqId);
                rangeUpdate.setDevStartDate(toDate(min));
                rangeUpdate.setDevEndDate(toDate(max));
                rangeUpdate.setUpdateTime(new Date());
                updateById(rangeUpdate);
            }
        }
    }

    /**
     * 新建需求落库后生成排期明细(无竞争, 各人员从需求开始日起算)
     */
    /**
     * 解析排期基准开始日(与 autoSchedule 口径一致):
     * - 需求自带 devStartDate 时优先使用；
     * - 否则取归属版本(iterationId → month)的开发周期首日——导入未排期的需求应从版本起始日开始，
     *   而不是落到硬编码默认日期(会导致任务全部堆在默认月份, 超出历史版本周期)；
     * - 找不到归属版本时才兜底默认日期。
     */
    private LocalDate resolveScheduleBaseStart(PmRequirement req, ScheduleConfigDTO config) {
        if (req.getDevStartDate() != null) {
            return toLocalDate(req.getDevStartDate());
        }
        PmIteration iteration = null;
        if (StringUtils.isNotEmpty(req.getIterationId())) {
            iteration = iterationMapper.selectById(req.getIterationId());
        }
        if (iteration == null && StringUtils.isNotEmpty(req.getMonth())) {
            iteration = iterationMapper.selectOne(new LambdaQueryWrapper<PmIteration>()
                    .eq(PmIteration::getCode, req.getMonth()).last("limit 1"));
        }
        if (iteration != null && iteration.getStartDate() != null) {
            return ScheduleDateUtils.getWorkdayStartDate(toLocalDate(iteration.getStartDate()), config);
        }
        // 无归属版本(未纳入版本): 起点取所有版本中最大的截至日期, 而非硬编码默认日期
        return resolveDefaultScheduleStart(config);
    }

    /**
     * 未纳入版本(无归属版本)需求的排期兜底起点: 取所有版本中最大的截至日期(endDate),
     * 使无归属任务排在所有版本工作之后, 而不是落到硬编码默认日期(2026-09-08, 会挤占各版本正常排期)。
     * 所有版本均无截至日期时回退 DEFAULT_START_DATE。
     */
    private LocalDate resolveDefaultScheduleStart(ScheduleConfigDTO config) {
        Date maxEnd = iterationMapper.selectMaxEndDate();
        // 取所有版本中最大的截至日期, 再加一天, 排到版本期之外(避免与最后一天版本任务同日)
        LocalDate base = (maxEnd != null ? toLocalDate(maxEnd) : ScheduleDateUtils.DEFAULT_START_DATE).plusDays(1);
        return ScheduleDateUtils.getWorkdayStartDate(base, config);
    }

    private void persistScheduleRowsForReq(PmRequirement req) {
        ScheduleConfigDTO config = currentConfig();
        LocalDate baseStart = resolveScheduleBaseStart(req, config);
        List<String> feDevs = PersonnelNameUtils.parsePersonnelNames(req.getFrontendDev());
        List<String> beDevs = PersonnelNameUtils.parsePersonnelNames(req.getBackendDev());
        BigDecimal feWorkload = positiveWorkload(req.getFrontendWorkload());
        BigDecimal beWorkload = positiveWorkload(req.getBackendWorkload());
        BigDecimal totalWorkload = (req.getWorkload() != null && req.getWorkload().compareTo(BigDecimal.ZERO) > 0)
                ? req.getWorkload() : BigDecimal.ONE;
        boolean hasSplit = feWorkload != null || beWorkload != null;
        List<PmDevSchedule> rows = new ArrayList<>();
        if (hasSplit) {
            if (feWorkload != null && feWorkload.compareTo(BigDecimal.ZERO) > 0) {
                for (String person : feDevs) {
                    rows.add(buildScheduleRow(req, person, "frontend", feWorkload, baseStart, config, new HashMap<>()));
                }
            }
            if (beWorkload != null && beWorkload.compareTo(BigDecimal.ZERO) > 0) {
                for (String person : beDevs) {
                    rows.add(buildScheduleRow(req, person, "backend", beWorkload, baseStart, config, new HashMap<>()));
                }
            }
        } else {
            for (String person : mergeDevs(feDevs, beDevs)) {
                String role = feDevs.contains(person) && !beDevs.contains(person) ? "frontend"
                        : (beDevs.contains(person) && !feDevs.contains(person) ? "backend" : "fullstack");
                rows.add(buildScheduleRow(req, person, role, totalWorkload, baseStart, config, new HashMap<>()));
            }
        }
        replaceScheduleRows(req.getId(), rows);
        reassignSeq(personsOf(req));
    }

    /**
     * 构建单条排期明细(无装箱竞争场景: 从基准日开始日顺延到有效工作日, 按工时推导完成日)
     */
    private PmDevSchedule buildScheduleRow(PmRequirement req, String person, String role, BigDecimal workload,
                                           LocalDate baseStart, ScheduleConfigDTO config,
                                           Map<String, PmDevSchedule> pinned) {
        PmDevSchedule pin = pinned.get(person + "|" + role);
        if (pin != null) {
            pin.setReqNo(req.getReqNo());
            pin.setIterationId(req.getIterationId());
            return pin;
        }
        LocalDate start = ScheduleDateUtils.getWorkdayStartDate(baseStart, config);
        LocalDate end = ScheduleDateUtils.calculateDevEndDate(start, workload, config);
        PmDevSchedule row = new PmDevSchedule();
        row.setReqId(req.getId());
        row.setReqNo(req.getReqNo());
        row.setIterationId(req.getIterationId());
        row.setPersonName(person);
        row.setDevRole(role);
        row.setWorkload(workload);
        row.setDevStartDate(toDate(start));
        row.setDevEndDate(toDate(end));
        row.setIsPinned(false);
        row.setTenantId(resolveTenantId(req));
        return row;
    }

    /**
     * 级联清理需求的排期明细(需求删除时调用)
     */
    private void deleteScheduleRows(List<String> reqIds) {
        if (reqIds == null || reqIds.isEmpty()) {
            return;
        }
        devScheduleMapper.delete(new LambdaQueryWrapper<PmDevSchedule>()
                .in(PmDevSchedule::getReqId, reqIds));
    }

    /**
     * 逐日装箱(容量模型): 从版本首日起向后逐日扫描，返回该人员剩余容量足以容纳整个任务的最早可开工日期
     * 每人每天容量 1 人天: 工时不足 1 天的多个任务可共享同一天(如 0.2+0.3+0.3 全排当天)，装不下才顺延
     * 与"人员游标"模型的本质区别: 每个任务都从版本首日重新扫描，因此后面排到的任务可以回填前面留下的空档，
     * 只要人员在这些日期确实有剩余容量即可开工，各人员之间天然并行(同一天可有多人各自推进自己的任务)
     * 容量日历为全局共享(跨版本)，前序版本任务的占用会参与可行性校验
     *
     * @param versionStartDate 版本开发周期首日(已规整为有效工作日)
     * @param person           任务人员(为空白表示未分配，无人力约束)
     * @param workload         预估工时(单人)
     * @param config           排期配置
     * @param personDayUsage   全局人员容量日历(人 -> 日期 -> 已用人天)
     * @return 最早可开工日期
     */
    private LocalDate findEarliestFeasibleStart(LocalDate versionStartDate, String person,
                                                BigDecimal workload, ScheduleConfigDTO config,
                                                Map<String, Map<LocalDate, BigDecimal>> personDayUsage) {
        if (versionStartDate == null) {
            versionStartDate = ScheduleDateUtils.DEFAULT_START_DATE;
        }
        if (StringUtils.isEmpty(person)) {
            // 未分配人员的需求无任何人力约束，一律从版本首日起排
            return versionStartDate;
        }
        LocalDate candidate = versionStartDate;
        // 安全上限: 最多向后探测 3 年，杜绝异常配置下的死循环
        LocalDate limit = versionStartDate.plusYears(3);
        while (!candidate.isAfter(limit)) {
            if (canFitFrom(person, candidate, workload, config, personDayUsage)) {
                return candidate;
            }
            candidate = ScheduleDateUtils.getNextWorkday(candidate, config);
        }
        return versionStartDate;
    }

    /**
     * 容量试算(不改日历): 从 start 起逐有效工作日消耗剩余容量(每天至多 1 人天)，
     * 模拟任务能否在有限天数内完整放下
     */
    private boolean canFitFrom(String person, LocalDate start, BigDecimal workload,
                               ScheduleConfigDTO config, Map<String, Map<LocalDate, BigDecimal>> personDayUsage) {
        BigDecimal remaining = positiveWorkload(workload) != null ? workload : BigDecimal.ONE;
        Map<LocalDate, BigDecimal> usage = personDayUsage.getOrDefault(person, new HashMap<>());
        LocalDate cursor = start;
        // 安全上限: 3 年内的有效工作日远超任何合法工时所需
        LocalDate limit = start.plusYears(3);
        boolean firstDay = true;
        while (!cursor.isAfter(limit)) {
            if (!ScheduleDateUtils.isDateSkipped(cursor, config)) {
                BigDecimal used = usage.getOrDefault(cursor, BigDecimal.ZERO);
                BigDecimal available = BigDecimal.ONE.subtract(used).max(BigDecimal.ZERO);
                // 首日必须实际获得容量, 杜绝任务"虚挂"在零剩余容量的日期上(跨出无占用的空天头)
                if (firstDay && available.compareTo(BigDecimal.ZERO) <= 0) {
                    return false;
                }
                remaining = remaining.subtract(available);
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    return true;
                }
                firstDay = false;
            }
            cursor = cursor.plusDays(1);
        }
        return false;
    }

    /**
     * 容量登记: 从 start 起逐有效工作日消耗剩余容量(每天至多 1 人天)，直至任务工时耗尽，
     * 返回任务实际触达的最后一天(容量充裕时 = ceil(workload) 个工作日，首日有占用时顺延)
     */
    private LocalDate applyTaskLoad(String person, LocalDate start, BigDecimal workload,
                                    ScheduleConfigDTO config, Map<String, Map<LocalDate, BigDecimal>> personDayUsage) {
        BigDecimal remaining = positiveWorkload(workload) != null ? workload : BigDecimal.ONE;
        Map<LocalDate, BigDecimal> usage = personDayUsage.computeIfAbsent(person, k -> new HashMap<>());
        LocalDate cursor = start;
        LocalDate lastTouched = start;
        LocalDate limit = start.plusYears(3);
        while (!cursor.isAfter(limit) && remaining.compareTo(BigDecimal.ZERO) > 0) {
            if (!ScheduleDateUtils.isDateSkipped(cursor, config)) {
                BigDecimal used = usage.getOrDefault(cursor, BigDecimal.ZERO);
                BigDecimal available = BigDecimal.ONE.subtract(used).max(BigDecimal.ZERO);
                if (available.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal take = available.min(remaining);
                    usage.put(cursor, used.add(take));
                    remaining = remaining.subtract(take);
                    lastTouched = cursor;
                }
            }
            cursor = cursor.plusDays(1);
        }
        return lastTouched;
    }

    /**
     * 固定区间占用登记(钉住行/历史明细行): 将工时均摊到区间内各有效工作日，
     * 保留同日剩余容量供后续小任务共享；工时缺失时按整日占满处理
     */
    private void spreadRowLoad(String person, LocalDate start, LocalDate end, BigDecimal workload,
                               ScheduleConfigDTO config, Map<String, Map<LocalDate, BigDecimal>> personDayUsage) {
        if (StringUtils.isEmpty(person) || start == null || end == null || end.isBefore(start)) {
            return;
        }
        // 统计区间内有效工作日数
        int workdays = 0;
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            if (!ScheduleDateUtils.isDateSkipped(cursor, config)) {
                workdays++;
            }
            cursor = cursor.plusDays(1);
        }
        if (workdays == 0) {
            return;
        }
        // 无工时(历史脏数据)按整日占满; 有工时则均摊(单日任务摊入值=工时, 与逐日装箱口径一致)
        BigDecimal perDay = positiveWorkload(workload) != null
                ? workload.divide(BigDecimal.valueOf(workdays), 4, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ONE;
        Map<LocalDate, BigDecimal> usage = personDayUsage.computeIfAbsent(person, k -> new HashMap<>());
        cursor = start;
        while (!cursor.isAfter(end)) {
            if (!ScheduleDateUtils.isDateSkipped(cursor, config)) {
                usage.merge(cursor, perDay, BigDecimal::add);
            }
            cursor = cursor.plusDays(1);
        }
    }

    /**
     * 需求信息纠偏：工时基准、人员字段标准化、日历排期推导
     * 严防1人天任务在甘特图上跨度达十几天、以及后续任务挤压等非理性排期
     */
    private void sanitizeRequirement(PmRequirement requirement, PmRequirement exist) {
        // 生效人员字段(未传则沿用旧值)
        String effFeDev = requirement.getFrontendDev() != null ? requirement.getFrontendDev()
                : (exist != null ? exist.getFrontendDev() : null);
        String effBeDev = requirement.getBackendDev() != null ? requirement.getBackendDev()
                : (exist != null ? exist.getBackendDev() : null);
        // 工时拆分: 前端/后端研发工时段(任一缺失时沿用旧值)，值为"单人"工时
        BigDecimal feWorkload = requirement.getFrontendWorkload() != null ? requirement.getFrontendWorkload()
                : (exist != null ? exist.getFrontendWorkload() : null);
        BigDecimal beWorkload = requirement.getBackendWorkload() != null ? requirement.getBackendWorkload()
                : (exist != null ? exist.getBackendWorkload() : null);
        // 该侧无有效人员(空或"不涉及")时, 该侧工时强制 0——前端已禁用编辑, 后端做最终保障
        // 反向兜底: 有人员但工时为 null(如批量导入未带工时列)或 0(历史"不涉及"残留) → 恢复默认 1 人天,
        // 防止导入(1前端+1后端)后总工时不按人数合计(应为 2 而非 1), 与 batchAssign 纠偏口径完全一致
        if (PersonnelNameUtils.parsePersonnelNames(effFeDev).isEmpty()) {
            feWorkload = BigDecimal.ZERO;
            requirement.setFrontendWorkload(BigDecimal.ZERO);
        } else if (feWorkload == null || feWorkload.compareTo(BigDecimal.ZERO) == 0) {
            feWorkload = BigDecimal.ONE;
            requirement.setFrontendWorkload(BigDecimal.ONE);
        }
        if (PersonnelNameUtils.parsePersonnelNames(effBeDev).isEmpty()) {
            beWorkload = BigDecimal.ZERO;
            requirement.setBackendWorkload(BigDecimal.ZERO);
        } else if (beWorkload == null || beWorkload.compareTo(BigDecimal.ZERO) == 0) {
            beWorkload = BigDecimal.ONE;
            requirement.setBackendWorkload(BigDecimal.ONE);
        }
        // 工时基准(默认1人天)
        BigDecimal workload = requirement.getWorkload() != null ? requirement.getWorkload()
                : (exist != null ? exist.getWorkload() : null);
        // 总工时(仅显示口径): 各角色单人工时 × 该角色人数；人数由人员字段实时解析
        if (feWorkload != null || beWorkload != null) {
            BigDecimal headcountTotal = headcountTotalWorkload(feWorkload, beWorkload, effFeDev, effBeDev);
            if (headcountTotal.compareTo(BigDecimal.ZERO) > 0) {
                workload = headcountTotal;
            }
        }
        // 两侧均无有效人员(工时为 0)时总工时为 0; 其余情况保持 1 人天兜底
        boolean noDevAtAll = feWorkload != null && beWorkload != null
                && feWorkload.compareTo(BigDecimal.ZERO) == 0 && beWorkload.compareTo(BigDecimal.ZERO) == 0;
        if (noDevAtAll) {
            workload = BigDecimal.ZERO;
        } else if (workload == null || workload.compareTo(BigDecimal.ZERO) <= 0) {
            workload = BigDecimal.ONE;
        }
        workload = workload.setScale(1, RoundingMode.HALF_UP);
        requirement.setWorkload(workload);
        if (feWorkload != null) {
            requirement.setFrontendWorkload(feWorkload.max(BigDecimal.ZERO).setScale(1, RoundingMode.HALF_UP));
        }
        if (beWorkload != null) {
            requirement.setBackendWorkload(beWorkload.max(BigDecimal.ZERO).setScale(1, RoundingMode.HALF_UP));
        }
        // 开始日期(默认基准开始日期)
        LocalDate startDate = requirement.getDevStartDate() != null ? toLocalDate(requirement.getDevStartDate())
                : (exist != null && exist.getDevStartDate() != null ? toLocalDate(exist.getDevStartDate())
                : ScheduleDateUtils.DEFAULT_START_DATE);
        // 排期时长口径: 前后端并行推进，取两岗位单人工时的较大者(与人数/总工时无关)
        BigDecimal feDur = positiveWorkload(feWorkload);
        BigDecimal beDur = positiveWorkload(beWorkload);
        BigDecimal scheduleWorkload = (feDur != null || beDur != null)
                ? (feDur == null ? BigDecimal.ZERO : feDur).max(beDur == null ? BigDecimal.ZERO : beDur)
                : workload;
        // 截止日期推导：未指定、或实际跨度异常远大于合理工时时必须校正
        LocalDate endDate = requirement.getDevEndDate() != null ? toLocalDate(requirement.getDevEndDate())
                : (exist != null && exist.getDevEndDate() != null ? toLocalDate(exist.getDevEndDate()) : null);
        if (endDate == null) {
            endDate = ScheduleDateUtils.calculateDevEndDate(startDate, scheduleWorkload, currentConfig());
        } else {
            long rawSpanDays = ScheduleDateUtils.calculateDays(startDate, endDate);
            long reasonableSpan = Math.max(4, Math.round(scheduleWorkload.doubleValue() * 2.5 + 2));
            if (rawSpanDays > reasonableSpan) {
                endDate = ScheduleDateUtils.calculateDevEndDate(startDate, scheduleWorkload, currentConfig());
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
     * 人员字段默认值处理：显式传值(含空串=清空)优先，未传(null)时沿用旧值；
     * 纯占位/空值格式化后为空串，不再回退默认值，以满足"未设置则空着、不落库 待分配/PMO/产品团队 等占位"(需求 #2)。
     */
    private String defaultPersonnel(String newValue, String oldValue, String defaultVal) {
        String target = newValue != null ? newValue : oldValue;
        return PersonnelNameUtils.formatPersonnelNames(target);
    }

    /**
     * 归属迭代信息联动：设置月份代码与研发确认发版时间
     *
     * @return 是否显式清空了归属版本 (前端传空串表示清空；传 null 表示未传、保持原值)。
     *         显式清空时会同步清空月份、发版时间与已绑定转测节点(名称类字段置空串、日期字段置 null
     *         由调用方通过 UpdateWrapper 显式 SET NULL 落库)。
     */
    private boolean syncIterationInfo(PmRequirement requirement, PmRequirement exist) {
        // 显式清空归属版本：同步清空月份、发版时间与转测节点绑定
        if (isEmptySent(requirement.getIterationId()) || isEmptySent(requirement.getMonth())) {
            requirement.setIterationId("");
            requirement.setMonth("");
            requirement.setTestNodeId("");
            requirement.setTestNodeName("");
            requirement.setTestNodeDate(null);
            requirement.setReleaseTime(null);
            return true;
        }
        String iterationId = StringUtils.isNotEmpty(requirement.getIterationId())
                ? requirement.getIterationId()
                : (exist != null ? exist.getIterationId() : null);
        String month = StringUtils.isNotEmpty(requirement.getMonth())
                ? requirement.getMonth()
                : (exist != null ? exist.getMonth() : null);
        if (StringUtils.isEmpty(iterationId)) {
            return false;
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
            return false;
        }
        requirement.setIterationId(iteration.getId());
        requirement.setMonth(iteration.getCode());
        if (iteration.getReleaseDate() != null && requirement.getReleaseTime() == null) {
            requirement.setReleaseTime(iteration.getReleaseDate());
        }
        return false;
    }

    /**
     * 判断前端是否显式传了"空值"(空串)。null 视为未传(保持原值)，空串视为显式清空。
     */
    private boolean isEmptySent(String value) {
        return value != null && value.isEmpty();
    }

    /**
     * 状态流转联动开发进度(阶段与默认进度均由字典 pm_requirement_status 驱动):
     * - 开发后阶段(转测/测试/验收/上线): 进度置 100%
     * - 开发前阶段(待评审): 进度归 0
     * - 开发中: 进度为 0 时取字典默认进度; 从开发后阶段回退且已满进度 → 回退 90% 继续开发
     */
    private Integer resolveStatusProgress(String status, Integer currentProgress, String oldStatus) {
        int progress = currentProgress == null ? 0 : currentProgress;
        String phase = pmDictService.statusPhase(status);
        if (PmDictCodes.PHASE_POST.equals(phase)) {
            return 100;
        }
        if (PmDictCodes.PHASE_PRE.equals(phase)) {
            return 0;
        }
        if (PmDictCodes.PHASE_DEV.equals(phase)) {
            if (progress == 0) {
                Integer preset = pmDictService.statusProgress(status);
                return preset == null ? 0 : preset;
            }
            // 从测试等状态回退至开发中，进度回退至90%继续开发
            if (PmDictCodes.PHASE_POST.equals(pmDictService.statusPhase(oldStatus)) && progress >= 100) {
                return 90;
            }
        }
        return progress;
    }

    /**
     * 创建需求时的默认进度(取字典中该状态配置的默认进度, 未配置按 0)
     */
    private Integer resolveDefaultProgress(String status) {
        Integer progress = pmDictService.statusProgress(status);
        return progress == null ? 0 : progress;
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
            history.setTenantId(resolveTenantId(req));
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
        // 一级功能点允许为空(批量导入常只带序号/模块/功能点描述)，功能点描述为必填
        if (StringUtils.isEmpty(requirement.getFeatureDescription())) {
            throw new MyRuntimeException("错误:功能点描述不能为空!");
        }
        // 评审结论/任务状态应与字典保持一致; 存量历史脏值(如旧版"通过")仅告警不阻断, 避免历史行无法保存
        if (StringUtils.isNotEmpty(requirement.getReviewResult()) && !pmDictService.isValidReviewResult(requirement.getReviewResult())) {
            log.warn("需求 #{} 的评审结论【{}】不在字典 pm_review_result 中, 建议在字典管理中维护该取值",
                    requirement.getReqNo(), requirement.getReviewResult());
        }
        if (StringUtils.isEmpty(requirement.getStatus())) {
            requirement.setStatus(pmDictService.defaultStatus());
        } else if (!pmDictService.isValidStatus(requirement.getStatus())) {
            log.warn("需求 #{} 的任务状态【{}】不在字典 pm_requirement_status 中, 建议在字典管理中维护该取值",
                    requirement.getReqNo(), requirement.getStatus());
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

    /**
     * 解析排期明细/审计日志应归属的租户ID: 优先取需求实体上的 tenantId, 缺失时回退当前登录租户
     */
    private String resolveTenantId(PmRequirement req) {
        if (req != null && StringUtils.isNotEmpty(req.getTenantId())) {
            return req.getTenantId();
        }
        return safeGetCurrentTenantId();
    }
}

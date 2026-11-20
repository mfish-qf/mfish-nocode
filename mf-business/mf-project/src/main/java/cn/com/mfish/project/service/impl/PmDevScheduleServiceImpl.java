package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.dto.ScheduleConfigDTO;
import cn.com.mfish.project.entity.PmDevSchedule;
import cn.com.mfish.project.mapper.PmDevScheduleMapper;
import cn.com.mfish.project.req.ReqDevScheduleAdjust;
import cn.com.mfish.project.req.ReqDevSchedulePin;
import cn.com.mfish.project.service.PmDevScheduleService;
import cn.com.mfish.project.service.PmRequirementService;
import cn.com.mfish.project.service.PmScheduleConfigService;
import cn.com.mfish.project.utils.ScheduleDateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @description: 研发排期明细服务实现
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.1
 */
@Slf4j
@Service
public class PmDevScheduleServiceImpl extends ServiceImpl<PmDevScheduleMapper, PmDevSchedule> implements PmDevScheduleService {

    /** 容量模拟的浮点容差 */
    private static final double EPS = 1e-9;

    @Resource
    private PmScheduleConfigService scheduleConfigService;
    @Resource
    private PmRequirementService requirementService;

    @Override
    public Result<List<PmDevSchedule>> listByReqIds(List<String> reqIds) {
        if (reqIds == null || reqIds.isEmpty()) {
            return Result.ok(List.of(), "排期明细-查询成功!");
        }
        List<PmDevSchedule> list = list(new LambdaQueryWrapper<PmDevSchedule>()
                .in(PmDevSchedule::getReqId, reqIds)
                .orderByAsc(PmDevSchedule::getReqNo)
                .orderByAsc(PmDevSchedule::getSeq)
                .orderByAsc(PmDevSchedule::getDevStartDate));
        return Result.ok(list, "排期明细-查询成功!");
    }

    @Override
    public Result<List<PmDevSchedule>> listBySelection(List<String> iterationIds, Boolean includeUnassigned) {
        // 复用需求查询的版本过滤语义解析需求ID, 再按ID聚合排期明细
        // 前端不再回传大量需求UUID; 版本筛选口径(按 iteration_id 精确匹配/未纳入版本/ALL排除已归档)与需求列表完全一致
        List<String> reqIds = requirementService.listReqIdsBySelection(iterationIds, includeUnassigned);
        return listByReqIds(reqIds);
    }

    /**
     * 拖拽调期 —— 「钉住开始日 + 按工时容量逐任务装箱」(与 autoSchedule 同一口径)。
     *
     * ★ 两个事实源缺一不可(用户 2026-11-15 明确要求):
     *   1. workload(工时/人天): 决定任务「应该」消耗多少产能 → 决定区间长度, 也是判定
     *      「周期被挤占拉长」的唯一基准。表中区间即便被拉长过也不影响它, 因此重复拖拽/拖回可还原。
     *   2. dev_start_date/dev_end_date(表中起止日期): 决定锚点(钉住任务的固定开始日)、影响窗口、
     *      窗口外任务已占用的工作日、以及任务的展示顺序。
     *
     * 每人每个有效工作日容量 1 人天, 与 {@code PmRequirementServiceImpl#autoSchedule} 的
     * {@code findEarliestFeasibleStart + applyTaskLoad + spreadRowLoad} 完全一致:
     *  - 被拖任务开始日 = 松手位置并自动钉住; 其它已钉住任务保持表中原开始日(钉住只钉开始日, 不钉周期);
 *  - 影响窗口**递归扩展**: 与窗口相交的任务全部纳入重排并把窗口扩到其整个区间, 直到稳定;
 *    链式重叠(1 人天任务首尾各占 0.5)不会被一半留在窗口外当「均摊幽灵」而叠出超载;
 *  - 钉住任务按锚定日分组优先装箱, 同一天锚定的多个钉住任务按剩余工时比例分摊当天产能
 *    (两个任务都钉在 10-27 → 各得 0.5/天 → 都延长到 10-28, 即用户「特殊情况」);
 *    **锚定日同样按当天剩余容量分配**, 不再强制视为满容量 1.0(否则凭空多出人天 → 容量违规);
 *  - 窗口内未钉住任务按表中原开始日顺序, 从时间轴起点找最早可行日(首日必须实际获得容量),
 *    再逐日吃掉剩余容量直到工时耗尽; 等长任务表现为互换, 遇到满载日顺延;
 *  - 窗口外任务日期完全不动, 其占用按工时均摊登记(不再「整天占满」), 剩余产能可被共享。
     *
     * 每个任务区间 = 「首次获得产能的工作日」~「工时耗尽的工作日」, 所以被拉长过的任务拖到
     * 新位置后会自动收缩回工时对应的正常长度(可还原、幂等)。「周期拉长」说明按**全部同人明细的
     * 最终区间**重算(含窗口外任务的过期说明清理), 不依赖拖拽瞬间参与重排的任务集合。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmDevSchedule> adjust(ReqDevScheduleAdjust req) {
        PmDevSchedule row = getById(req.getId());
        if (row == null) {
            return Result.fail("错误:排期明细不存在!");
        }
        ScheduleConfigDTO config = scheduleConfigService.getConfigData();
        LocalDate newStart;
        try {
            newStart = LocalDate.parse(req.getDevStartDate().trim(), ScheduleDateUtils.FORMATTER);
        } catch (DateTimeParseException e) {
            return Result.fail("错误:开始日期格式不合法(yyyy-MM-dd)!");
        }
        // 拖到周末/节假日: 顺延到最近的有效工作日
        newStart = ScheduleDateUtils.getWorkdayStartDate(newStart, config);
        if (row.getDevStartDate() == null || row.getDevEndDate() == null) {
            return Result.fail("错误:该排期明细缺少起止日期, 无法拖拽调期!");
        }
        LocalDate oldStart = toLocalDate(row.getDevStartDate());
        LocalDate oldEnd = toLocalDate(row.getDevEndDate());
        String pinReason = StringUtils.isEmpty(req.getPinReason()) ? "拖拽调期" : req.getPinReason();
        Date now = new Date();

        // ===== 1) 同一人员全部明细 + 影响窗口 =====
        // 被拖任务的"理论占用天数"由工时决定(不是表中已被拉长的区间)
        double dragNeed = needOf(row.getWorkload());
        int dragNeedDays = Math.max(1, (int) Math.ceil(dragNeed - EPS));
        LocalDate newEndEstimate = advanceWorkdays(newStart, dragNeedDays - 1, config);
        List<PmDevSchedule> all = new ArrayList<>(listSamePersonRows(row));
        all.add(row);
        LocalDate winLo = oldStart.isBefore(newStart) ? oldStart : newStart;
        LocalDate winHi = oldEnd.isAfter(newEndEstimate) ? oldEnd : newEndEstimate;
        Set<String> affectedIds = new HashSet<>();
        // ★ 递归扩展影响窗口: 只要任务区间与当前窗口相交就纳入重排, 并把窗口扩到它的整个区间,
        //   重复直到稳定。旧实现只取「旧区间 ∪ 新区间」的一次性快照, 链式重叠(如 1 人天任务
        //   首尾各占 0.5)会有一半留在窗口外被当作「均摊幽灵」, 拖拽把任务前移进这条链时
        //   就会叠出 0.5 人天的硬性超载(2026-11-15 实测 Hall 违规 2 个窗口)。
        //   区间互不重叠的普通任务不会被波及, 因此用户 4 个验收例子结果不变。
        boolean expanded = true;
        while (expanded) {
            expanded = false;
            for (PmDevSchedule t : all) {
                if (affectedIds.contains(t.getId())) {
                    continue;
                }
                LocalDate s = (t == row) ? oldStart
                        : (t.getDevStartDate() == null ? null : toLocalDate(t.getDevStartDate()));
                LocalDate e = (t == row) ? oldEnd
                        : (t.getDevEndDate() == null ? null : toLocalDate(t.getDevEndDate()));
                if (s == null || e == null || e.isBefore(winLo) || s.isAfter(winHi)) {
                    continue;
                }
                affectedIds.add(t.getId());
                if (s.isBefore(winLo)) {
                    winLo = s;
                }
                if (e.isAfter(winHi)) {
                    winHi = e;
                }
                expanded = true;
            }
        }
        affectedIds.add(row.getId());
        LocalDate timelineStart = winLo;
        if (newStart.isBefore(timelineStart)) {
            timelineStart = newStart;
        }

        // ===== 2) 窗口外任务: 日期完全不动, 占用按工时均摊登记到各有效工作日 =====
        // 旧实现用 Set<LocalDate> 把窗口外任务的每个工作日「整天占满」, 哪怕该任务只有 0.5 人天,
        // 会制造大量产能黑洞, 使窗口内任务连续多天拿不到产能、被切成 7~8 个工作日的碎片区间。
        // 现与 autoSchedule.spreadRowLoad 同口径: 每人每天 1 人天容量, 剩余产能可被其它任务共享。
        Map<LocalDate, Double> used = new HashMap<>();
        for (PmDevSchedule t : all) {
            if (affectedIds.contains(t.getId())) {
                continue;
            }
            registerSpreadLoad(used, toLocalDate(t.getDevStartDate()), toLocalDate(t.getDevEndDate()),
                    needOf(t.getWorkload()), config);
        }

        // ===== 3) 构造重排计划: 钉住组(锚定开始日) + 普通组(按表中原开始日顺序) =====
        Map<String, DragPlan> plans = new LinkedHashMap<>();
        Map<LocalDate, List<DragPlan>> pinnedGroups = new TreeMap<>();
        List<DragPlan> normals = new ArrayList<>();
        for (PmDevSchedule t : all) {
            if (!affectedIds.contains(t.getId())) {
                continue;
            }
            double need = needOf(t.getWorkload());
            DragPlan p;
            if (t == row) {
                p = new DragPlan(t, need, newStart, true, true, pinReason);
            } else if (Boolean.TRUE.equals(t.getIsPinned())) {
                p = new DragPlan(t, need, toLocalDate(t.getDevStartDate()), true, false, null);
            } else {
                p = new DragPlan(t, need, timelineStart, false, false, null);
            }
            plans.put(t.getId(), p);
            if (p.pinned) {
                pinnedGroups.computeIfAbsent(p.anchor, k -> new ArrayList<>()).add(p);
            } else {
                normals.add(p);
            }
        }

        // ===== 4) 逐任务容量装箱(与 autoSchedule 的 findEarliestFeasibleStart + applyTaskLoad 同构) =====
        // 旧实现是「逐工作日把当天产能切给所有候选任务」: 钉住任务锚定日独占整天、窗口外任务又整天锁死,
        // 普通任务只能捡到 0.1~0.5 的碎片, 一个 1 人天任务被拖成 7 个工作日。现改为逐任务整块装箱:
        //  4.1 钉住任务(含被拖任务)按锚定日分组优先装箱, 按当天剩余容量逐日推进直到工时耗尽;
        //      同一天锚定的多个钉住任务按剩余工时比例分摊(用户「特殊情况」: 两个任务都钉在
        //      10-27 → 各得 0.5/天 → 都延长到 10-28), 开始日始终固定为锚定日;
        //  4.2 普通任务按表中原开始日顺序, 从时间轴起点找最早可行日(首日必须实际获得容量),
        //      再逐日吃掉剩余容量直到工时耗尽; 遇到满载日顺延(区间跨越但不反复切碎)。
        for (Map.Entry<LocalDate, List<DragPlan>> entry : pinnedGroups.entrySet()) {
            LocalDate anchor = entry.getKey();
            List<DragPlan> group = entry.getValue();
            LocalDate day = ScheduleDateUtils.getWorkdayStartDate(anchor, config);
            for (int guard = 0; guard < 4000 && anyRemaining(group); guard++) {
                // ★ 锚定日也按「当天剩余容量」分配, 不再强制视为满容量 1.0。
                //   旧实现会在已有窗口外占用的锚定日再塞 1.0 人天, 凭空造出第 2 个人天 →
                //   多次拖拽后出现 Hall 容量违规(2026-11-15 压力测试 6 组里 3 组超载)。
                //   钉住任务仍然「开始日固定在松手位置 + 先于所有普通任务装箱」, 只是当天
                //   拿不满时后续顺延(区间被撑长, 由 remark 说明), 不会挤掉别人的产能。
                double free = availableCapacity(used, day);
                if (free > EPS) {
                    double total = 0.0;
                    for (DragPlan p : group) {
                        if (p.remaining > EPS) {
                            total += Math.min(p.remaining, 1.0);
                        }
                    }
                    for (DragPlan p : group) {
                        if (p.remaining <= EPS) {
                            continue;
                        }
                        double want = Math.min(p.remaining, 1.0);
                        consume(p, total <= free ? want : free * want / total, day);
                    }
                    used.merge(day, Math.min(total, free), Double::sum);
                }
                day = ScheduleDateUtils.getNextWorkday(day, config);
            }
            for (DragPlan p : group) {
                // 钉住 = 开始日固定为松手/锚定位置。锚定日容量被占满时任务会在后续日子才拿到
                // 产能, 但开始日仍是锚定日(区间因此被撑长并由 remark 说明), 不能悄悄往后挪。
                p.start = anchor;
                if (p.end == null) {
                    p.end = day;
                }
                if (p.end.isBefore(p.start)) {
                    p.end = p.start;
                }
            }
        }
        normals.sort(Comparator
                .comparing((DragPlan p) -> toLocalDate(p.row.getDevStartDate()),
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(p -> p.row.getReqNo(), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(p -> p.row.getId()));
        for (DragPlan p : normals) {
            p.start = findEarliestFeasibleStart(timelineStart, p.need, used, config);
            p.end = applyTaskLoad(p.start, p.need, used, config);
            p.remaining = 0.0;
        }
        // 极端兜底(所有工作日都被占满): 给未落位的任务一个确定的起止日, 避免空指针
        for (DragPlan p : plans.values()) {
            if (p.start == null) {
                p.start = timelineStart;
            }
            if (p.end == null) {
                p.end = p.start;
            }
        }

        // ===== 5) 判定「多任务挤占导致周期拉长」(说明是排期现状的派生结果, 不是拖拽瞬间的快照) =====
        // ★ 必须对同一人员的**全部**明细重算, 不能只算本次影响窗口内的任务: 窗口外任务的说明会因
        //   本次拖拽而过期(旧伙伴被拖走 / 新伙伴插进来), 不刷新就会留下「与 #X 挤占」但两者区间
        //   根本不重叠的脏说明(2026-11-15 库中实测: #2430 残留 #5、#5 残留 #6)。
        // ★ 伙伴名单按「最终区间是否有公共有效工作日」判定, 不再依赖「拖拽瞬间谁参与重排」这类快照:
        //   早期实现只记录当次重排任务, 会漏记窗口外真正占住那天的任务(如 #7), 也会残留已被拖走的伙伴。
        // 判定基准是工时对应的工作日数 ceil(need); 表中被拉长过的区间不作为基准, 所以拖回时能还原并清掉说明
        Map<String, Set<LocalDate>> finalDays = new HashMap<>();
        for (PmDevSchedule t : all) {
            DragPlan p = plans.get(t.getId());
            LocalDate s = p != null ? p.start : toLocalDate(t.getDevStartDate());
            LocalDate e = p != null ? p.end : toLocalDate(t.getDevEndDate());
            if (s != null && e != null) {
                finalDays.put(t.getId(), new HashSet<>(workdaysBetween(s, e, config)));
            }
        }
        Map<String, String> remarkById = new HashMap<>();
        int stretched = 0;
        for (PmDevSchedule t : all) {
            // 窗口外且原本没有说明的历史行: 默认保持无说明, 避免一次拖拽给大批无关明细
            // 批量写上「挤占」说明并刷新其 update_time。但若该行确实被拉长、且与本次重排后的
            // 任一任务区间有公共工作日, 说明它正处在挤占链上(如 2026-11-15 实测的 #24),
            // 仍要补写说明, 否则会出现「周期被拉长却无任何标注」的不一致。
            if (!plans.containsKey(t.getId()) && StringUtils.isEmpty(t.getRemark())) {
                Set<LocalDate> ownDays = finalDays.get(t.getId());
                int ownNeed = Math.max(1, (int) Math.ceil(needOf(t.getWorkload()) - EPS));
                boolean inCrowd = false;
                if (ownDays != null && ownDays.size() > ownNeed) {
                    for (DragPlan p : plans.values()) {
                        Set<LocalDate> planDays = finalDays.get(p.row.getId());
                        if (planDays != null && !java.util.Collections.disjoint(ownDays, planDays)) {
                            inCrowd = true;
                            break;
                        }
                    }
                }
                if (!inCrowd) {
                    remarkById.put(t.getId(), null);
                    continue;
                }
            }
            Set<LocalDate> own = finalDays.get(t.getId());
            if (own == null || own.isEmpty()) {
                remarkById.put(t.getId(), null);
                continue;
            }
            double need = needOf(t.getWorkload());
            int needDays = Math.max(1, (int) Math.ceil(need - EPS));
            if (own.size() <= needDays) {
                remarkById.put(t.getId(), null); // 周期正常 -> 清掉历史说明
                continue;
            }
            Set<String> partners = new LinkedHashSet<>();
            for (PmDevSchedule other : all) {
                if (Objects.equals(other.getId(), t.getId())) {
                    continue;
                }
                Set<LocalDate> otherDays = finalDays.get(other.getId());
                if (otherDays == null) {
                    continue;
                }
                boolean overlap = false;
                for (LocalDate d : own) {
                    if (otherDays.contains(d)) {
                        overlap = true;
                        break;
                    }
                }
                if (overlap) {
                    partners.add(taskLabel(other));
                }
            }
            remarkById.put(t.getId(), String.format(
                    "周期拉长：与 %s 挤占同一时间段，多任务并行分摊每日产能导致本任务被撑长"
                            + "（按工时 %.1f 人天本应 %d 个工作日，实际跨度 %d 个工作日）",
                    partners.isEmpty() ? "其它并行任务" : String.join("、", partners),
                    need, needDays, own.size()));
            stretched++;
        }

        // ===== 6) 批量落库(仅写有变化的行; remark 用 Wrapper 显式 set null 才能清掉旧值) =====
        int moved = 0;
        for (PmDevSchedule t : all) {
            DragPlan p = plans.get(t.getId());
            String remark = remarkById.get(t.getId());
            if (p == null) {
                // 窗口外任务: 日期与钉住状态一律不动, 只刷新可能过期的「周期拉长」说明
                if (Objects.equals(remark, t.getRemark())) {
                    continue;
                }
                update(new LambdaUpdateWrapper<PmDevSchedule>()
                        .eq(PmDevSchedule::getId, t.getId())
                        .set(PmDevSchedule::getRemark, remark)
                        .set(PmDevSchedule::getUpdateTime, now));
                t.setRemark(remark);
                moved++;
                continue;
            }
            p.remark = remark;
            boolean changed = p.pin
                    || !p.start.equals(toLocalDate(p.row.getDevStartDate()))
                    || !p.end.equals(toLocalDate(p.row.getDevEndDate()))
                    || !Objects.equals(p.remark, p.row.getRemark());
            if (!changed) {
                continue;
            }
            LambdaUpdateWrapper<PmDevSchedule> uw = new LambdaUpdateWrapper<PmDevSchedule>()
                    .eq(PmDevSchedule::getId, p.row.getId())
                    .set(PmDevSchedule::getDevStartDate, java.sql.Date.valueOf(p.start))
                    .set(PmDevSchedule::getDevEndDate, java.sql.Date.valueOf(p.end))
                    .set(PmDevSchedule::getRemark, p.remark)
                    .set(PmDevSchedule::getUpdateTime, now);
            if (p.pin) {
                uw.set(PmDevSchedule::getIsPinned, true)
                        .set(PmDevSchedule::getPinReason, p.pinReason);
            }
            update(uw);
            moved++;
            // 回写内存对象, 保证返回的 row 与库一致
            p.row.setDevStartDate(java.sql.Date.valueOf(p.start));
            p.row.setDevEndDate(java.sql.Date.valueOf(p.end));
            p.row.setRemark(p.remark);
            if (p.pin) {
                p.row.setIsPinned(true);
                p.row.setPinReason(p.pinReason);
            }
        }

        DragPlan dragged = plans.get(row.getId());
        String msg = "已调整至 " + ScheduleDateUtils.FORMATTER.format(dragged.start)
                + " ~ " + ScheduleDateUtils.FORMATTER.format(dragged.end) + "(已自动钉住)"
                + (moved > 0 ? "，重排 " + moved + " 项任务" : "")
                + (stretched > 0 ? "，当前 " + stretched + " 项任务处于挤占拉长状态" : "") + "!";
        return Result.ok(row, msg);
    }

    @Override
    public Result<PmDevSchedule> togglePin(ReqDevSchedulePin req) {
        PmDevSchedule row = getById(req.getId());
        if (row == null) {
            return Result.fail("错误:排期明细不存在!");
        }
        boolean pinned = !Boolean.FALSE.equals(req.getPinned());
        row.setIsPinned(pinned);
        row.setPinReason(pinned
                ? (StringUtils.isEmpty(req.getPinReason()) ? "手动钉住" : req.getPinReason())
                : null);
        row.setUpdateTime(new Date());
        if (!updateById(row)) {
            return Result.fail("错误:钉住状态更新失败!");
        }
        return Result.ok(row, pinned ? "已钉住, 一键重算将保持不动" : "已取消钉住, 重算时参与重新排期!");
    }

    @Override
    public Result<Integer> deleteByReqIds(List<String> reqIds) {
        if (reqIds == null || reqIds.isEmpty()) {
            return Result.ok(0, "无排期明细需要清理!");
        }
        // 本表不走逻辑删除(@TableLogic已移除): delete 直接物理删除, 避免重算堆积幽灵行
        // 物理删除前限定本租户, 防止跨租户排期明细被误清
        int count = baseMapper.delete(new LambdaQueryWrapper<PmDevSchedule>()
                .in(PmDevSchedule::getReqId, reqIds)
                .eq(PmDevSchedule::getTenantId, AuthInfoUtils.getCurrentTenantId()));
        return Result.ok(count, "已清理 " + count + " 条排期明细!");
    }

    /**
     * 查询同一人员的其它排期明细(拖拽重排的作用范围)。
     * 身份感知: 行带 personId 时按账号匹配(区分同租户同名两人); 行无 personId(历史姓名行)
     * 时按姓名匹配且限定 person_id 为 null。显式带 tenant_id，不依赖 @DataScope 拦截器兜底。
     */
    private List<PmDevSchedule> listSamePersonRows(PmDevSchedule row) {
        LambdaQueryWrapper<PmDevSchedule> q = new LambdaQueryWrapper<PmDevSchedule>()
                .ne(PmDevSchedule::getId, row.getId())
                .eq(PmDevSchedule::getTenantId, AuthInfoUtils.getCurrentTenantId());
        if (StringUtils.isNotEmpty(row.getPersonId())) {
            q.eq(PmDevSchedule::getPersonId, row.getPersonId());
        } else {
            q.eq(PmDevSchedule::getPersonName, row.getPersonName())
                    .isNull(PmDevSchedule::getPersonId);
        }
        return list(q.orderByAsc(PmDevSchedule::getDevStartDate)
                .orderByAsc(PmDevSchedule::getReqNo));
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    /** 表中 [start,end] 区间内的有效工作日列表(跳过周末及配置休假日) */
    private List<LocalDate> workdaysBetween(LocalDate start, LocalDate end, ScheduleConfigDTO config) {
        List<LocalDate> days = new ArrayList<>();
        if (start == null || end == null || end.isBefore(start)) {
            return days;
        }
        LocalDate d = ScheduleDateUtils.getWorkdayStartDate(start, config);
        int guard = 0;
        while (!d.isAfter(end) && guard++ < 4000) {
            days.add(d);
            d = ScheduleDateUtils.getNextWorkday(d, config);
        }
        return days;
    }

    /** 从 start 起往后数 n 个有效工作日 */
    private LocalDate advanceWorkdays(LocalDate start, int n, ScheduleConfigDTO config) {
        LocalDate d = start;
        for (int i = 0; i < n; i++) {
            d = ScheduleDateUtils.getNextWorkday(d, config);
        }
        return d;
    }

    /** ★ 任务应消耗的产能 = 表中 workload(人天); 非正值兜底为 1 人天 */
    private double needOf(BigDecimal workload) {
        if (workload == null) {
            return 1.0;
        }
        double w = workload.doubleValue();
        return w > EPS ? w : 1.0;
    }

    private boolean anyRemaining(List<DragPlan> group) {
        for (DragPlan p : group) {
            if (p.remaining > EPS) {
                return true;
            }
        }
        return false;
    }

    /** 当天剩余可用产能(每人每天 1 人天), 已占用超出 1 时按 0 处理 */
    private double availableCapacity(Map<LocalDate, Double> used, LocalDate day) {
        return Math.max(0.0, 1.0 - used.getOrDefault(day, 0.0));
    }

    /** 窗口外/历史行按工时均摊登记占用(与 autoSchedule.spreadRowLoad 同口径) */
    private void registerSpreadLoad(Map<LocalDate, Double> used, LocalDate start, LocalDate end,
                                    double workload, ScheduleConfigDTO config) {
        List<LocalDate> workdays = workdaysBetween(start, end, config);
        if (workdays.isEmpty()) {
            return;
        }
        double perDay = workload / workdays.size();
        for (LocalDate d : workdays) {
            used.merge(d, perDay, Double::sum);
        }
    }

    /** 容量试算: 从 from 起找最早可开工日, 首日必须实际获得容量(杜绝任务虚挂在满载日) */
    private LocalDate findEarliestFeasibleStart(LocalDate from, double need,
                                                Map<LocalDate, Double> used, ScheduleConfigDTO config) {
        LocalDate candidate = ScheduleDateUtils.getWorkdayStartDate(from, config);
        LocalDate limit = candidate.plusYears(3);
        while (!candidate.isAfter(limit)) {
            double remaining = need;
            LocalDate cursor = candidate;
            boolean first = true;
            boolean feasible = false;
            for (int guard = 0; guard < 4000 && remaining > EPS; guard++) {
                double available = availableCapacity(used, cursor);
                if (first && available <= EPS) {
                    break;
                }
                remaining -= available;
                if (remaining <= EPS) {
                    feasible = true;
                    break;
                }
                first = false;
                cursor = ScheduleDateUtils.getNextWorkday(cursor, config);
            }
            if (feasible) {
                return candidate;
            }
            candidate = ScheduleDateUtils.getNextWorkday(candidate, config);
        }
        return from;
    }

    /** 容量登记: 从 start 起逐有效工作日吃掉剩余产能直到工时耗尽, 返回实际触达的末日 */
    private LocalDate applyTaskLoad(LocalDate start, double need,
                                    Map<LocalDate, Double> used, ScheduleConfigDTO config) {
        double remaining = need;
        LocalDate cursor = ScheduleDateUtils.getWorkdayStartDate(start, config);
        LocalDate lastTouched = cursor;
        for (int guard = 0; guard < 4000 && remaining > EPS; guard++) {
            double available = availableCapacity(used, cursor);
            if (available > EPS) {
                double take = Math.min(remaining, available);
                used.merge(cursor, take, Double::sum);
                remaining -= take;
                lastTouched = cursor;
            }
            cursor = ScheduleDateUtils.getNextWorkday(cursor, config);
        }
        return lastTouched;
    }

    /** 在 day 给任务分配 take 人天产能, 记录首/末工作日 */
    private void consume(DragPlan p, double take, LocalDate day) {
        if (take <= EPS) {
            return;
        }
        p.remaining -= take;
        if (p.start == null) {
            p.start = day;
        }
        if (p.remaining <= EPS) {
            p.remaining = 0.0;
            p.end = day;
        }
    }

    /** tooltip 中展示的任务标识 */
    private String taskLabel(PmDevSchedule t) {
        return t.getReqNo() == null ? t.getId() : "#" + t.getReqNo();
    }

    /** 一次拖拽中某条明细的重排计划 */
    private static final class DragPlan {
        final PmDevSchedule row;
        /** 按工时本应消耗的人天数 */
        final double need;
        double remaining;
        /** 固定开始日(钉住任务) / 可前移的时间轴起点(普通任务) */
        final LocalDate anchor;
        /** 开始日是否被钉住 */
        final boolean pinned;
        /** 是否需要写入 isPinned=true(仅被拖任务) */
        final boolean pin;
        final String pinReason;
        LocalDate start;
        LocalDate end;
        String remark;

        DragPlan(PmDevSchedule row, double need, LocalDate anchor, boolean pinned, boolean pin, String pinReason) {
            this.row = row;
            this.need = need;
            this.remaining = need;
            this.anchor = anchor;
            this.pinned = pinned;
            this.pin = pin;
            this.pinReason = pinReason;
        }
    }
}

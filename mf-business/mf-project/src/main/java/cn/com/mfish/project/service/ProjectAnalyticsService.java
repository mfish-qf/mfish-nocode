package cn.com.mfish.project.service;

import cn.com.mfish.project.dict.PmDictCodes;
import cn.com.mfish.project.dict.PmDictService;
import cn.com.mfish.project.dto.ProjectProgressVO;
import cn.com.mfish.project.dto.WeeklyReportVO;
import cn.com.mfish.project.entity.PmIteration;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.mapper.PmIterationMapper;
import cn.com.mfish.project.mapper.PmRequirementMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: 项目进度汇总服务
 * 用单次投影查询(只取聚合所需列)替代"拉全量需求列表再让 LLM 自己汇总"，
 * 把需求/迭代的明细大字段排除在返回之外，从源头压低 AI 分析的 token 消耗。
 * 租户隔离由调用方(AnalyticsController)的 @DataScopes 注解注入 WHERE tenant_id 实现。
 * @author: mfish
 * @date: 2026-11-03
 * @version: V2.4.1
 */
@Service
public class ProjectAnalyticsService {

    @Resource
    private PmRequirementMapper requirementMapper;
    @Resource
    private PmIterationMapper iterationMapper;
    @Resource
    private PmDictService pmDictService;

    /**
     * 获取按月份聚焦的项目进度汇总
     *
     * @param month 统计月份(yyyy-MM)，为空时取当前月
     * @return 紧凑的进度汇总视图
     */
    public ProjectProgressVO getMonthlyProgress(String month) {
        YearMonth ym = parseMonth(month);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();
        Date monthStartDt = java.sql.Date.valueOf(monthStart);
        Date monthEndDt = java.sql.Date.valueOf(monthEnd);
        Date now = new Date();

        // 1) 拉取全部迭代的管理周期[startDate,endDate]，圈定"周期落到本月"的版本
        //    规则：迭代周期 [startDate,endDate] 与本月 [monthStart,monthEnd] 存在交集即纳入
        List<PmIteration> allIterations = iterationMapper.selectList(
                new LambdaQueryWrapper<PmIteration>()
                        .select(PmIteration::getId, PmIteration::getName, PmIteration::getCode,
                                PmIteration::getStatus, PmIteration::getStartDate, PmIteration::getEndDate)
                        .eq(PmIteration::getDelFlag, 0));
        Set<String> monthIterationIds = new HashSet<>();
        List<PmIteration> monthIterations = new ArrayList<>();
        for (PmIteration it : allIterations) {
            if (it.getStartDate() == null) {
                continue; // 连开始日期都没有，无法判定是否落到本月，跳过
            }
            Date s = it.getStartDate();
            Date e = it.getEndDate();
            boolean overlaps;
            if (e == null) {
                // 未设结束日期(进行中/规划中)：只要开始日期不晚于本月末，即视为本月活跃项目
                overlaps = !s.after(monthEndDt);
            } else {
                // 管理周期 [startDate,endDate] 与本月 [monthStart,monthEnd] 存在交集即纳入
                overlaps = !s.after(monthEndDt) && !e.before(monthStartDt);
            }
            if (overlaps) {
                monthIterationIds.add(it.getId());
                monthIterations.add(it);
            }
        }

        // 2) 投影查询需求，仅保留"本月版本"下的需求，杜绝全量历史需求混入
        List<PmRequirement> allReqs = requirementMapper.selectList(
                new LambdaQueryWrapper<PmRequirement>()
                        .select(PmRequirement::getId, PmRequirement::getIterationId,
                                PmRequirement::getStatus, PmRequirement::getDevEndDate,
                                PmRequirement::getWorkload)
                        .eq(PmRequirement::getDelFlag, 0));
        List<PmRequirement> reqs = allReqs.stream()
                .filter(r -> r.getIterationId() != null && monthIterationIds.contains(r.getIterationId()))
                .collect(Collectors.toList());

        // 状态字典：value -> label（按字典顺序保证输出稳定）
        Map<String, String> statusLabel = new LinkedHashMap<>();
        for (var item : pmDictService.items(PmDictCodes.REQUIREMENT_STATUS)) {
            statusLabel.put(String.valueOf(item.getDictValue()), item.getDictLabel());
        }

        // 状态分布 + 总工时 + 本月交付 + 逾期（均只统计本月版本下的需求）
        Map<String, Integer> statusDistribution = new LinkedHashMap<>();
        BigDecimal totalWorkload = BigDecimal.ZERO;
        int monthlyDeliveries = 0;
        int overdueRequirements = 0;
        for (PmRequirement r : reqs) {
            String label = statusLabel.getOrDefault(r.getStatus(), r.getStatus());
            statusDistribution.merge(label, 1, Integer::sum);
            if (r.getWorkload() != null) {
                totalWorkload = totalWorkload.add(r.getWorkload());
            }
            Date end = r.getDevEndDate();
            if (end != null) {
                if (!end.before(monthStartDt) && !end.after(monthEndDt)) {
                    monthlyDeliveries++;
                }
                Integer progress = pmDictService.statusProgress(r.getStatus());
                boolean done = progress != null && progress >= 100;
                if (end.before(now) && !done) {
                    overdueRequirements++;
                }
            }
        }

        // 各"本月版本"进度
        List<ProjectProgressVO.IterationProgress> iterationVOs = new ArrayList<>();
        for (PmIteration it : monthIterations) {
            List<PmRequirement> inIter = reqs.stream()
                    .filter(r -> it.getId().equals(r.getIterationId()))
                    .collect(Collectors.toList());
            int total = inIter.size();
            int done = (int) inIter.stream()
                    .filter(r -> {
                        Integer p = pmDictService.statusProgress(r.getStatus());
                        return p != null && p >= 100;
                    })
                    .count();
            double rate = total == 0 ? 0d : Math.round((double) done / total * 100.0) / 100.0;
            iterationVOs.add(new ProjectProgressVO.IterationProgress()
                    .setId(it.getId())
                    .setName(it.getName())
                    .setStatus(statusLabel.getOrDefault(it.getStatus(), it.getStatus()))
                    .setRequirementTotal(total)
                    .setRequirementDone(done)
                    .setCompletionRate(rate));
        }

        return new ProjectProgressVO()
                .setMonth(ym.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .setGeneratedAt(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now))
                .setIncludedIterationCount(monthIterations.size())
                .setTotalRequirements(reqs.size())
                .setStatusDistribution(statusDistribution)
                .setMonthlyDeliveries(monthlyDeliveries)
                .setOverdueRequirements(overdueRequirements)
                .setTotalWorkload(totalWorkload)
                .setIterations(iterationVOs);
    }

    /**
     * 获取本周(周一~周日, 北京时间)项目周报汇总
     * 返回各迭代版本进度 + 进行中需求明细(进度<100%, 上限150) + 下周计划,
     * 供 AI 按"本周工作/下周工作"格式生成周报，从源头压低 token 消耗。
     *
     * @return 紧凑的周报视图
     */
    public WeeklyReportVO weeklyReport() {
        ZoneId zone = ZoneId.of("Asia/Shanghai");
        LocalDate today = LocalDate.now(zone);
        int dow = today.getDayOfWeek().getValue(); // 1=周一
        LocalDate weekStart = today.minusDays(dow - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        Date now = new Date();

        // 单次投影查询：只取周报所需列，排除 PRD 文本/人员大字段
        List<PmRequirement> reqs = requirementMapper.selectList(
                new LambdaQueryWrapper<PmRequirement>()
                        .select(PmRequirement::getId, PmRequirement::getIterationId,
                                PmRequirement::getStatus, PmRequirement::getDevEndDate,
                                PmRequirement::getDevProgress, PmRequirement::getWorkload,
                                PmRequirement::getModule, PmRequirement::getLevel1Feature,
                                PmRequirement::getLevel2Feature, PmRequirement::getLevel3Feature,
                                PmRequirement::getFeatureDescription)
                        .eq(PmRequirement::getDelFlag, 0));

        Map<String, String> statusLabel = new LinkedHashMap<>();
        for (var item : pmDictService.items(PmDictCodes.REQUIREMENT_STATUS)) {
            statusLabel.put(String.valueOf(item.getDictValue()), item.getDictLabel());
        }

        // 圈定"管理周期覆盖本周或下周"的版本(与月度进度口径对齐：版本周期落入统计窗口即纳入)；
        // 无开始日期 / 周期与 [本周一,下周日] 无交集的版本一律排除，避免历史已发布版本、过期需求混入周报。
        Date weekStartDt = java.sql.Date.valueOf(weekStart);
        Date nextWeekEndDt = java.sql.Date.valueOf(weekEnd.plusDays(7)); // 下周日 = 本周日 + 7
        List<PmIteration> iterations = iterationMapper.selectList(
                new LambdaQueryWrapper<PmIteration>()
                        .select(PmIteration::getId, PmIteration::getName, PmIteration::getCode,
                                PmIteration::getStatus, PmIteration::getStartDate, PmIteration::getEndDate)
                        .eq(PmIteration::getDelFlag, 0));
        Set<String> weekIterIds = new HashSet<>();
        List<PmIteration> weekIterations = new ArrayList<>();
        for (PmIteration it : iterations) {
            if (it.getStartDate() == null) {
                continue; // 无开始日期无法判定周期，跳过
            }
            Date s = it.getStartDate();
            Date e = it.getEndDate();
            boolean overlaps;
            if (e == null) {
                // 进行中/规划中(无结束日期)：开始不晚于下周末即视为本周/下周活跃项目
                overlaps = !s.after(nextWeekEndDt);
            } else {
                // 管理周期 [startDate,endDate] 与窗口 [本周一,下周日] 存在交集即纳入
                overlaps = !s.after(nextWeekEndDt) && !e.before(weekStartDt);
            }
            if (overlaps) {
                weekIterIds.add(it.getId());
                weekIterations.add(it);
            }
        }

        // 需求只保留圈定版本(无版本归属/已删除版本的需求无管理周期，无法判定落入本周/下周，与月报口径一致不纳入)
        reqs = reqs.stream()
                .filter(r -> r.getIterationId() != null && weekIterIds.contains(r.getIterationId()))
                .collect(Collectors.toList());

        List<WeeklyReportVO.IterationWeekly> iterVOs = new ArrayList<>();
        List<WeeklyReportVO.RequirementBrief> active = new ArrayList<>();
        BigDecimal totalActiveWorkload = BigDecimal.ZERO;
        for (PmIteration it : weekIterations) {
            List<PmRequirement> inIter = reqs.stream()
                    .filter(r -> it.getId().equals(r.getIterationId()))
                    .collect(Collectors.toList());
            int total = inIter.size();
            int done = 0;
            int transferred = 0;
            for (PmRequirement r : inIter) {
                int prog = progressOf(r);
                if (prog >= 100) {
                    done++;
                }
                if (isTransferredToTest(r)) {
                    transferred++;
                }
                if (prog < 100) {
                    active.add(buildBrief(r, it, statusLabel, prog));
                    if (r.getWorkload() != null) {
                        totalActiveWorkload = totalActiveWorkload.add(r.getWorkload());
                    }
                }
            }
            double rate = total == 0 ? 0d : Math.round((double) done / total * 100.0) / 100.0;
            iterVOs.add(new WeeklyReportVO.IterationWeekly()
                    .setId(it.getId())
                    .setName(it.getName())
                    .setCode(it.getCode())
                    .setStatus(statusLabel.getOrDefault(it.getStatus(), it.getStatus()))
                    .setRequirementTotal(total)
                    .setRequirementDone(done)
                    .setTransferredToTest(transferred)
                    .setCompletionRate(rate));
        }

        // 进行中需求按计划完成日期升序(最紧急在前), 上限 150 条, 防止 token 爆炸
        final int MAX_ACTIVE = 150;
        active.sort((a, b) -> {
            if (a.getPlanEndDate() == null && b.getPlanEndDate() == null) return 0;
            if (a.getPlanEndDate() == null) return 1;
            if (b.getPlanEndDate() == null) return -1;
            return a.getPlanEndDate().compareTo(b.getPlanEndDate());
        });
        if (active.size() > MAX_ACTIVE) {
            active = new ArrayList<>(active.subList(0, MAX_ACTIVE));
        }

        // 下周计划：由"未结束(非终止阶段)且完成率<100%"的迭代推导
        List<String> nextWeekPlan = new ArrayList<>();
        for (WeeklyReportVO.IterationWeekly it : iterVOs) {
            String phase = pmDictService.statusPhase(it.getStatus());
            boolean terminal = PmDictCodes.PHASE_TERMINAL.equals(phase);
            if (!terminal && it.getCompletionRate() < 1.0) {
                String label = (it.getCode() != null && !it.getCode().isEmpty()) ? it.getCode() : it.getName();
                nextWeekPlan.add("开展" + label + "相关需求开发工作");
            }
        }

        return new WeeklyReportVO()
                .setWeekStart(weekStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .setWeekEnd(weekEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .setGeneratedAt(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now))
                .setIterations(iterVOs)
                .setActiveRequirements(active)
                .setNextWeekPlan(nextWeekPlan)
                .setWorkload(new WeeklyReportVO.WorkloadSummary()
                        .setTotalPersonDays(totalActiveWorkload)
                        .setActiveRequirementCount(active.size()));
    }

    private int progressOf(PmRequirement r) {
        if (r.getDevProgress() != null) {
            return r.getDevProgress();
        }
        Integer p = pmDictService.statusProgress(r.getStatus());
        return p != null ? p : 0;
    }

    private boolean isTransferredToTest(PmRequirement r) {
        return PmDictCodes.PHASE_POST.equals(pmDictService.statusPhase(r.getStatus()));
    }

    private WeeklyReportVO.RequirementBrief buildBrief(PmRequirement r, PmIteration it,
                                                      Map<String, String> statusLabel, int prog) {
        String planEnd = r.getDevEndDate() != null
                ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(r.getDevEndDate()) : null;
        String iterName = it != null ? it.getName() : "未分配版本";
        String iterCode = it != null ? it.getCode() : "";
        return new WeeklyReportVO.RequirementBrief()
                .setName(reqName(r))
                .setIterationName(iterName)
                .setIterationCode(iterCode)
                .setStatus(statusLabel.getOrDefault(r.getStatus(), r.getStatus()))
                .setProgress(prog)
                .setTransferredToTest(isTransferredToTest(r))
                .setPlanEndDate(planEnd);
    }

    private String reqName(PmRequirement r) {
        StringBuilder sb = new StringBuilder();
        if (r.getModule() != null && !r.getModule().isEmpty()) {
            sb.append(r.getModule()).append(" ");
        }
        Stream.of(r.getLevel1Feature(), r.getLevel2Feature(), r.getLevel3Feature())
                .filter(f -> f != null && !f.isEmpty())
                .forEach(f -> sb.append(f).append("/"));
        String base = sb.toString().replaceAll("/$", "").trim();
        if (r.getFeatureDescription() != null && !r.getFeatureDescription().isEmpty()) {
            String desc = r.getFeatureDescription().length() > 40
                    ? r.getFeatureDescription().substring(0, 40) + "…" : r.getFeatureDescription();
            base = base.isEmpty() ? desc : base + " - " + desc;
        }
        return base.isEmpty() ? "(未命名需求)" : base;
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month.trim(), DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            return YearMonth.now();
        }
    }
}

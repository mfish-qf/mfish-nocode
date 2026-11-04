package cn.com.mfish.project.service;

import cn.com.mfish.project.dict.PmDictCodes;
import cn.com.mfish.project.dict.PmDictService;
import cn.com.mfish.project.dto.ProjectProgressVO;
import cn.com.mfish.project.dto.WeeklyReportVO;
import cn.com.mfish.project.entity.PmDevSchedule;
import cn.com.mfish.project.entity.PmIteration;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.mapper.PmDevScheduleMapper;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private PmDevScheduleMapper devScheduleMapper;
    @Resource
    private PmDictService pmDictService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** 单版本「本周完成/本周推进」功能点短名上限，控制返回体 token */
    private static final int MAX_WEEK_FEATURES = 20;

    /** 单版本「整体内容主题」上限，控制返回体 token */
    private static final int MAX_SCOPE_THEMES = 12;

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
     * 获取本周(周一~周日, 北京时间)项目周报汇总。
     * 两个关键口径严格分离，供 AI 分别写"本周主要完成"与"主要包括"：
     * 1) weekCompletedFeatures / weekOngoingFeatures：仅来自 pm_dev_schedule 落在[本周一,本周日]的排期需求；
     * 2) scopeThemes：该版本全部需求按模块·一级功能聚合的内容主题(不限本周排期)。
     * 返回各迭代版本进度 + 进行中需求明细(进度<100%, 上限150) + 下周计划, 从源头压低 token 消耗。
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

        // 本周排期：取排期计划表(pm_dev_schedule)中落在[本周一,本周日]的行，按需求聚合。
        // 仅作为"本周工作"的事实依据：已在本周前完成(排期结束日<本周一)的需求不会出现，从而自然排除历史工作。
        Map<String, List<PmDevSchedule>> schedByReq = buildWeekScheduleMap(reqs, weekStartDt,
                java.sql.Date.valueOf(weekEnd));

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
            // 本周该版本下被排期的功能点(按需求聚合)：只统计本周有排期行的需求，一条需求=一个功能点
            List<WeeklyReportVO.WeekFeature> weekFeatures = new ArrayList<>();
            // 「本周主要完成 / 本周主要推进」两个口径都只来自 pm_dev_schedule 落在本周的排期需求，
            // 由后端直接分组，避免 AI 自行判断状态把"待评审"写成"已完成"。
            LinkedHashSet<String> weekCompleted = new LinkedHashSet<>();
            LinkedHashSet<String> weekOngoing = new LinkedHashSet<>();
            BigDecimal weekWorkload = BigDecimal.ZERO;
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
                // 本周排期功能点：该需求本周有排期行才纳入
                List<PmDevSchedule> rows = schedByReq.get(r.getId());
                if (rows != null && !rows.isEmpty()) {
                    BigDecimal w = rows.stream().map(PmDevSchedule::getWorkload)
                            .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
                    weekWorkload = weekWorkload.add(w);
                    String fname = featureName(r);
                    weekFeatures.add(new WeeklyReportVO.WeekFeature()
                            .setFeatureName(fname)
                            .setTransferredToTest(isTransferredToTest(r))
                            .setProgress(prog));
                    boolean finished = prog >= 100 || isTransferredToTest(r);
                    if (finished) {
                        if (weekCompleted.size() < MAX_WEEK_FEATURES) {
                            weekCompleted.add(fname);
                        }
                    } else if (weekOngoing.size() < MAX_WEEK_FEATURES) {
                        weekOngoing.add(fname);
                    }
                }
            }
            // 本周功能点按涉及工时(功能点粒度近似用进度无关, 这里按出现顺序即可)——为控制 token, 单版本上限 40 个
            final int MAX_FEAT = 40;
            if (weekFeatures.size() > MAX_FEAT) {
                weekFeatures = new ArrayList<>(weekFeatures.subList(0, MAX_FEAT));
            }
            // 「主要包括」用的是整个版本的内容(全部需求按模块·一级功能聚合), 与本周排期无关，
            // 避免周报里出现"本周主要完成X，主要包括X"这种同义反复。
            List<String> scopeThemes = buildScopeThemes(inIter);
            double rate = total == 0 ? 0d : Math.round((double) done / total * 100.0) / 100.0;
            iterVOs.add(new WeeklyReportVO.IterationWeekly()
                    .setId(it.getId())
                    .setName(it.getName())
                    .setCode(it.getCode())
                    .setStatus(statusLabel.getOrDefault(it.getStatus(), it.getStatus()))
                    .setRequirementTotal(total)
                    .setRequirementDone(done)
                    .setTransferredToTest(transferred)
                    .setCompletionRate(rate)
                    .setStartDate(it.getStartDate() != null ? DATE_FMT.format(it.getStartDate().toInstant().atZone(zone).toLocalDate()) : null)
                    .setEndDate(it.getEndDate() != null ? DATE_FMT.format(it.getEndDate().toInstant().atZone(zone).toLocalDate()) : null)
                    .setWeekFeatures(weekFeatures)
                    .setWeekCompletedFeatures(new ArrayList<>(weekCompleted))
                    .setWeekOngoingFeatures(new ArrayList<>(weekOngoing))
                    .setScopeThemes(scopeThemes)
                    .setWeekWorkload(weekWorkload));
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

        // 下周计划：由"未结束(非终止阶段)且完成率<100%"的迭代推导(保证本周工作涉及的版本下周仍会列出)
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

    /**
     * 构建"本周排期"映射：取排期计划表(pm_dev_schedule)中落在[本周一,本周日]的行，按需求ID聚合。
     * 只统计本周实际排了期的工作，已在本周前完成(排期结束日<本周一)的需求不会出现，从而自然排除历史工作。
     */
    private Map<String, List<PmDevSchedule>> buildWeekScheduleMap(List<PmRequirement> reqs,
                                                                 Date weekStartDt, Date weekEndDt) {
        List<String> reqIds = reqs.stream().map(PmRequirement::getId)
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (reqIds.isEmpty()) {
            return Map.of();
        }
        List<PmDevSchedule> schedules = devScheduleMapper.selectList(
                new LambdaQueryWrapper<PmDevSchedule>()
                        .select(PmDevSchedule::getReqId, PmDevSchedule::getWorkload,
                                PmDevSchedule::getDevStartDate, PmDevSchedule::getDevEndDate)
                        .in(PmDevSchedule::getReqId, reqIds)
                        .le(PmDevSchedule::getDevStartDate, weekEndDt)
                        .ge(PmDevSchedule::getDevEndDate, weekStartDt));
        return schedules.stream()
                .filter(s -> s.getReqId() != null)
                .collect(Collectors.groupingBy(PmDevSchedule::getReqId));
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
        List<String> parts = new ArrayList<>();
        if (r.getModule() != null && !r.getModule().trim().isEmpty()) {
            parts.add(r.getModule().trim());
        }
        Stream.of(r.getLevel1Feature(), r.getLevel2Feature(), r.getLevel3Feature())
                .filter(f -> f != null && !f.trim().isEmpty())
                .forEach(f -> parts.add(f.trim()));
        String base = String.join("/", parts);
        if (r.getFeatureDescription() != null && !r.getFeatureDescription().trim().isEmpty()) {
            String desc = r.getFeatureDescription().trim();
            desc = desc.length() > 40 ? desc.substring(0, 40) + "…" : desc;
            base = base.isEmpty() ? desc : base + " - " + desc;
        }
        return base.isEmpty() ? "(未命名需求)" : base;
    }

    /**
     * 取"去噪短功能名"：优先取最具体的功能点层级(三级→二级→一级)，再退到功能描述、模块。
     * 会剔除数据里的 "/" 等纯符号占位段与 "新增/修改" 等纯动作词，并去掉 "--163定制" 尾部噪声，
     * 避免周报出现 "竞赛保障、/、我的项目" 这类脏值。
     */
    private static final java.util.Set<String> GENERIC_ACTION =
            java.util.Set.of("新增", "修改", "优化", "调整", "完善", "修复", "删除", "配置", "查询", "列表");
    private String featureName(PmRequirement r) {
        String[] levels = {r.getLevel3Feature(), r.getLevel2Feature(), r.getLevel1Feature()};
        for (String lv : levels) {
            String s = cleanFeatureSegment(lv);
            if (s != null) {
                return s;
            }
        }
        String desc = cleanFeatureSegment(r.getFeatureDescription());
        if (desc != null) {
            return desc.length() > 40 ? desc.substring(0, 40) + "…" : desc;
        }
        String module = cleanFeatureSegment(r.getModule());
        return module != null ? module : "(未命名功能)";
    }

    /**
     * 版本整体内容主题：由该版本【全部需求】(不限本周排期)按「模块·一级功能」聚合，
     * 按覆盖需求数降序取前 MAX_SCOPE_THEMES 个。供 AI 归纳"主要包括"，
     * 与"本周主要完成"(仅本周排期需求)严格区分，避免两句罗列同一批功能点。
     */
    private List<String> buildScopeThemes(List<PmRequirement> inIter) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (PmRequirement r : inIter) {
            String module = cleanFeatureSegment(r.getModule());
            String level = firstValidLevel(r.getLevel1Feature(), r.getLevel2Feature(), r.getLevel3Feature());
            String theme;
            if (module != null && level != null && !module.equals(level)) {
                theme = module + "·" + level;
            } else {
                theme = module != null ? module : level;
            }
            if (theme != null) {
                counts.merge(theme, 1, Integer::sum);
            }
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(MAX_SCOPE_THEMES)
                .map(e -> {
                    String t = e.getKey();
                    return t.length() > 24 ? t.substring(0, 24) + "…" : t;
                })
                .collect(Collectors.toList());
    }

    /** 取第一个有效层级(一级→二级→三级)：某级的 "/" 占位会自动跳过并降级取下一层 */
    private String firstValidLevel(String... levels) {
        for (String lv : levels) {
            String s = cleanFeatureSegment(lv);
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    private String cleanFeatureSegment(String s) {
        if (s == null) {
            return null;
        }
        // 去掉尾部 --数字定制 噪声(数据里常见 "负载感知路由--163定制")
        s = s.replaceAll("--\\d*定制\\s*$", "").trim();
        if (s.isEmpty()) {
            return null;
        }
        // 库里 level2/level3 大量用 "/" "-" 等纯符号占位；纯动作词也不是功能名，一律丢弃
        if (s.matches("[^\\p{L}\\p{N}]+") || GENERIC_ACTION.contains(s)) {
            return null;
        }
        return s;
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

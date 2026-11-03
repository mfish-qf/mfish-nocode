package cn.com.mfish.project.controller;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.DataScopes;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.common.DataScopeType;
import cn.com.mfish.project.dto.ProjectProgressVO;
import cn.com.mfish.project.dto.WeeklyReportVO;
import cn.com.mfish.project.service.ProjectAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 项目数据汇总分析(供 AI 分析与看板概览)
 * 与 /requirement/all 拉全量明细相反，本控制器只返回聚合后的紧凑指标，
 * 显著减少 AI 分析时的 token 消耗，避免触发模型 TPM 限流。
 * @author: mfish
 * @date: 2026-11-03
 * @version: V2.4.1
 */
@Slf4j
@Tag(name = "项目汇总分析")
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Resource
    private ProjectAnalyticsService analyticsService;

    /**
     * 项目进度汇总(按月份聚焦)
     * 口径：仅统计"管理周期[startDate,endDate]与本月有交集"的版本(迭代)下的需求，
     * 不会把全量历史需求混入。返回这些本月项目的状态分布/完成率/工时/交付/逾期等紧凑指标。
     */
    @Operation(summary = "项目进度汇总-按月(版本周期落到本月)", description = "仅统计管理周期[startDate,endDate]与本月有交集、或已开始且未设结束日期(进行中/规划中)的版本下的需求, 返回状态分布/迭代完成率/总工时/本月交付/逾期等紧凑指标, 供AI分析, 不含明细")
    @DataScopes({
            @DataScope(table = "pm_requirement", type = DataScopeType.Tenant),
            @DataScope(table = "pm_iteration", type = DataScopeType.Tenant),
            @DataScope(table = "pm_dev_schedule", type = DataScopeType.Tenant)
    })
    @GetMapping("/project-progress")
    @RequiresPermissions("project:requirement:query")
    public Result<ProjectProgressVO> projectProgress(
            @Parameter(name = "month", description = "统计月份 yyyy-MM, 为空取当前月")
            @RequestParam(required = false) String month) {
        return Result.ok(analyticsService.getMonthlyProgress(month), "项目进度汇总-查询成功!");
    }

    /**
     * 项目周报数据汇总(本周)
     * 返回本周(周一至周日)各迭代版本进度 / 进行中需求明细 / 下周计划等紧凑数据，
     * 供 AI 按"本周工作 / 下周工作"格式生成周报，不含全量明细。
     */
    @Operation(summary = "项目周报数据汇总-本周", description = "返回本周(周一至周日)各迭代版本进度/进行中需求明细/下周计划等紧凑数据, 供AI生成周报, 不含全量明细")
    @DataScopes({
            @DataScope(table = "pm_requirement", type = DataScopeType.Tenant),
            @DataScope(table = "pm_iteration", type = DataScopeType.Tenant),
            @DataScope(table = "pm_dev_schedule", type = DataScopeType.Tenant)
    })
    @GetMapping("/weekly-report")
    @RequiresPermissions("project:requirement:query")
    public Result<WeeklyReportVO> weeklyReport() {
        return Result.ok(analyticsService.weeklyReport(), "项目周报数据汇总-查询成功!");
    }
}

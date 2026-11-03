package cn.com.mfish.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 项目周报数据汇总视图对象
 * 专为 AI 生成"本周周报"设计：只暴露"本周(周一~周日)各迭代版本进度 + 进行中需求明细 + 下周计划"
 * 的紧凑结构，不含任何 PRD 文本/人员大字段，从源头压低 AI 分析的 token 消耗。
 * 数据口径与人工编写的周报一致：版本号→迭代 code/name；功能点→需求名称；
 * "已转测"→需求处于转测/测试/验收/上线阶段(progress=100%)；"当前进度"→完成率/开发进度。
 * @author: mfish
 * @date: 2026-11-03
 * @version: V2.4.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "项目周报数据汇总(供AI生成周报, 紧凑无明细)")
public class WeeklyReportVO {

    @Schema(description = "本周起始(周一, yyyy-MM-dd)")
    private String weekStart;

    @Schema(description = "本周结束(周日, yyyy-MM-dd)")
    private String weekEnd;

    @Schema(description = "汇总生成时间(yyyy-MM-dd HH:mm:ss)")
    private String generatedAt;

    @Schema(description = "各迭代版本进度(含需求总数/已完成数/已转测数/完成率)")
    private List<IterationWeekly> iterations;

    @Schema(description = "进行中需求明细(进度<100%), 按计划完成日期升序, 上限150条, 供AI组织'本周工作'条目")
    private List<RequirementBrief> activeRequirements;

    @Schema(description = "下周工作重点(由进行中/规划中迭代推导, 供AI组织'下周工作'条目)")
    private List<String> nextWeekPlan;

    @Schema(description = "工作量概览(仅统计进行中需求)")
    private WorkloadSummary workload;

    @Data
    @Accessors(chain = true)
    @Schema(description = "单个迭代版本周报进度")
    public static class IterationWeekly {
        @Schema(description = "迭代版本ID")
        private String id;
        @Schema(description = "迭代版本名称")
        private String name;
        @Schema(description = "版本号/代号(如 v1.3.8)")
        private String code;
        @Schema(description = "迭代状态名称")
        private String status;
        @Schema(description = "绑定需求总数")
        private int requirementTotal;
        @Schema(description = "已完成需求数(进度=100%)")
        private int requirementDone;
        @Schema(description = "已转测需求数(处于待转测/测试中/验收中/已上线阶段)")
        private int transferredToTest;
        @Schema(description = "完成率(0-1, 两位小数)")
        private double completionRate;
        @Schema(description = "版本管理周期开始日(yyyy-MM-dd, 可能为空)")
        private String startDate;
        @Schema(description = "版本管理周期结束日(yyyy-MM-dd, 可能为空)")
        private String endDate;
        @Schema(description = "本周该版本下被排期(落在[本周一,本周日])的功能点清单(按需求聚合, 已去噪的短功能名)。"
                + "为空表示本周该版本无排期任务(版本仍需作为一条出现在本周工作中, 但不写'本周主要完成')")
        private List<WeekFeature> weekFeatures;
        @Schema(description = "本周该版本涉及工时合计(人天, 该版本所有本周排期行工时累加)")
        private BigDecimal weekWorkload;
    }

    @Data
    @Accessors(chain = true)
    @Schema(description = "本周排期涉及的功能点(已按需求聚合, 去噪短名, 不含模块前缀与空段斜杠)")
    public static class WeekFeature {
        @Schema(description = "功能点名称(去噪短名, 如 负载感知路由 / 运营监测分析)")
        private String featureName;
        @Schema(description = "是否已转测(处于转测/测试/验收/上线阶段)")
        private boolean transferredToTest;
        @Schema(description = "当前进度百分比(0-100)")
        private int progress;
    }

    @Data
    @Accessors(chain = true)
    @Schema(description = "单个进行中需求简报")
    public static class RequirementBrief {
        @Schema(description = "需求名称(模块 + 一/二/三级功能点 + 功能点描述 组合)")
        private String name;
        @Schema(description = "归属版本名称")
        private String iterationName;
        @Schema(description = "归属版本号")
        private String iterationCode;
        @Schema(description = "研发状态名称")
        private String status;
        @Schema(description = "当前进度百分比(0-100, 优先取开发进度, 缺失时取状态默认进度)")
        private int progress;
        @Schema(description = "是否已转测(处于转测/测试/验收/上线阶段)")
        private boolean transferredToTest;
        @Schema(description = "计划完成日期(yyyy-MM-dd, 可能为空)")
        private String planEndDate;
    }

    @Data
    @Accessors(chain = true)
    @Schema(description = "工作量概览")
    public static class WorkloadSummary {
        @Schema(description = "进行中需求总预估工时(人天)")
        private BigDecimal totalPersonDays;
        @Schema(description = "进行中需求数")
        private long activeRequirementCount;
    }
}

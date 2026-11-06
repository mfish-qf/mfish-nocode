package cn.com.mfish.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description: 项目进度汇总视图对象
 * 专为 AI 分析 / 看板概览设计：只暴露聚合后的紧凑指标，不含任何需求/迭代明细大字段，
 * 避免把全量列表回灌到 LLM 上下文，从而把单轮 token 消耗从数万压到数千。
 * @author: mfish
 * @date: 2026-11-03
 * @version: V2.4.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "项目进度汇总(供AI分析/看板概览, 紧凑无明细)")
public class ProjectProgressVO {

    @Schema(description = "统计月份(yyyy-MM)")
    private String month;

    @Schema(description = "汇总生成时间(yyyy-MM-dd HH:mm:ss)")
    private String generatedAt;

    @Schema(description = "纳入本月统计的版本(迭代)数：其管理周期[startDate,endDate]与本月有交集、或已开始且未设结束日期的版本")
    private int includedIterationCount;

    @Schema(description = "本月项目需求总数：仅统计管理周期落到本月的版本下的需求(未删除)")
    private int totalRequirements;

    @Schema(description = "需求状态分布: 状态名称 -> 数量(按字典顺序)")
    private Map<String, Integer> statusDistribution;

    @Schema(description = "本月计划交付需求数(排期明细 MAX(dev_end_date) 落在统计月内)")
    private int monthlyDeliveries;

    @Schema(description = "已逾期未完成需求数(排期明细 MAX(dev_end_date) 早于当前时间且进度<100%)")
    private int overdueRequirements;

    @Schema(description = "总预估工时(人天)")
    private BigDecimal totalWorkload;

    @Schema(description = "各迭代版本进度明细")
    private List<IterationProgress> iterations;

    @Data
    @Accessors(chain = true)
    @Schema(description = "单个迭代版本进度")
    public static class IterationProgress {
        @Schema(description = "迭代版本ID")
        private String id;
        @Schema(description = "迭代版本名称")
        private String name;
        @Schema(description = "状态名称")
        private String status;
        @Schema(description = "绑定需求总数")
        private int requirementTotal;
        @Schema(description = "已完成需求数(进度=100%)")
        private int requirementDone;
        @Schema(description = "完成率(0-1, 两位小数)")
        private double completionRate;
    }
}

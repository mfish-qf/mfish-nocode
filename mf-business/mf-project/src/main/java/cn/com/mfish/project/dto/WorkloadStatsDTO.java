package cn.com.mfish.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @description: 开发人员工时负载统计 DTO
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "开发人员工时负载统计")
public class WorkloadStatsDTO {
    @Schema(description = "人员姓名")
    private String name;
    @Schema(description = "显示全称")
    private String displayName;
    @Schema(description = "岗位角色编码")
    private String role;
    @Schema(description = "中文岗位名")
    private String roleName;
    @Schema(description = "部门")
    private String department;
    @Schema(description = "职称头衔")
    private String title;
    @Schema(description = "总承接需求数")
    private Integer requirementCount;
    @Schema(description = "总工时数(人天)")
    private BigDecimal totalWorkload;
    @Schema(description = "交付百分比(0-100)")
    private BigDecimal deliveryPercent;
    @Schema(description = "团队平均工时偏差差值(人天)")
    private BigDecimal avgWorkloadDeviation;
    @Schema(description = "健康负荷诊断状态(轻载/正常/饱和/过载)")
    private String healthStatus;
}

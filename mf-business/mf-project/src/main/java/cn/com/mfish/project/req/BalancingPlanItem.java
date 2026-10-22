package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 智能负载均衡调配计划项
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Schema(description = "智能负载均衡调配计划项")
public class BalancingPlanItem {
    @Schema(description = "需求ID")
    @NotBlank(message = "需求ID不能为空")
    private String reqId;
    @Schema(description = "原人员")
    @NotBlank(message = "原人员不能为空")
    private String fromPerson;
    @Schema(description = "目标人员")
    @NotBlank(message = "目标人员不能为空")
    private String toPerson;
    @Schema(description = "岗位角色(frontend前端/backend后端)")
    @NotBlank(message = "岗位角色不能为空")
    private String role;
    @Schema(description = "需求工时(人天)")
    private BigDecimal workload;
    @Schema(description = "需求标题/功能名")
    private String reqTitle;
}

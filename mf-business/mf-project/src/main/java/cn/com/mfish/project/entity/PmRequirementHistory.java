package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 需求调配与状态流转审计日志表
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@TableName("pm_requirement_history")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_requirement_history对象 需求调配与状态流转审计日志")
public class PmRequirementHistory extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "需求ID")
    private String reqId;
    @Schema(description = "需求序号")
    private Integer reqNo;
    @Schema(description = "操作类型(REASSIGN人员调配/STATUS状态流转/BALANCING负载均衡/WORKLOAD工时调整/BATCH批量操作)")
    private String actionType;
    @Schema(description = "岗位角色(frontend/backend/qa/owner)")
    private String role;
    @Schema(description = "原人员")
    private String fromPerson;
    @Schema(description = "目标人员")
    private String toPerson;
    @Schema(description = "变更前内容")
    private String oldValue;
    @Schema(description = "变更后内容")
    private String newValue;
    @Schema(description = "操作详细说明")
    private String detail;
    @Schema(description = "删除标志(0正常 1已删除)")
    @TableLogic
    private Integer delFlag;
    @Schema(description = "备注")
    private String remark;
}

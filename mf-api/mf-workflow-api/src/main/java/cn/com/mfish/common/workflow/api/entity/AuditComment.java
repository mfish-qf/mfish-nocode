package cn.com.mfish.common.workflow.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 审批意见
 * @author: mfish
 * @date: 2025/9/18
 */
@Data
@Accessors(chain = true)
@Schema(description = "审批意见")
public class AuditComment {
    @Schema(description = "任务id")
    private String taskId;
    @Schema(description = "流程实例id")
    private String processInstanceId;
    @Schema(description = "流程定义id")
    private String processDefinitionId;
    @Schema(description = "任务名称")
    private String name;
    @Schema(description = "审批人")
    private String assignee;
    @Schema(description = "审批意见")
    private String comment;
    @Schema(description = "审批时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
}

package cn.com.mfish.common.workflow.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @description: 任务
 * @author: mfish
 * @date: 2025/9/17
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务")
public class MfTask {
    @Schema(description = "任务id")
    private String id;
    @Schema(description = "流程实例id")
    private String processInstanceId;
    @Schema(description = "流程定义id")
    private String processDefinitionId;
    @Schema(description = "流程定义key")
    private String processDefinitionKey;
    @Schema(description = "任务名称")
    private String name;
    @Schema(description = "流程名称")
    private String processName;
    @Schema(description = "业务key")
    private String businessKey;
    @Schema(description = "任务描述")
    private String description;
    @Schema(description = "候选用户")
    private List<String> candidateUsers;
    @Schema(description = "候选用户组")
    private List<String> candidateGroups;
    @Schema(description = "表单key")
    private String formKey;
    @Schema(description = "任务负责人")
    private String assignee;
    @Schema(description = "发起人账号")
    private String startAccount;
    @Schema(description = "任务开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @Schema(description = "任务结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @Schema(description = "任务状态")
    private String status;
    @Schema(description = "删除原因")
    private String deleteReason;
    @Schema(description = "审批意见")
    private List<AuditComment> comments;
}

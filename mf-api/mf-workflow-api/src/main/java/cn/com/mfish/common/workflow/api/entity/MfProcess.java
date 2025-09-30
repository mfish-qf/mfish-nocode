package cn.com.mfish.common.workflow.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 流程实例
 * @author: mfish
 * @date: 2025/9/19
 */
@Data
@Accessors(chain=true)
@Schema(description = "流程实例")
public class MfProcess {
    @Schema(description = "流程定义key")
    private String processDefinitionKey;
    @Schema(description = "流程定义名称")
    private String processDefinitionName;
    @Schema(description = "流程定义id")
    private String processDefinitionId;
    @Schema(description = "部署id")
    private String deploymentId;
    @Schema(description = "流程实例id")
    private String processInstanceId;
    @Schema(description = "业务id")
    private String businessKey;
    @Schema(description = "启动用户id")
    private String startUserId;
    @Schema(description = "启动时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @Schema(description = "结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    @Schema(description = "删除原因")
    private String deleteReason;

}

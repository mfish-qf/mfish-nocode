package cn.com.mfish.common.workflow.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 审核信息
 * @author: mfish
 * @date: 2025/9/24
 */
@Data
@Accessors(chain = true)
@Schema(description = "审核信息")
public class ApproveInfo {
    @Schema(description = "任务id")
    private String taskId;
    @Schema(description = "审核意见")
    private String message;
}

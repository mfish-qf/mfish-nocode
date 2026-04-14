package cn.com.mfish.common.workflow.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 流程定义
 * @author: mfish
 * @date: 2026/4/14
 */
@Data
@Schema(description = "流程定义")
@Accessors(chain = true)
public class FlowDefinition {
    @Schema(description = "流程定义ID")
    private String id;
    @Schema(description = "流程定义Key")
    private String flowKey;
    @Schema(description = "流程定义版本")
    private Integer version;
}

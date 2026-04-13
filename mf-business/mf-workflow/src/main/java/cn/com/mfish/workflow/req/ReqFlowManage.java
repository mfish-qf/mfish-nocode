package cn.com.mfish.workflow.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 流程管理
 * @author: mfish
 * @date: 2026-03-30
 * @version: V2.3.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "流程管理请求参数")
public class ReqFlowManage {
    @Schema(description = "流程key")
    private String flowKey;
    @Schema(description = "流程名称")
    private String name;
    @Schema(description = "是否发布（0未发布 1已发布）")
    private Short released;
}

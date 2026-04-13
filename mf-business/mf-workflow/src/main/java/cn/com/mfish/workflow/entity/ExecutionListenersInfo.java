package cn.com.mfish.workflow.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "节点监听器")
public class ExecutionListenersInfo {
    @Schema(description = "事件")
    private String event;
    @Schema(description = "实现类型")
    private String type;
    @Schema(description = "实现类")
    private String value;
}

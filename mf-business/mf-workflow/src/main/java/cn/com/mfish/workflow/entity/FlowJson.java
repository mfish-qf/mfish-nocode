package cn.com.mfish.workflow.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @description: 根对象
 * @author: mfish
 * @date: 2026/3/24
 */
@Data
@Schema(description = "流程 JSON 根对象", name = "FlowJson")
public class FlowJson {
    @Schema(description = "节点列表")
    private List<FlowNode> nodes;
    @Schema(description = "连线列表")
    private List<FlowEdge> edges;
}

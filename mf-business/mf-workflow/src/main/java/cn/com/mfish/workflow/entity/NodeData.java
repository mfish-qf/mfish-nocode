package cn.com.mfish.workflow.entity;

import cn.com.mfish.workflow.enums.NodeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @description: 节点业务数据
 * @author: mfish
 * @date: 2026/3/24
 */
@Data
@Schema(description = "节点数据", name = "NodeData")
public class NodeData {
    @Schema(description = "节点类型")
    private NodeType type;
    @Schema(description = "节点标签")
    private String label;
    @Schema(description = "节点描述")
    private String description;
    @Schema(description = "指定审批人")
    private String assignee;
    @Schema(description = "候选用户列表")
    private String candidateUsers;
    @Schema(description = "候选组列表")
    private String candidateGroups;
    @Schema(description = "审批类型（OR 或签，AND 会签）")
    private String approvalType;
    @Schema(description = "执行监听器列表")
    private List<ExecutionListenersInfo> executionListeners;
    @Schema(description = "完成百分比")
    private Integer completionPercent;
    @Schema(description = "分支列表")
    private List<Branch> branches;
}
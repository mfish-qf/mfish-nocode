package cn.com.mfish.workflow.entity;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.workflow.nodes.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description: 节点对象
 * @author: mfish
 * @date: 2026/3/24
 */
@Data
@Schema(description = "流程节点对象", name = "FlowNode")
public class FlowNode {
    @Schema(description = "节点 ID")
    private String id;
    @Schema(description = "节点业务数据")
    private NodeData data;

    public BaseNode create() {
        BaseNode node;
        switch (data.getType()) {
            case START:
                node = new StartNode(id, data.getLabel(), data.getDescription());
                break;
            case END:
                node = new EndNode(id, data.getLabel(), data.getDescription());
                break;
            case APPROVAL:
                node = new ApprovalNode(id, data.getLabel(), data.getDescription());
                ApprovalNode approvalNode = (ApprovalNode) node;
                approvalNode.setCandidateUsers(data.getCandidateUsers());
                approvalNode.setCandidateGroups(data.getCandidateGroups());
                approvalNode.setApprovalType(data.getApprovalType());
                approvalNode.setCompletionPercent(data.getCompletionPercent());
                approvalNode.setAssignee(data.getAssignee());
                break;
            case CONDITION:
                node = new ExclusiveNode(id, data.getLabel(), data.getDescription(), data.getBranches());
                break;
            default:
                throw new MyRuntimeException("错误:未知的节点类型 " + data.getType());
        }
        node.setExecutionListeners(data.getExecutionListeners());
        return node;
    }
}
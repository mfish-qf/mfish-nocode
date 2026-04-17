package cn.com.mfish.workflow.entity;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.workflow.nodes.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
                approvalNode.setCandidateUsers(data.getUserIds());
                List<String> groupList = new ArrayList<>();
                if (data.getOrgIds() != null && !data.getOrgIds().isEmpty()) {
                    groupList.addAll(data.getOrgIds().stream().map(orgId -> "org:" + orgId).toList());
                }
                if (data.getRoleIds() != null && !data.getRoleIds().isEmpty()) {
                    groupList.addAll(data.getRoleIds().stream().map(roleId -> "role:" + roleId).toList());
                }
                approvalNode.setCandidateGroups(groupList);
                approvalNode.setApprovalType(data.getApprovalType());
                approvalNode.setCompletionPercent(data.getCompletionPercent());
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
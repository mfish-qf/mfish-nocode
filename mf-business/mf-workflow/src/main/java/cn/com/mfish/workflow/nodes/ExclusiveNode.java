package cn.com.mfish.workflow.nodes;

import cn.com.mfish.workflow.entity.Branch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;

import java.util.List;

/**
 * @description: 排他网关节点（条件分支）
 * 用于根据条件表达式选择不同的流程路径，只有一个分支会被执行
 * @author: mfish
 * @date: 2026/4/2
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "排他网关节点")
@Data
public class ExclusiveNode extends BaseNode {
    @Schema(description = "以当前网关为源节点的线条列表")
    private List<Branch> branches;

    public ExclusiveNode(String id, String name, String documentation, List<Branch> branches) {
        super(id, name, documentation);
        this.setBranches(branches);
    }

    @Override
    protected FlowNode create(List<SequenceFlow> sequenceFlows) {
        ExclusiveGateway gateway = new ExclusiveGateway();
        for (Branch branch : branches) {
            if (branch.getId().startsWith("else_")) {
                gateway.setDefaultFlow(branch.getId());
                break;
            }
        }
        return gateway;
    }
}

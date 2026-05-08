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

    /**
     * 排他网关节点构造方法
     *
     * @param id            节点ID
     * @param name          节点名称
     * @param documentation 节点说明
     * @param branches      分支列表
     */
    public ExclusiveNode(String id, String name, String documentation, List<Branch> branches) {
        super(id, name, documentation);
        this.setBranches(branches);
    }

    /**
     * 创建排他网关节点
     * 自动查找 else 分支并设置为默认流转路径
     *
     * @param sequenceFlows 所有连线列表
     * @return 排他网关节点
     */
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

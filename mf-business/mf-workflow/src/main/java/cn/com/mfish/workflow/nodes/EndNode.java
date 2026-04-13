package cn.com.mfish.workflow.nodes;

import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;

import java.util.List;

/**
 * @description: 结束节点
 * @author: mfish
 * @date: 2026/4/2
 */

public class EndNode extends BaseNode {
    public EndNode(String id, String name, String documentation) {
        super(id, name, documentation);
    }

    @Override
    protected FlowNode create(List<SequenceFlow> sequenceFlows) {
        return new EndEvent();
    }
}

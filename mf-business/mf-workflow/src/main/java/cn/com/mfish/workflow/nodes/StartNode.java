package cn.com.mfish.workflow.nodes;

import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;

import java.util.List;

/**
 * @description: 开始节点
 * @author: mfish
 * @date: 2026/4/2
 */
public class StartNode extends BaseNode {
    public StartNode(String id, String name, String documentation) {
        super(id, name, documentation);
    }

    @Override
    protected FlowNode create(List<SequenceFlow> sequenceFlows) {
        return new StartEvent();
    }
}

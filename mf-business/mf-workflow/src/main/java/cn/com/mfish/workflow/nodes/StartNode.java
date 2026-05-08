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
    /**
     * 开始节点构造方法
     *
     * @param id            节点ID
     * @param name          节点名称
     * @param documentation 节点说明
     */
    public StartNode(String id, String name, String documentation) {
        super(id, name, documentation);
    }

    /**
     * 创建开始事件节点
     *
     * @param sequenceFlows 所有连线列表
     * @return 开始事件节点
     */
    @Override
    protected FlowNode create(List<SequenceFlow> sequenceFlows) {
        return new StartEvent();
    }
}

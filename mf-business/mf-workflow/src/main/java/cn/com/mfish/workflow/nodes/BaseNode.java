package cn.com.mfish.workflow.nodes;

import cn.com.mfish.workflow.entity.ExecutionListenersInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.SequenceFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 基础节点
 * @author: mfish
 * @date: 2026/4/2
 */
@Data
public abstract class BaseNode {
    @Schema(description = "节点ID")
    private final String id;
    @Schema(description = "节点名称")
    private final String name;
    @Schema(description = "节点说明")
    private final String documentation;
    @Schema(description = "执行监听器列表")
    private List<ExecutionListenersInfo> executionListeners;

    public BaseNode(String id, String name, String documentation) {
        this.id = id;
        this.name = name;
        this.documentation = documentation;
    }

    /**
     * 创建节点
     *
     * @param sequenceFlows 所有连线列表
     * @return FlowNode 节点
     */
    protected abstract FlowNode create(List<SequenceFlow> sequenceFlows);

    /**
     * 创建节点
     *
     * @param sequenceFlows 所有连线列表
     * @return FlowNode
     */
    public FlowNode createNode(List<SequenceFlow> sequenceFlows) {
        FlowNode node = create(sequenceFlows);
        // 设置节点 ID（必须确保与连线中的 source/target 引用一致）
        node.setId(this.id);
        node.setName(this.name);
        node.setDocumentation(this.documentation);
        // 建立节点与连线的关联关系
        for (SequenceFlow flow : sequenceFlows) {
            if (flow.getSourceRef().equals(id)) {
                // 该节点是连线的源节点，添加到节点的流出列表
                node.getOutgoingFlows().add(flow);
            }
            if (flow.getTargetRef().equals(id)) {
                // 该节点是连线的目标节点，添加到节点的流入列表
                node.getIncomingFlows().add(flow);
            }
        }
        // 处理执行监听器 (Execution Listeners)
        // 监听器可在节点的生命周期事件（start/end）触发指定的 Java 类或表达式
        if (executionListeners != null && !executionListeners.isEmpty()) {
            List<FlowableListener> listeners = new ArrayList<>();
            for (ExecutionListenersInfo listenersInfo : executionListeners) {
                FlowableListener fl = new FlowableListener();
                fl.setEvent(listenersInfo.getEvent());           // 事件类型：start, end
                fl.setImplementationType(listenersInfo.getType()); // 实现类型：class, expression, delegateExpression
                fl.setImplementation(listenersInfo.getValue());    // 具体实现值
                listeners.add(fl);
            }
            node.setExecutionListeners(listeners);
        }
        return node;
    }

}

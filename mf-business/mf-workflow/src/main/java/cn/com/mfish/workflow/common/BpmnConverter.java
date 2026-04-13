package cn.com.mfish.workflow.common;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.workflow.entity.FlowEdge;
import cn.com.mfish.workflow.entity.FlowJson;
import cn.com.mfish.workflow.entity.FlowNode;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * BPMN 转换器
 * 将前端流程设计器的 JSON 数据转换为 Flowable BPMN 2.0 XML 模型
 *
 * @author mfish
 * @date 2026/3/24
 */
@Slf4j
public class BpmnConverter {

    /**
     * 将前端流程设计器的 JSON 字符串转换为 BPMN 模型
     *
     * @param flowKey     流程定义 Key
     * @param name        流程名称
     * @param description 流程描述
     * @param jsonString  前端流程设计器生成的 JSON 字符串
     * @return Flowable BPMN 模型对象
     */
    public static BpmnModel convertToBpmn(String flowKey, String name, String description, String jsonString) {
        if (StringUtils.isEmpty(jsonString)) {
            throw new MyRuntimeException("错误：流程配置不能为空");
        }
        FlowJson flowJson;
        try {
            flowJson = JSON.parseObject(jsonString, FlowJson.class);
        } catch (Exception e) {
            throw new MyRuntimeException("错误：流程配置不正确");
        }
        return convertToBpmn(flowKey, name, description, flowJson);
    }

    /**
     * 将前端流程设计器的 JSON 对象转换为 BPMN 模型
     *
     * @param flowKey     流程定义 key
     * @param name        流程名称
     * @param description 流程描述
     * @param flowJson    前端流程设计器生成的 JSON 对象
     * @return Flowable BPMN 模型对象
     */
    public static BpmnModel convertToBpmn(String flowKey, String name, String description, FlowJson flowJson) {
        if (StringUtils.isEmpty(flowKey)) {
            throw new MyRuntimeException("错误：流程定义Key不能为空");
        }
        BpmnModel bpmnModel = new BpmnModel();
        Process process = new Process();
        process.setId(flowKey);
        process.setName(name);
        process.setDocumentation(description);
        bpmnModel.addProcess(process);
        // 存储所有连线，用于后续节点的流入流出关系处理
        List<SequenceFlow> sequenceFlows = new ArrayList<>();
        // 转换连线 (Edges) - 先处理连线，确保节点的流入流出关系正确
        for (FlowEdge edge : flowJson.getEdges()) {
            SequenceFlow sequenceFlow = edge.create();
            process.addFlowElement(sequenceFlow);
            sequenceFlows.add(sequenceFlow);
        }
        for (FlowNode node : flowJson.getNodes()) {
            FlowElement flowElement = node.create().createNode(sequenceFlows);
            process.addFlowElement(flowElement);
        }
        // 自动布局 - 使用 Flowable 提供的自动布局功能
        try {
            new BpmnAutoLayout(bpmnModel).execute();
        } catch (Exception e) {
            log.warn("自动布局异常：{}", e.getMessage(), e);
            throw new MyRuntimeException("错误：流程配置不正确");
        }
        return bpmnModel;
    }


}

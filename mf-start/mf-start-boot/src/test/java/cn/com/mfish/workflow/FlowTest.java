package cn.com.mfish.workflow;

import cn.com.mfish.workflow.common.BpmnConverter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileOutputStream;

/**
 * @description: 工作流测试
 * @author: mfish
 * @date: 2026/2/24
 */
@Slf4j
@SpringBootTest
@ComponentScan(basePackages = "cn.com.mfish")
public class FlowTest {
    @Test
    public void testFlow() {
        try {
            BpmnModel bpmnModel = BpmnConverter.convertToBpmn("screen_release", "屏幕发布流程", "屏幕发布流程",
                    """
                            {
                              "version": "1.0",
                              "timestamp": "2026-04-03T01:37:38.894Z",
                              "nodes": [
                                {
                                  "id": "startEvent1",
                                  "type": "custom",
                                  "draggable": true,
                                  "initialized": false,
                                  "position": {
                                    "x": 0,
                                    "y": 100
                                  },
                                  "data": {
                                    "type": "start",
                                    "label": "开始",
                                    "icon": "Play"
                                  },
                                  "label": "开始"
                                },
                                {
                                  "id": "node_approval_1775179346215",
                                  "type": "custom",
                                  "draggable": true,
                                  "initialized": false,
                                  "position": {
                                    "x": 390,
                                    "y": 100
                                  },
                                  "data": {
                                    "type": "approval",
                                    "label": "审批",
                                    "icon": "Activity",
                                    "assignee": "mfish",
                                    "candidateUsers": "",
                                    "candidateGroups": "",
                                    "approvalType": "OR"
                                  },
                                  "label": "审批"
                                },
                                {
                                  "id": "node_approval_1775179348412",
                                  "type": "custom",
                                  "draggable": true,
                                  "initialized": false,
                                  "position": {
                                    "x": 780,
                                    "y": 10
                                  },
                                  "data": {
                                    "type": "approval",
                                    "label": "审批",
                                    "icon": "Activity",
                                    "assignee": "",
                                    "candidateUsers": "admin",
                                    "candidateGroups": "",
                                    "approvalType": "OR"
                                  },
                                  "label": "审批"
                                },
                                {
                                  "id": "node_end_1775179350902",
                                  "type": "custom",
                                  "draggable": true,
                                  "initialized": false,
                                  "position": {
                                    "x": 1170,
                                    "y": 0
                                  },
                                  "data": {
                                    "type": "end",
                                    "label": "结束",
                                    "icon": "Activity",
                                    "executionListeners": [
                                      {
                                        "event": "start",
                                        "type": "class",
                                        "value": "cn.com.mfish.workflow.handler.CompleteCallbackHandler"
                                      }
                                    ]
                                  },
                                  "label": "结束"
                                },
                                {
                                  "id": "node_end_1775179357007",
                                  "type": "custom",
                                  "draggable": true,
                                  "initialized": false,
                                  "position": {
                                    "x": 1170,
                                    "y": 200
                                  },
                                  "data": {
                                    "type": "end",
                                    "label": "结束",
                                    "icon": "Activity",
                                    "executionListeners": [
                                      {
                                        "event": "start",
                                        "type": "class",
                                        "value": "cn.com.mfish.workflow.handler.CompleteCallbackHandler"
                                      }
                                    ]
                                  },
                                  "label": "结束"
                                }
                              ],
                              "edges": [
                                {
                                  "id": "e-startEvent1-right-node_approval_1775179346215-left",
                                  "type": "custom",
                                  "source": "startEvent1",
                                  "target": "node_approval_1775179346215",
                                  "sourceHandle": "right",
                                  "targetHandle": "left",
                                  "data": {
                                    "showArrow": true,
                                    "pathType": "default",
                                    "condition": null
                                  },
                                  "label": "",
                                  "animated": true,
                                  "style": {
                                    "stroke": "#EE4F12",
                                    "strokeWidth": 2
                                  },
                                  "markerEnd": {
                                    "type": "arrowclosed",
                                    "color": "#EE4F12"
                                  },
                                  "sourceX": 242.5,
                                  "sourceY": 132.375,
                                  "targetX": 387.5,
                                  "targetY": 162.86460876464844
                                },
                                {
                                  "id": "e-node_approval_1775179346215-right-node_approval_1775179348412-left",
                                  "type": "custom",
                                  "source": "node_approval_1775179346215",
                                  "target": "node_approval_1775179348412",
                                  "sourceHandle": "right",
                                  "targetHandle": "left",
                                  "data": {
                                    "showArrow": true,
                                    "pathType": "default",
                                    "condition": "approved"
                                  },
                                  "label": "",
                                  "animated": true,
                                  "style": {
                                    "stroke": "#EE4F12",
                                    "strokeWidth": 2
                                  },
                                  "markerEnd": {
                                    "type": "arrowclosed",
                                    "color": "#EE4F12"
                                  },
                                  "sourceX": 632.5,
                                  "sourceY": 162.86460876464844,
                                  "targetX": 777.5,
                                  "targetY": 72.86460876464844
                                },
                                {
                                  "id": "e-node_approval_1775179348412-right-node_end_1775179350902-left",
                                  "type": "custom",
                                  "source": "node_approval_1775179348412",
                                  "target": "node_end_1775179350902",
                                  "sourceHandle": "right",
                                  "targetHandle": "left",
                                  "data": {
                                    "showArrow": true,
                                    "pathType": "default",
                                    "condition": "approved"
                                  },
                                  "label": "",
                                  "animated": true,
                                  "style": {
                                    "stroke": "#EE4F12",
                                    "strokeWidth": 2
                                  },
                                  "markerEnd": {
                                    "type": "arrowclosed",
                                    "color": "#EE4F12"
                                  },
                                  "sourceX": 1022.5,
                                  "sourceY": 72.86460876464844,
                                  "targetX": 1167.5,
                                  "targetY": 32.375030517578125
                                },
                                {
                                  "id": "e-node_approval_1775179348412-bottom-node_end_1775179357007-top",
                                  "type": "custom",
                                  "source": "node_approval_1775179348412",
                                  "target": "node_end_1775179357007",
                                  "sourceHandle": "bottom",
                                  "targetHandle": "top",
                                  "data": {
                                    "showArrow": true,
                                    "pathType": "default",
                                    "condition": "rejected"
                                  },
                                  "label": "",
                                  "animated": true,
                                  "style": {
                                    "stroke": "#EE4F12",
                                    "strokeWidth": 2
                                  },
                                  "markerEnd": {
                                    "type": "arrowclosed",
                                    "color": "#EE4F12"
                                  },
                                  "sourceX": 900,
                                  "sourceY": 138.23959350585938,
                                  "targetX": 1290,
                                  "targetY": 200
                                },
                                {
                                  "id": "e-node_approval_1775179346215-bottom-node_end_1775179357007-left",
                                  "type": "custom",
                                  "source": "node_approval_1775179346215",
                                  "target": "node_end_1775179357007",
                                  "sourceHandle": "bottom",
                                  "targetHandle": "left",
                                  "data": {
                                    "showArrow": true,
                                    "pathType": "default",
                                    "condition": "rejected"
                                  },
                                  "label": "",
                                  "animated": true,
                                  "style": {
                                    "stroke": "#EE4F12",
                                    "strokeWidth": 2
                                  },
                                  "markerEnd": {
                                    "type": "arrowclosed",
                                    "color": "#EE4F12"
                                  },
                                  "sourceX": 510,
                                  "sourceY": 228.23959350585938,
                                  "targetX": 1167.5,
                                  "targetY": 232.37503051757812
                                }
                              ],
                              "position": [
                                286,
                                364
                              ],
                              "zoom": 0.8,
                              "viewport": {
                                "x": 286,
                                "y": 364,
                                "zoom": 0.8
                              }
                            }""");
            
            // 转换为 XML
            byte[] xml = new BpmnXMLConverter().convertToXML(bpmnModel);
            
            // 保存到桌面
            String desktopPath = System.getProperty("user.home") + "\\Desktop\\screen_release.bpmn20.xml";
            try (FileOutputStream fos = new FileOutputStream(desktopPath)) {
                fos.write(xml);
                log.info("✅ BPMN 文件已成功保存到：{}", desktopPath);
            }
            
        } catch (Exception e) {
            log.error("❌ 流程转换失败：{}", e.getMessage(), e);
        }
    }
}

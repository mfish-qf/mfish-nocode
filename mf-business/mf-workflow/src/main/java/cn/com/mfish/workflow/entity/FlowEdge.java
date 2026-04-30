package cn.com.mfish.workflow.entity;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.workflow.common.ConditionConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.flowable.bpmn.model.SequenceFlow;

/**
 * @description: 连线对象
 * @author: mfish
 * @date: 2026/3/24
 */
@Data
@Schema(description = "连线对象", name = "FlowEdge")
public class FlowEdge {
    private static final String APPROVED = "approved";
    private static final String REJECTED = "rejected";
    private static final String IF = "if_";
    private static final String ELSE = "else_";
    private static final String CASE = "case_";
    private static final String AUDIT_TYPE = "auditType";
    @Schema(description = "连线 ID")
    private String id;
    @Schema(description = "源节点 ID")
    private String source;
    @Schema(description = "目标节点 ID")
    private String target;
    @Schema(description = "源节点手柄")
    private String sourceHandle;
    @Schema(description = "目标节点手柄")
    private String targetHandle;
    @Schema(description = "连线业务数据")
    private EdgeData data;

    public SequenceFlow create() {
        SequenceFlow flow = new SequenceFlow(source, target);
        // BPMN ID 不能包含连字符，做简单替换处理
        flow.setId(id);
        // 处理连线上的条件表达式（如网关分支条件、审批结果条件等）
        String expression = buildConditionExpression();
        if (expression != null) {
            flow.setName(data != null ? data.getCondition().equals(APPROVED) ? "通过" : data.getCondition().equals(REJECTED) ? "拒绝" : expression : sourceHandle);
            flow.setConditionExpression("${ " + expression + " }");
        }
        return flow;
    }

    /**
     * 构建条件表达式
     * 根据连线类型生成对应的 BPMN 条件表达式
     *
     * @return 条件表达式字符串（如 "${auditType == 'approved'}"）
     */
    private String buildConditionExpression() {
        if (data == null || StringUtils.isBlank(data.getCondition())) {
            return null;
        }
        if (sourceHandle.startsWith(IF) || sourceHandle.startsWith(ELSE) || sourceHandle.startsWith(CASE)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Condition condition = mapper.readValue(data.getCondition(), Condition.class);
                String expr = ConditionConverter.convert(condition);
                // 去除首尾的括号
                if (expr.startsWith("(") && expr.endsWith(")")) {
                    expr = expr.substring(1, expr.length() - 1);
                }
                return expr;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return AUDIT_TYPE + " == '" + data.getCondition() + "'";
    }
}
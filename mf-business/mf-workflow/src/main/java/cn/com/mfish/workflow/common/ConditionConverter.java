package cn.com.mfish.workflow.common;

import cn.com.mfish.workflow.entity.Condition;
import cn.com.mfish.workflow.entity.ConditionGroup;
import cn.com.mfish.workflow.entity.SimpleCondition;

import java.util.stream.Collectors;

/**
 * @description: 条件转换
 * @author: mfish
 * @date: 2026/4/30
 */
public class ConditionConverter {
    /**
     * 将条件节点转换为字符串表达式
     *
     * @param node 条件节点
     * @return 字符串表达式
     */
    public static String convert(Condition node) {
        if (node instanceof SimpleCondition) {
            return convertSimple((SimpleCondition) node);
        }
        if (node instanceof ConditionGroup) {
            return convertGroup((ConditionGroup) node);
        }
        return "";
    }

    /**
     * 将条件组节点转换为字符串表达式
     *
     * @param node 条件组节点
     * @return 字符串表达式
     */
    private static String convertGroup(ConditionGroup node) {
        String logic = "and".equalsIgnoreCase(node.getLogic()) ? " && " : " || ";
        String expr = node.getChildren().stream()
                .map(ConditionConverter::convert)
                .collect(Collectors.joining(logic));
        return "(" + expr + ")";
    }

    /**
     * 将简单条件节点转换为字符串表达式
     *
     * @param node 简单条件节点
     * @return 字符串表达式
     */
    private static String convertSimple(SimpleCondition node) {
        String op = mapOperator(node.getOperator());
        String val = node.getValue().toString();
        // 如果是常量且为字符串，需要加引号
        if (!"variable".equalsIgnoreCase(node.getValueType()) && isString(node.getValue())) {
            val = "'" + val + "'";
        }

        return String.format("%s %s %s", node.getVariable(), op, val);
    }

    /**
     * 将操作符映射为字符串
     *
     * @param op 操作符
     * @return 字符串
     */
    private static String mapOperator(String op) {
        return switch (op) {
            case "eq" -> "==";
            case "ne" -> "!=";
            case "gt" -> ">";
            case "lt" -> "<";
            case "ge" -> ">=";
            case "le" -> "<=";
            default -> op;
        };
    }

    /**
     * 判断是否为字符串类型
     *
     * @param val 值
     * @return 是否为字符串类型
     */
    private static boolean isString(Object val) {
        if (!(val instanceof String)) {
            return false;
        }
        // 尝试解析为数字，如果失败则认为是字符串
        try {
            Double.parseDouble((String) val);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}

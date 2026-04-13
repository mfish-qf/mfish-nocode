package cn.com.mfish.workflow.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 流程节点类型枚举
 * 定义工作流设计中可用的节点类型
 * 
 * @author mfish
 * @date 2026/3/24
 */
@Getter
public enum NodeType {
    START("start", "开始事件", "流程的起始点"),
    END("end", "结束事件", "流程的终止点"),
    CONDITION("condition", "排他网关", "条件分支，用于根据条件选择不同路径"),
    APPROVAL("approval", "用户任务", "审批节点，需要用户进行处理");

    @JsonValue
    private final String type;
    private final String name;
    private final String description;

    NodeType(String type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    /**
     * 根据类型字符串获取对应的节点类型枚举
     * 
     * @param type 类型字符串
     * @return 对应的节点类型枚举，如果不存在则返回 null
     */
    public static NodeType fromType(String type) {
        if (type == null) {
            return null;
        }
        for (NodeType nodeType : values()) {
            if (nodeType.type.equals(type)) {
                return nodeType;
            }
        }
        return null;
    }

    /**
     * 判断给定的类型是否为有效的节点类型
     * 
     * @param type 类型字符串
     * @return true 表示有效，false 表示无效
     */
    public static boolean isValid(String type) {
        return fromType(type) != null;
    }

}

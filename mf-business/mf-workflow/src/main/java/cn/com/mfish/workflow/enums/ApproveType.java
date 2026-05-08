package cn.com.mfish.workflow.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @description: 审批类型枚举
 * 定义工作流审批节点的审批方式
 * @author: mfish
 * @date: 2026/4/2
 */
@Getter
public enum ApproveType {
    /** 会签：所有审批人必须审批通过 */
    AND("AND", "会签"),
    /** 或签：任意一人审批通过即可审批通过 */
    OR("OR", "或签");

    /** 审批类型标识 */
    @JsonValue
    private final String type;
    /** 审批类型描述 */
    private final String comment;

    /**
     * 审批类型构造方法
     *
     * @param type    审批类型标识
     * @param comment 审批类型描述
     */
    ApproveType(String type, String comment) {
        this.type = type;
        this.comment = comment;
    }

}

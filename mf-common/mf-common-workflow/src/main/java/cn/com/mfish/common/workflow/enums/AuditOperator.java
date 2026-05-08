package cn.com.mfish.common.workflow.enums;

import lombok.Getter;

/**
 * @description: 审批操作
 * @author: mfish
 * @date: 2025/9/17
 */
@Getter
public enum AuditOperator {
    审核通过("approved"),
    审核拒绝("rejected"),
    暂停("suspended"),
    终止("terminated"),
    取消("canceled");
    /**
     * 操作值
     */
    private final String value;

    AuditOperator(String value) {
        this.value = value;
    }

    /**
     * 根据操作值获取枚举
     *
     * @param value 操作值
     * @return 审批操作枚举，未匹配返回null
     */
    public static AuditOperator of(String value) {
        for (AuditOperator flowOperator : values()) {
            if (flowOperator.value.equals(value)) return flowOperator;
        }
        return null;
    }

}

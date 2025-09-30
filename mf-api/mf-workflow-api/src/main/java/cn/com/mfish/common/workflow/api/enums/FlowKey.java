package cn.com.mfish.common.workflow.api.enums;

import lombok.Getter;

/**
 * @description: 流程定义key
 * @author: mfish
 * @date: 2025/9/16
 */
@Getter
public enum FlowKey {
    UNKNOWN("unknown"),
    大屏发布("screen_release"),
    TEST("test");

    private final String key;

    FlowKey(String key) {
        this.key = key;
    }

    /**
     * 根据key获取枚举
     *
     * @param key 键
     * @return 枚举
     */
    public static FlowKey getByKey(String key) {
        for (FlowKey flowKey : values()) {
            if (flowKey.getKey().equals(key)) {
                return flowKey;
            }
        }
        return FlowKey.UNKNOWN;
    }

    @Override
    public String toString() {
        return this.key;
    }
}

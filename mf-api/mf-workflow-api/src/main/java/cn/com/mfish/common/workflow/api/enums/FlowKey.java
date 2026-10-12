package cn.com.mfish.common.workflow.api.enums;

import lombok.Getter;

/**
 * 流程定义key
 * <p>
 * callback/prefix 用于 AI 发起流程时回调业务系统更新单据状态：
 * <ul>
 *   <li>callback 非空：AI 启动流程时走 {@code startProcess}，流程结束后 {@code CompleteCallbackHandler}
 *       按 callback 回调业务系统，业务单据状态正常更新。</li>
 *   <li>callback 为空：AI 启动流程时走 {@code startProcessWithoutCallback}，不触发业务回调（纯自动化流程）。</li>
 * </ul>
 * 新增流程时，在此枚举追加一项并配置 callback/prefix（无业务回调则留空）。
 * </p>
 *
 * @author: mfish
 * @date: 2025/9/16
 */
@Getter
public enum FlowKey {
    /** 未知流程 */
    UNKNOWN("unknown", null, null),
    /** 大屏发布流程：回调 nocode 服务更新大屏资源状态 */
    大屏发布("screen_release",
            "cn.com.mfish.common.nocode.api.remote.RemoteNocodeService",
            "screenResource"),
    /** 图片资源发布流程：回调 nocode 服务更新图片资源状态 */
    图片资源发布("screen_image_release",
            "cn.com.mfish.common.nocode.api.remote.RemoteNocodeService",
            "screenResourceImg"),
    /** 请假申请发布流程：回调 demo 服务更新请假单状态 */
    请假申请发布("demo_leave_apply_release",
            "cn.com.mfish.demo.api.remote.RemoteDemoLeaveApplyService",
            "demoLeaveApply"),
    /** 测试流程 */
    TEST("test", null, null);

    /** 流程定义key值 */
    private final String key;
    /** 业务回调 Feign 接口全路径（为空表示无业务回调） */
    private final String callback;
    /** 业务前缀 */
    private final String prefix;

    FlowKey(String key, String callback, String prefix) {
        this.key = key;
        this.callback = callback;
        this.prefix = prefix;
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

    /**
     * 是否配置了业务回调
     *
     * @return true 表示有业务回调
     */
    public boolean hasCallback() {
        return callback != null && !callback.isEmpty();
    }

    @Override
    public String toString() {
        return this.key;
    }
}

package cn.com.mfish.common.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 工作流回调结果
 * @author: mfish
 * @date: 2025/9/26
 */
@Data
@Accessors(chain = true)
public class WorkflowCompleteResult {
    /** 流程实例ID */
    private String processInstanceId;
    /** 审批意见 */
    private String comment;
    /** 事件名称 */
    private String eventName;
}

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
    private String processInstanceId;
    private String comment;
    private String eventName;
}

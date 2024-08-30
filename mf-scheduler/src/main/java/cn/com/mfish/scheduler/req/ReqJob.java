package cn.com.mfish.scheduler.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V1.3.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "定时调度任务请求参数")
public class ReqJob {
    @Schema(description = "任务名称")
    private String jobName;
    @Schema(description = "任务组")
    private String jobGroup;
    @Schema(description = "任务类型(0 本地任务 1 RPC远程调用任务 2 MQ消息任务)")
    private Integer jobType;
    @Schema(description = "类名称")
    private String className;
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
}

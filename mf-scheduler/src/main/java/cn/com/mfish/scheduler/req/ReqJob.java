package cn.com.mfish.scheduler.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel("定时调度任务请求参数")
public class ReqJob {
    @ApiModelProperty(value = "任务名称")
    private String jobName;
    @ApiModelProperty(value = "任务组")
    private String jobGroup;
    @ApiModelProperty(value = "任务类型(0 本地任务 1 RPC远程调用任务 2 MQ消息任务)")
    private Integer jobType;
    @ApiModelProperty(value = "类名称")
    private String className;
}

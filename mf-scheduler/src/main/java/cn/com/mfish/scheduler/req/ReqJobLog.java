package cn.com.mfish.scheduler.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 任务日志
 * @author: mfish
 * @date: 2023-02-14
 * @version: V1.1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ApiModel("任务日志请求参数")
public class ReqJobLog extends ReqJob {
    @ApiModelProperty(value = "cron表达式")
    private String cron;
}

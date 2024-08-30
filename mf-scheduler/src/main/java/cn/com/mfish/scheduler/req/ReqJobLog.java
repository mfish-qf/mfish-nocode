package cn.com.mfish.scheduler.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 任务日志
 * @author: mfish
 * @date: 2023-02-14
 * @version: V1.3.1
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(description = "任务日志请求参数")
public class ReqJobLog extends ReqJob {
    @Schema(description = "cron表达式")
    private String cron;
}

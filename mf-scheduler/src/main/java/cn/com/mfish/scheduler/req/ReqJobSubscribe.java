package cn.com.mfish.scheduler.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 任务订阅表
 * @author: mfish
 * @date: 2023-02-20
 * @version: V1.3.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务订阅表请求参数")
public class ReqJobSubscribe {
    @Schema(description = "任务ID")
    private String jobId;
}

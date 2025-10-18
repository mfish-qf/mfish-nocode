package cn.com.mfish.common.workflow.api.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 查询所有任务列表参数
 * @author: mfish
 * @date: 2025/10/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(description = "所有任务请求参数")
public class ReqAllTask extends ReqTask {
    @Schema(description = "任务状态 为空时查询所有 created 待处理 completed 已完成 terminated 已取消，对应flowable的Task状态")
    private String status;
    @Schema(description = "审核开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditStartTime;
    @Schema(description = "审核结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditEndTime;
}

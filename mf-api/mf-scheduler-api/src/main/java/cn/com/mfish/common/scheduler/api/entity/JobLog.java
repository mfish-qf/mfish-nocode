package cn.com.mfish.common.scheduler.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 任务日志
 * @author: mfish
 * @date: 2023-02-14
 * @version: V2.2.0
 */
@Data
@TableName("qrtz_job_log")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "qrtz_job_log对象 任务日志")
public class JobLog extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @Schema(description = "任务ID")
    private String jobId;
    @Schema(description = "订阅ID")
    private String subscribeId;
    @Schema(description = "任务名称")
    private String jobName;
    @Schema(description = "任务组")
    private String jobGroup;
    @Schema(description = "任务类型(0 本地任务 1 RPC远程调用任务 2 MQ消息任务)")
    private Integer jobType;
    @Schema(description = "类名称")
    private String className;
    @Schema(description = "方法名称")
    private String methodName;
    @Schema(description = "调用参数")
    private String params;
    @Schema(description = "cron表达式")
    private String cron;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private Date endTime;
    @Schema(description = "日志类型(0入库日志 1文件日志)")
    private Integer logType;
    @Schema(description = "耗时(ms)")
    private Long costTime;
    @Schema(description = "执行状态（0开始 1调度成功 2调度失败 3执行成功 4执行失败）")
    private Integer status;
    @Schema(description = "备注信息")
    private String remark;
}

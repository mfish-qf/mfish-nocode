package cn.com.mfish.common.scheduler.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 任务日志
 * @author: mfish
 * @date: 2023-02-14
 * @version: V1.2.0
 */
@Data
@TableName("qrtz_job_log")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "qrtz_job_log对象", description = "任务日志")
public class JobLog extends BaseEntity<String> {
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty(value = "任务ID")
    private String jobId;
    @ApiModelProperty(value = "订阅ID")
    private String subscribeId;
    @ApiModelProperty(value = "任务名称")
    private String jobName;
    @ApiModelProperty(value = "任务组")
    private String jobGroup;
    @ApiModelProperty(value = "任务类型(0 本地任务 1 RPC远程调用任务 2 MQ消息任务)")
    private Integer jobType;
    @ApiModelProperty(value = "类名称")
    private String className;
    @ApiModelProperty(value = "方法名称")
    private String methodName;
    @ApiModelProperty(value = "调用参数")
    private String params;
    @ApiModelProperty(value = "cron表达式")
    private String cron;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "日志类型(0入库日志 1文件日志)")
    private Integer logType;
    @ApiModelProperty(value = "耗时(ms)")
    private Long costTime;
    @ApiModelProperty(value = "执行状态（0开始 1调度成功 2调度失败 3执行成功 4执行失败）")
    private Integer status;
    @ApiModelProperty(value = "备注信息")
    private String remark;
}

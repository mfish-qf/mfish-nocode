package cn.com.mfish.scheduler.entity;

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
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V1.0.0
 */
@Data
@TableName("qrtz_job")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "qrtz_job对象", description = "定时调度任务")
public class Job extends BaseEntity<String> {
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty(value = "任务名称")
    private String jobName;
    @ApiModelProperty(value = "任务组")
    private String jobGroup;
    @ApiModelProperty(value = "任务类型(0 本地任务 1 RPC远程调用任务 2 MQ消息任务)")
    private Integer jobType;
    @ApiModelProperty(value = "cron表达式")
    private String cron;
    @ApiModelProperty(value = "类名称")
    private String className;
    @ApiModelProperty(value = "方法名称")
    private String methodName;
    @ApiModelProperty(value = "调用参数")
    private String params;
    @ApiModelProperty(value = "允许并发执行（0不允许 1允许 ）")
    private Integer allowConcurrent = 0;
    @ApiModelProperty(value = "任务执行出错后处理方式（1立即执行 2执行一次 3放弃执行）")
    private Integer misfireHandler;
    @ApiModelProperty(value = "状态（0正常 1暂停）")
    private Integer status = 0;
    @ApiModelProperty(value = "备注信息")
    private String remark;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date endTime;
    @ApiModelProperty("优先级")
    private Integer priority = 0;
    @ApiModelProperty("时区")
    private String timeZone = "Asia/Shanghai";
}

package cn.com.mfish.scheduler.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-20
 * @version: V1.2.1
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
    @ApiModelProperty(value = "类名称")
    private String className;
    @ApiModelProperty(value = "方法名称")
    private String methodName;
    @ApiModelProperty(value = "调用参数")
    private String params;
    @ApiModelProperty(value = "允许并发执行（0不允许 1允许）")
    private Integer allowConcurrent;
    @ApiModelProperty(value = "过期策略（1立即执行一次 2放弃执行 ）")
    private Integer misfireHandler;
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;
    @ApiModelProperty(value = "优先级")
    private Integer priority;
    @ApiModelProperty(value = "时区")
    private String timeZone;
    @ApiModelProperty(value = "备注信息")
    private String remark;
    @ApiModelProperty(value = "日志类型(0入库日志 1文件日志)")
    private Integer logType;
    @ApiModelProperty(value = "订阅列表")
    @TableField(exist = false)
    private List<JobSubscribe> subscribes;
}

package cn.com.mfish.scheduler.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-20
 * @version: V2.0.1
 */
@Data
@TableName("qrtz_job")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "qrtz_job对象 定时调度任务")
public class Job extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
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
    @Schema(description = "允许并发执行（0不允许 1允许）")
    private Integer allowConcurrent;
    @Schema(description = "过期策略（1立即执行一次 2放弃执行 ）")
    private Integer misfireHandler;
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
    @Schema(description = "优先级")
    private Integer priority;
    @Schema(description = "时区")
    private String timeZone;
    @Schema(description = "备注信息")
    private String remark;
    @Schema(description = "日志类型(0入库日志 1文件日志)")
    private Integer logType;
    @Schema(description = "订阅列表")
    @TableField(exist = false)
    private List<JobSubscribe> subscribes;
}

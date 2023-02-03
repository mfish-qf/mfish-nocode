package cn.com.mfish.scheduler.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
    @ApiModelProperty(value = "cron表达式")
	private String cron;
    @ApiModelProperty(value = "调用方法")
	private String invokeMethod;
    @ApiModelProperty(value = "调用参数")
	private String invokeParam;
    @ApiModelProperty(value = "允许并发执行（1允许 0不允许）")
	private Integer allowConcurrent;
    @ApiModelProperty(value = "任务执行出错后处理方式（1立即执行 2执行一次 3放弃执行）")
	private String misfireHandler;
    @ApiModelProperty(value = "状态（0正常 1暂停）")
	private Integer status;
    @ApiModelProperty(value = "备注信息")
	private String remark;
}

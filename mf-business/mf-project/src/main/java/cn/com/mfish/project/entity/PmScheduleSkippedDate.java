package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @description: 排期日历跳过日期表(节假日、封版冻结日)
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@TableName("pm_schedule_skipped_date")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_schedule_skipped_date对象 排期日历跳过日期")
public class PmScheduleSkippedDate extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "所属配置ID")
    private String configId;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "跳过日期")
    private Date date;
    @Schema(description = "假期/跳过原因(如 国庆节黄金周、中秋节、研发封版冻结)")
    private String name;
    @Schema(description = "类型(holiday法定假日/custom_off自定义休息/freeze封版冻结)")
    private String type;
    @Schema(description = "是否周末")
    private Boolean isRecurringWeekend;
    @Schema(description = "删除标志(0正常 1已删除)")
    @TableLogic
    private Integer delFlag;
    @Schema(description = "备注")
    private String remark;
}

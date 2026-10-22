package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 排期日历全局规则配置表(单行配置)
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@TableName("pm_schedule_config")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_schedule_config对象 排期日历全局规则配置")
public class PmScheduleConfig extends BaseEntity<String> {
    @Schema(description = "唯一ID(固定为1,单行配置)")
    @TableId(type = IdType.INPUT)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "是否默认跳过周六与周日")
    private Boolean skipWeekends;
    @Schema(description = "删除标志(0正常 1已删除)")
    @TableLogic
    private Integer delFlag;
    @Schema(description = "备注")
    private String remark;
}

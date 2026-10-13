package cn.com.mfish.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 排期日历全局规则配置 DTO(含节假日、封版冻结日列表与调休补班工作日)
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "排期日历全局规则配置")
public class ScheduleConfigDTO {
    @Schema(description = "是否默认跳过周六与周日(默认true)")
    private Boolean skipWeekends = Boolean.TRUE;
    @Schema(description = "手动设置的跳过日期列表(节假日、封版冻结日)")
    private List<SkippedDateDTO> skippedDates = new ArrayList<>();
    @Schema(description = "调休补班工作日列表(在周末但仍需计入工作的日期)")
    private List<String> makeupWorkdays = new ArrayList<>();
}

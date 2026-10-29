package cn.com.mfish.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 排期跳过日期明细 DTO
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "排期跳过日期明细")
public class SkippedDateDTO {
    @Schema(description = "唯一ID")
    private String id;
    @Schema(description = "跳过日期(yyyy-MM-dd)")
    private String date;
    @Schema(description = "假期/跳过原因(如 国庆节黄金周、中秋节、研发封版冻结)")
    private String name;
    @Schema(description = "类型(holiday/custom_off/freeze)")
    private String type;
    @Schema(description = "是否周末")
    private Boolean isRecurringWeekend;
}

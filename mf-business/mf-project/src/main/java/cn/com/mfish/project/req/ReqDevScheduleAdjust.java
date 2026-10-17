package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description: 排期拖拽调期请求参数(甘特图拖动任务条改期)
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.1
 */
@Data
@Schema(description = "排期拖拽调期请求参数")
public class ReqDevScheduleAdjust {
    @Schema(description = "排期明细ID")
    @NotBlank(message = "排期明细ID不能为空")
    private String id;
    @Schema(description = "拖动后的新开始日(yyyy-MM-dd)")
    @NotBlank(message = "新的开始日期不能为空")
    private String devStartDate;
    @Schema(description = "调期原因/备注")
    private String pinReason;
}

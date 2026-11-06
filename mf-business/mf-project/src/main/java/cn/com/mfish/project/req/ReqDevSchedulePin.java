package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description: 排期钉住切换请求参数
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.1
 */
@Data
@Schema(description = "排期钉住切换请求参数")
public class ReqDevSchedulePin {
    @Schema(description = "排期明细ID")
    @NotBlank(message = "排期明细ID不能为空")
    private String id;
    @Schema(description = "是否钉住: true=钉住(重算不动) / false=取消钉住")
    private Boolean pinned;
    @Schema(description = "钉住原因/备注")
    private String pinReason;
}

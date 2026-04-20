package cn.com.mfish.common.demo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: leave apply request
 * @author: mfish
 * @date: 2026-04-19
 * @version: V2.3.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "Leave apply query request")
public class ReqDemoLeaveApply {
    @Schema(description = "Title")
    private String title;

    @Schema(description = "Leave Type")
    private Integer leaveType;

    @Schema(description = "Audit State")
    private Integer auditState;
}

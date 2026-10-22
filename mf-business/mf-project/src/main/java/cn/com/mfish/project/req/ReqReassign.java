package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description: 快捷人员调配请求参数
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Schema(description = "快捷人员调配请求参数")
public class ReqReassign {
    @Schema(description = "需求ID")
    @NotBlank(message = "需求ID不能为空")
    private String reqId;
    @Schema(description = "原人员")
    @NotBlank(message = "原人员不能为空")
    private String fromPerson;
    @Schema(description = "目标人员")
    @NotBlank(message = "目标人员不能为空")
    private String toPerson;
    @Schema(description = "岗位角色(frontend前端/backend后端)")
    private String role;
    @Schema(description = "是否独占整单转派(true将整个需求移交目标开发人员)")
    private Boolean transferAll = Boolean.FALSE;
}

package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description: 快捷人员调配请求参数
 * fromPerson 为空串表示「从空槽位分配」: 该侧原本无开发人员(未分配/不涉及),
 * 服务层 removeIf 空串无副作用, 直接将目标人员加入对应侧名单, 用于甘特图「分配」场景
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
    @Schema(description = "原人员(为空表示从空槽位分配: 该侧原本未指派开发人员, 直接加入目标人员)")
    private String fromPerson;
    @Schema(description = "目标人员")
    @NotBlank(message = "目标人员不能为空")
    private String toPerson;
    @Schema(description = "目标人员用户ID(账号化: 点选人员时已知的确切 userId, 用于区分同租户同名人员; 不传则该位退回按姓名排期)")
    private String toPersonId;
    @Schema(description = "岗位角色(frontend前端/backend后端)")
    private String role;
    @Schema(description = "是否独占整单转派(true将整个需求移交目标开发人员)")
    private Boolean transferAll = Boolean.FALSE;
}

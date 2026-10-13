package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @description: 批量分配人员请求参数
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Schema(description = "批量分配人员请求参数")
public class ReqBatchAssign {
    @Schema(description = "需求ID列表")
    @NotEmpty(message = "需求ID列表不能为空")
    private List<String> ids;
    @Schema(description = "人员类型(frontendDev前端/backendDev后端/qaTester测试/owner负责人)")
    @NotBlank(message = "人员类型不能为空")
    private String personnelType;
    @Schema(description = "人员姓名串(如 周伟、王敏，多个以、分隔)")
    private String names;
}

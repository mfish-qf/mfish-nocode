package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 需求工时微调请求参数
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Schema(description = "需求工时微调请求参数")
public class ReqWorkload {
    @Schema(description = "需求ID")
    @NotBlank(message = "需求ID不能为空")
    private String reqId;
    @Schema(description = "调整后的工时(人/天)；指定 devType 时写入对应角色的工时段")
    @DecimalMin(value = "0.1", message = "工时不能小于0.1人天")
    private BigDecimal workload;
    @Schema(description = "调整的岗位类型: frontend=前端 / backend=后端; 为空时按 total 处理")
    private String devType;
    @Schema(description = "前端研发工时(单位:人/天)")
    private BigDecimal frontendWorkload;
    @Schema(description = "后端研发工时(单位:人/天)")
    private BigDecimal backendWorkload;
}

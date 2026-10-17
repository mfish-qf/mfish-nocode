package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @description: 批量更新需求状态请求参数
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Schema(description = "批量更新需求状态请求参数")
public class ReqBatchStatus {
    @Schema(description = "需求ID列表")
    @NotEmpty(message = "需求ID列表不能为空")
    private List<String> ids;
    @Schema(description = "目标状态(待评审/开发中/待转测/测试中/验收中/已上线)")
    @NotBlank(message = "目标状态不能为空")
    private String status;
}

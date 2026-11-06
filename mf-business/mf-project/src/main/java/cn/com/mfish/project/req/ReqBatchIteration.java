package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @description: 批量分配/移除所属迭代请求参数
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Schema(description = "批量分配/移除所属迭代请求参数")
public class ReqBatchIteration {
    @Schema(description = "需求ID列表")
    @NotEmpty(message = "需求ID列表不能为空")
    private List<String> ids;
    @Schema(description = "目标迭代ID(为空表示移除所属迭代)")
    private String iterationId;
}

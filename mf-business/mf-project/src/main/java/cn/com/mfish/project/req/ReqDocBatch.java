package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @description: 批量分页获取需求PRD文档内容请求参数
 * @author: mfish
 * @date: 2026-10-14
 * @version: V2.4.2
 */
@Data
@Schema(description = "批量分页获取需求PRD文档内容请求参数")
public class ReqDocBatch {
    @Schema(description = "需求ID列表")
    @NotEmpty(message = "ID列表不能为空")
    private List<String> ids;

    @Schema(description = "页码(从1开始, 默认1)")
    private Integer pageNum;

    @Schema(description = "每页条数(默认50)")
    private Integer pageSize;
}

package cn.com.mfish.common.workflow.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 查询所有任务列表参数
 * @author: mfish
 * @date: 2025/10/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(description = "所有任务请求参数")
public class ReqAllTask extends ReqTask {
    @Schema(description = "任务状态 为空时查询所有 0 待处理 1 已完成 2已取消")
    private Integer status;
}

package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @description: 排期明细按版本筛选查询请求参数
 *  不再由前端回传大量需求UUID, 改为传版本筛选条件, 后端复用需求查询解析需求ID后聚合排期明细
 * @author: mfish
 * @date: 2026-10-20
 * @version: V2.4.1
 */
@Data
@Schema(description = "排期明细按版本筛选查询请求参数")
public class ReqDevScheduleQuery {
    @Schema(description = "勾选的版本ID列表(为空表示全部版本汇总; 可与 includeUnassigned 叠加)")
    private List<String> iterationIds;

    @Schema(description = "是否叠加\"未纳入版本\"(无迭代归属且月份为空), 可与版本条件并用")
    private Boolean includeUnassigned = false;
}

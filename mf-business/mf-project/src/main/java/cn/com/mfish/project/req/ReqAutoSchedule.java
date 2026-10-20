package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @description: 自动排期请求参数
 * @author: mfish
 * @date: 2026-10-18
 * @version: V2.4.1
 */
@Data
@Schema(description = "自动排期请求参数")
public class ReqAutoSchedule {
    @Schema(description = "需追加进排期的版本ID列表(前端仅在显式选中已归档版本时传入; 为空时统一排期全部未归档版本的需求)")
    private List<String> iterationIds;

    @Schema(description = "是否同步排期\"未纳入版本\"的需求(需求无归属版本, 无版本ID可传, 用该标识告诉后端合并计算)")
    private Boolean includeUnassigned;
}

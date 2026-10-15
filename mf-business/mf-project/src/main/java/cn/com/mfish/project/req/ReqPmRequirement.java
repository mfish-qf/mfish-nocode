package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 核心需求查询请求参数
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "核心需求查询请求参数")
public class ReqPmRequirement {
    @Schema(description = "归属迭代/月份代码(如 2026-09)")
    private String month;
    @Schema(description = "归属迭代ID")
    private String iterationId;
    @Schema(description = "研发状态(待评审/开发中/待转测/测试中/验收中/已上线)")
    private String status;
    @Schema(description = "优先级")
    private String priority;
    @Schema(description = "前端人员(模糊匹配)")
    private String frontendDev;
    @Schema(description = "后端人员(模糊匹配)")
    private String backendDev;
    @Schema(description = "测试人员(模糊匹配)")
    private String qaTester;
    @Schema(description = "关键词(模块/功能名模糊匹配)")
    private String keyword;
    @Schema(description = "是否包含已归档(状态=已发布)版本的需求; 缺省/null 时仅纯汇总场景排除归档版本, 精确勾选版本场景不受影响")
    private Boolean includeArchived;
}

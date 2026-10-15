package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

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
    @Schema(description = "勾选的版本代号列表(单选/多选, 如 v1.3.7,v1.3.7.2); 后端按版本号精确查询(iteration_id 或 month 命中任一), 不做前缀模糊匹配")
    private List<String> codes;
    @Schema(description = "是否包含未纳入版本(无归属)的需求; 与 codes 可同时传入")
    private Boolean includeUnassigned;
}

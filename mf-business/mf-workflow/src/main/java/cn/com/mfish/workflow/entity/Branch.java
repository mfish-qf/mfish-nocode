package cn.com.mfish.workflow.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description: 分支条件
 * @author: mfish
 * @date: 2026/4/12
 */
@Data
@Schema(description = "分支条件")
public class Branch {
    @Schema(description = "分支ID")
    private String id;
    @Schema(description = "分支表达式")
    private String expression;
}

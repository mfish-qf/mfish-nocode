package cn.com.mfish.workflow.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description: 连线业务数据
 * @author: mfish
 * @date: 2026/3/24
 */
@Data
@Schema(description = "连线业务数据", name = "EdgeData")
public class EdgeData {
    @Schema(description = "条件（approved, rejected,自定义条件等）")
    private String condition;
}
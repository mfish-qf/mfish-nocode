package cn.com.mfish.ai.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: AI模型配置信息
 * @author: mfish
 * @date: 2026-07-03
 * @version: V2.4.1
 */
@Data
@TableName("ai_model_config")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "ai_model_config对象 AI模型配置信息")
public class AiModelConfig extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;
    @ExcelProperty("租户ID")
    @Schema(description = "租户ID")
    private String tenantId;
    @ExcelProperty("提供者: openai/ollama/deepseek/zhipuai/anthropic等")
    @Schema(description = "提供者: openai/ollama/deepseek/zhipuai/anthropic等")
    private String provider;
    @ExcelProperty("模型名称: gpt-4o, qwen3:8b, deepseek-v3 等")
    @Schema(description = "模型名称: gpt-4o, qwen3:8b, deepseek-v3 等")
    private String modelName;
    @ExcelProperty("接入协议: openai/ollama/deepseek/anthropic")
    @Schema(description = "接入协议: openai/ollama/deepseek/anthropic")
    private String protocol;
    @ExcelProperty("API密钥(加密存储)")
    @Schema(description = "API密钥(加密存储)")
    private String apiKey;
    @ExcelProperty("API基础地址")
    @Schema(description = "API基础地址")
    private String baseUrl;
    @ExcelProperty("最大token数")
    @Schema(description = "最大token数")
    private Integer maxTokens;
    @ExcelProperty("温度参数")
    @Schema(description = "温度参数")
    private Double temperature;
    @ExcelProperty("top_p参数")
    @Schema(description = "top_p参数")
    private Double topP;
    @ExcelProperty("补全项路径")
    @Schema(description = "补全项路径")
    private String completionsPath;
    @ExcelProperty("是否启用 1启用 0禁用")
    @Schema(description = "是否启用 1启用 0禁用")
    private Short enabled;
    @ExcelProperty("排序(决定fallback优先级)")
    @Schema(description = "排序(决定fallback优先级)")
    private Integer sortOrder;
    @ExcelProperty("备注")
    @Schema(description = "备注")
    private String remark;
}

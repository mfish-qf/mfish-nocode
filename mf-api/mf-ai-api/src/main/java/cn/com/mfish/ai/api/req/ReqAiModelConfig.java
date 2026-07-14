package cn.com.mfish.ai.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: AI模型配置信息
 * @author: mfish
 * @date: 2026-07-03
 * @version: V2.4.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "AI模型配置信息请求参数")
public class ReqAiModelConfig {
    @Schema(description = "模型名称: gpt-4o, qwen3:8b, deepseek-v3 等")
    private String modelName;
    @Schema(description = "是否启用 1启用 0禁用")
    private Integer enabled;
    @Schema(description = "接入协议: openai/ollama/deepseek/anthropic")
    private String protocol;
}

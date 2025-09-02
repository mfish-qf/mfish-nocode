package cn.com.mfish.common.ai.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description: Ai请求
 * @author: mfish
 * @date: 2025/8/29
 */
@Data
@Schema(description = "Ai请求")
public class AiRequest {
    @Schema(description = "聊天id")
    private String id;
    @Schema(description = "会话id")
    private String sessionId;
    @Schema(description = "消息")
    private AiMessage message;
}

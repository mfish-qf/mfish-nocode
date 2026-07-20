package cn.com.mfish.common.ai.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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
    @Schema(description = "文件ID列表(文件fileKey集合，AI将获取文件内容并解析)")
    private List<String> fileIds;
}

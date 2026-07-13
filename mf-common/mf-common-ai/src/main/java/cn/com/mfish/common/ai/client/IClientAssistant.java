package cn.com.mfish.common.ai.client;

import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * @description: 客户端助手
 * @author: mfish
 * @date: 2025/8/22
 */
public interface IClientAssistant {
    /**
     * 聊天
     * @param sessionId 会话id
     * @param prompt 聊天内容
     * @return 聊天信息
     */
    Flux<ChatResponse> chat(String sessionId, String prompt);

    /**
     * 聊天
     * @param aiRequest 聊天请求
     * @return 聊天结果
     */
    Flux<ChatResponseVo> chat(AiRequest aiRequest);

    /**
     * 获取助手对应的网关路由路径
     * 用于AssistantRegistry按路径查找助手，替代Servlet forward实现真正的SSE流式传输
     *
     * @return 路由路径，如/ai/agent/chat、/ai/sys/assist、/ai/oauth2/assist
     */
    String getPath();
}

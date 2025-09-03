package cn.com.mfish.common.ai.agent;

import cn.com.mfish.common.ai.client.IClientAssistant;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import reactor.core.publisher.Flux;

/**
 * @description: 系统中心助手
 * @author: mfish
 * @date: 2025/8/22
 */
public abstract class BaseAssistant implements IClientAssistant {

    /**
     * 聊天返回id
     *
     * @return 聊天信息
     */
    @Override
    public Flux<ChatResponseVo> chat(AiRequest aiRequest) {
        return chat(aiRequest.getSessionId(), aiRequest.getMessage().getContent())
                .mapNotNull(resp -> new ChatResponseVo().setId(aiRequest.getId())
                        .setContent(resp.getResult().getOutput().getText()));
    }
}

package cn.com.mfish.common.ai.client;

import cn.com.mfish.common.ai.entity.ChatResponseVo;
import reactor.core.publisher.Flux;

/**
 * @description: 客户端助手
 * @author: mfish
 * @date: 2025/8/22
 */
public interface IClientAssistant {
    /**
     * 聊天
     *
     * @param prompt 提示词
     * @return 聊天信息
     */
    Flux<String> chat(String prompt);

    /**
     * 聊天
     *
     * @param chatId 聊天id
     * @param prompt 聊天内容
     * @return 聊天结果
     */
    Flux<ChatResponseVo> chat(String chatId, String prompt);
}

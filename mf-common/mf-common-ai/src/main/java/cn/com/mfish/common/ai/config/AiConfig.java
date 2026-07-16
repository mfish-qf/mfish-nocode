package cn.com.mfish.common.ai.config;

import cn.com.mfish.common.ai.tool.FaultTolerantToolCallingManager;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: Ai配置信息
 * @author: mfish
 * @date: 2025/8/18
 */
@Configuration
public class AiConfig {
    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }
    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }

    /**
     * 注册容错 ToolCallingManager，覆盖 Spring AI 默认的 DefaultToolCallingManager
     * <p>
     * 当 LLM 虚构了不存在的工具名时，返回提示信息（含可用工具列表）让 LLM 重新选择，
     * 而不是抛出 IllegalStateException 中断整个对话流程。
     * </p>
     * 通过 @ConditionalOnMissingBean 机制，此 Bean 会自动覆盖 Spring AI 的默认实现。
     */
    @Bean
    public ToolCallingManager toolCallingManager() {
        return FaultTolerantToolCallingManager.createDefault();
    }
}

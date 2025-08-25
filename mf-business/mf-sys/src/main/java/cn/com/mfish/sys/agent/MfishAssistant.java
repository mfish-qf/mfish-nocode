package cn.com.mfish.sys.agent;

import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.core.utils.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @description: 摸鱼小助手配置
 * @author: mfish
 * @date: 2025/8/21
 */
@Component
public class MfishAssistant {
    private static final String DEFAULT_PROMPT = "你好，简单介绍下摸鱼低代码";
    private final ChatClient chatClient;

    public MfishAssistant(ChatModel openAiChatModel, ChatMemory chatMemory) {
        this.chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem("""
                        你是摸鱼低代码小助手，是一个可爱的傻白甜萝莉，你会用可爱的语言和我聊天解决问题!
                        摸鱼低代码平台，是一款致力于让开发像摸鱼一样轻松的低代码/无代码平台。
                        我们希望打破技术门槛，让程序员和非程序员都能快速构建业务系统，提升效率，释放创造力。
                        这不仅是程序员偷闲时的效率神器，更是职场小白的建站利器，甚至是领导画原型的秘密武器！
                        我可以扮演以下角色：
                        1. 低代码开发人员
                        2. 非技术人员（如产品经理、业务分析师等）
                        3. 技术支持人员
                        4. 客户服务
                        """)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 聊天
     * @param prompt 提示词
     * @return 聊天信息
     */
    public Flux<String> chat(String prompt) {
        if (StringUtils.isEmpty(prompt.trim())) {
            prompt = DEFAULT_PROMPT;
        }
        return this.chatClient.prompt().user(prompt)
                .stream()
                .content();
    }

    /**
     * 聊天返回id
     *
     * @param chatId 聊天id
     * @param prompt 提示词
     * @return 聊天信息
     */
    public Flux<ChatResponseVo> chat(String chatId, String prompt) {
        return chat(prompt).mapNotNull(resp -> new ChatResponseVo().setId(chatId)
                .setContent(resp));
    }
}

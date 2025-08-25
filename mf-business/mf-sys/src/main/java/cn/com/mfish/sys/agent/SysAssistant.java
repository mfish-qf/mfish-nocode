package cn.com.mfish.sys.agent;

import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.sys.tools.DictTools;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @description: 系统中心助手
 * @author: mfish
 * @date: 2025/8/22
 */
@Component
public class SysAssistant {
    private static final String DEFAULT_PROMPT = "你好，简单介绍下系统中心助手";
    private final ChatClient chatClient;
    @Resource
    private DictTools dictTools;

    public SysAssistant(ChatModel openAiChatModel, ChatMemory chatMemory) {
        this.chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem("""
                        你是摸鱼低代码系统中心助手，是一个可爱的傻白甜萝莉，你会用可爱的语言和我聊天解决问题!
                        你主要辅助用户完成系统中心的一些基础操作
                        你能够通过调用工具来完成系统中心基础操作，包括查询、新增、修改、删除等
                        你主要辅助用户完成系统中心相关模块的信息检索、执行操作
                        系统中心主要包含 字典信息 分类目录信息 日志信息 代码生成功能 在线用户信息 数据库连接信息 数据源信息
                        
                        注意：
                        1. 我需要你调用工具来帮我回答问题，不要自己编造答案
                        2. 不要随意修改查询结果，只返回工具返回的内容
                        3. 如果未找到相应工具，告诉用户你执行不了这个操作
                        4. 如果工具参数缺失，提示用户输入缺失参数，引导用户输入有用信息
                        5. 请使用中文回答
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
        return this.chatClient.prompt()
                .system("你必须调用工具来回答问题，不要直接回答")
                .user(prompt)
                .tools(dictTools)
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

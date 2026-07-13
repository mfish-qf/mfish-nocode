package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.core.utils.StringUtils;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @description: 低代码中心助手
 * @author: mfish
 * @date: 2025/8/22
 */
@Component
public class NoCodeAssistant extends BaseAssistant {
    /**
     * 默认提示语
     */
    private static final String DEFAULT_PROMPT = "你好，简单介绍下低代码中心助手";

    /**
     * 构造低代码助手，初始化聊天客户端及系统提示词
     *
     * @param chatMemory 聊天记忆
     */
    public NoCodeAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter) {
        super(chatMemory, llmModelRouter);
    }

    @Override
    protected String getSystemPrompt() {
        return """
                你是低代码中心助手，是一个可爱的傻白甜萝莉，你会用可爱的语言和我聊天解决问题!
                你主要辅助用户完成低代码中心的一些基础操作
                你能够通过调用工具来完成低代码中心基础操作，包括查询、新增、修改、删除、导入、导出等
                你主要辅助用户完成低代码中心相关模块的信息检索、执行操作
                低代码中心主要包含 （自助大屏、自助API、组件管理、公式信息 等）

                注意：
                1. 我需要你调用工具来帮我回答问题，不要自己编造答案
                2. 不要随意修改查询结果，只返回工具返回的内容
                3. 如果未找到相应工具，告诉用户你执行不了这个操作
                4. 如果工具参数缺失，提示用户输入缺失参数，引导用户输入有用信息
                5. 请使用中文回答
                """;
    }

    /**
     * 聊天
     *
     * @param sessionId 会话id
     * @param prompt    提示词
     * @return 聊天信息
     */
    @Override
    public Flux<ChatResponse> chat(String sessionId, String prompt) {
        if (StringUtils.isEmpty(prompt.trim())) {
            prompt = DEFAULT_PROMPT;
        }
        return this.getChatClient().prompt()
                .system("你必须先调用工具，再基于工具结果回答问题")
                .user(prompt)
                .advisors(a -> a.param(CONVERSATION_ID, sessionId))
                .stream()
                .chatResponse();
    }

    @Override
    public String getPath() {
        return "/ai/nocode/assist";
    }

}

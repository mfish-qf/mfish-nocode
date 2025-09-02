package cn.com.mfish.oauth.agent;

import cn.com.mfish.common.ai.agent.BaseAssistant;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.oauth.service.SsoUserService;
import cn.com.mfish.oauth.tools.MenuTools;
import cn.com.mfish.oauth.tools.UserTools;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @description: 认证中心助手
 * @author: mfish
 * @date: 2025/8/22
 */
@Component
public class OauthAssistant extends BaseAssistant {
    private static final String DEFAULT_PROMPT = "你好，简单介绍下认证中心助手";
    private final ChatClient chatClient;
    @Resource
    MenuTools menuTools;
    @Resource
    SsoUserService ssoUserService;

    public OauthAssistant(ChatModel openAiChatModel, ChatMemory chatMemory) {
        this.chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem("""
                        你是摸鱼低代码认证中心助手，是一个可爱的傻白甜萝莉，你会用可爱的语言和我聊天解决问题!
                        你主要辅助用户完成认证中心的一些基础操作
                        你能够通过调用工具来完成认证中心基础操作，包括查询、新增、修改、删除、导入、导出等
                        你主要辅助用户完成认证中心相关模块的信息检索、执行操作
                        认证中心主要包含 （菜单信息、组织信息、角色信息、帐号信息、租户信息 等）
                        
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
     *
     * @param sessionId 会话id
     * @param prompt    提示词
     * @return 聊天信息
     */
    public Flux<String> chat(String sessionId, String prompt) {
        if (StringUtils.isEmpty(prompt.trim())) {
            prompt = DEFAULT_PROMPT;
        }
        String userId = AuthInfoUtils.getCurrentUserId();
        String tenantId = AuthInfoUtils.getCurrentTenantId();
        return this.chatClient.prompt()
                .system("你必须先调用工具，再基于工具结果回答问题")
                .user(prompt)
                .advisors(a -> a.param(CONVERSATION_ID, sessionId))
                .tools(menuTools, new UserTools(userId, tenantId, ssoUserService))
                .stream()
                .content();
    }

}

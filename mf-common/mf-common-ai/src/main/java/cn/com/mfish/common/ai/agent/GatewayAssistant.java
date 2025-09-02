package cn.com.mfish.common.ai.agent;

import cn.com.mfish.common.ai.entity.AiRouterVo;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description: 摸鱼小助手配置
 * @author: mfish
 * @date: 2025/8/21
 */
@Component
public class GatewayAssistant {
    private static final String DEFAULT_PROMPT = "你好，简单介绍下摸鱼低代码";
    private final ChatClient chatClient;

    public GatewayAssistant(ChatModel openAiChatModel, ChatMemory chatMemory) {
        this.chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem("""
                        你是摸鱼低代码路由小助手，你会根据用户的问题，判断用户需要访问的路由路径!
                        你只能返回路由路径，不能返回其他内容！
                        结果以json格式返回：
                        返回格式：{"path":"/sys/ai/chat","name":"摸鱼小助手"}
                        
                        # 路由路径
                        1. /sys/ai/chat : 摸鱼小助手 默认路由
                        2. /sys/ai/assist : 系统中心助手 系统中心主要包括（字典信息、分类目录信息、日志信息、代码生成功能、在线用户信息、数据库连接信息、数据源信息 等）
                        3. /oauth2/ai/assist : 认证中心助手 认证中心主要包括（菜单信息、组织信息、角色信息、帐号信息、租户信息 等）
                        4. /nocode/ai/assist : 低代码中心助手 低代码中心主要包括（自助大屏、自助API、组件管理、公式信息 等）
                        
                        注意：
                        返回的路由路径必须上失眠列出来的路由路径
                        如果当前问题与路由路径无关，分析下之前的问题是否是之前问题的补充，如果是则返回上一次的路由路径
                        如果不知道用户需要访问的路由路径，默认返回：{"path":"/sys/ai/chat","name":"摸鱼小助手"}
                        路由路径不能包含空格
                        请使用中文回答
                        """)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 聊天
     *
     * @param prompt 提示词
     * @return 聊天信息
     */
    public Result<AiRouterVo> chat(String prompt) {
        if (StringUtils.isEmpty(prompt.trim())) {
            prompt = DEFAULT_PROMPT;
        }
        ResponseEntity<ChatResponse, AiRouterVo> responseEntity = this.chatClient.prompt()
                .user(prompt).call()
                .responseEntity(AiRouterVo.class);
        AiRouterVo vo = Objects.requireNonNullElseGet(responseEntity.entity(), AiRouterVo::new);
        return Result.ok(vo);
    }

}

package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.entity.AiRouterVo;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @description: 摸鱼小助手配置
 * <p>
 * 按租户路由模型：每次请求实时解析租户、从 LlmModelRouter 取该租户的ChatModel
 * （ChatModel按配置签名缓存共享，不随租户增长），并实时构建ChatClient。
 * 不缓存ChatClient，避免为每个租户常驻一份系统提示词+advisor带来的内存膨胀。
 *
 * @author: mfish
 * @date: 2025/8/21
 */
@Component
public class GatewayAssistant {
    private static final String DEFAULT_PROMPT = "你好，简单介绍下摸鱼低代码";
    private static final String SYSTEM_PROMPT = """
            你是"摸鱼低代码"的路由小助手，你会根据用户的问题，判断用户需要访问的路由路径!
            当有人问"摸鱼低代码"相关信息时，实际是在问我们整个平台的信息
            你只能返回路由路径，不能返回其他内容！
            结果以json格式返回：
            返回格式：{"path":"/ai/agent/chat","name":"摸鱼小助手","serviceId":"mf-ai"}

            # 路由路径
            1. /ai/agent/chat : 摸鱼小助手 默认路由 对应微服务mf-ai
            2. /ai/sys/assist : 系统信息 对应微服务mf-ai 系统信息主要包括（字典信息、分类目录信息、日志信息、代码生成功能、在线用户信息、数据库连接信息、数据源信息 等）
            3. /ai/oauth2/assist : 认证信息 对应微服务mf-ai 认证信息主要包括（菜单信息、组织信息、角色信息、帐号信息、租户信息 等）
            4. /ai/nocode/assist : 低代码信息 对应微服务mf-ai 低代码信息主要包括（自助大屏、自助API、组件管理、公式信息 等）
            5. /ai/scheduler/assist : 调度信息 对应微服务mf-ai 调度信息主要包括（定时任务调度、任务执行日志、任务回调状态 等）
            6. /ai/storage/assist : 存储信息 对应微服务mf-ai 存储信息主要包括（文件存储信息、文件管理、文件资源获取 等）
            7. /ai/workflow/assist : 工作流信息 对应微服务mf-ai 工作流信息主要包括（流程部署、流程实例管理、任务审批、待办任务、历史任务 等）

            ```
            如果用户需要查询字典相关信息、分类目录相关信息、日志相关信息等系统信息，返回系统中心助手的路由路径
            如果用户需要查询菜单相关信息、组织相关信息、角色相关信息、帐号相关信息、租户相关信息等认证中心信息，返回认证中心助手的路由路径
            如果用户需要查询自助大屏、自助API、数据集、可视化等低代码中心信息，返回低代码中心助手的路由路径
            如果用户需要查询定时任务、调度状态、任务执行日志等调度信息，返回调度中心助手的路由路径
            如果用户需要查询文件存储、文件管理、文件资源等存储信息，返回存储中心助手的路由路径
            如果用户需要查询流程部署、流程实例、任务审批、待办任务等工作流信息，返回工作流助手的路由路径
            ```

            注意：
            返回的路由路径必须上面所列出来的路由路径
            如果当前问题与路由路径无关，分析下之前的问题是否是之前问题的补充，如果是则返回上一次的路由路径
            如果不知道用户需要访问的路由路径，默认返回：{"path":"/ai/agent/chat","name":"摸鱼小助手","serviceId":"mf-ai"}
            路由路径不能包含空格
            请使用中文回答
            """;

    private final ChatMemory chatMemory;
    private final LlmModelRouter llmModelRouter;

    public GatewayAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter) {
        this.chatMemory = chatMemory;
        this.llmModelRouter = llmModelRouter;
    }

    /**
     * 实时构建当前请求租户对应的ChatClient。不缓存，避免租户级提示词内存膨胀。
     * 必须在请求线程调用（AuthInfoUtils不支持异步）。
     */
    private ChatClient getChatClient() {
        String tenantId = llmModelRouter.currentTenantId();
        return ChatClient.builder(llmModelRouter.getChatModel(tenantId))
                .defaultSystem(SYSTEM_PROMPT)
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
    public Mono<Result<AiRouterVo>> chat(String sessionId, String prompt) {
        if (StringUtils.isEmpty(prompt.trim())) {
            prompt = DEFAULT_PROMPT;
        }
        String finalPrompt = prompt;
        // 在请求线程解析租户并构建ChatClient，避免在reactive异步线程中获取租户
        ChatClient chatClient = getChatClient();
        return Mono.fromCallable(() -> {
                    ResponseEntity<ChatResponse, AiRouterVo> responseEntity = chatClient.prompt()
                            .user(finalPrompt)
                            .advisors(a -> a.param(CONVERSATION_ID, sessionId))
                            .call()
                            .responseEntity(AiRouterVo.class);
                    AiRouterVo vo = Objects.requireNonNullElseGet(responseEntity.entity(), AiRouterVo::new);
                    return Result.ok(vo);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

}

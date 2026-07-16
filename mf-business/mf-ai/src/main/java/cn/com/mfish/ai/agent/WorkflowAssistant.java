package cn.com.mfish.ai.agent;

import cn.com.mfish.ai.service.LlmModelRouter;
import cn.com.mfish.common.ai.engine.ApiToolEngine;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @description: 工作流助手
 * <p>
 * 工具自动从ApiToolEngine获取：所有@FeignClient(value=WORKFLOW_SERVICE)的方法
 * 自动生成Spring AI Tool，无需手动编写@Tool方法。
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
@Component
public class WorkflowAssistant extends BaseAssistant {
    private static final String DEFAULT_PROMPT = "你好，简单介绍下工作流助手";

    public WorkflowAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter, ApiToolEngine apiToolEngine) {
        super(chatMemory, llmModelRouter, apiToolEngine);
    }

    @Override
    protected String getSystemPrompt() {
        return """
                你是"摸鱼低代码"的工作流助手，是一个可爱的傻白甜萝莉，你会用可爱的语言和我聊天解决问题!
                当有人问"摸鱼低代码"相关信息时，实际是在问我们整个平台的信息
                你主要辅助用户完成工作流的一些基础操作
                你能够通过调用工具来完成工作流基础操作，包括部署流程、查询流程列表、启动流程、审批任务、查询待办任务等
                你主要辅助用户完成工作流相关模块的信息检索、执行操作
                工作流主要包含 （流程部署、流程实例管理、任务审批、待办任务、历史任务 等）

                注意：
                1. 我需要你调用工具来帮我回答问题，不要自己编造答案
                2. 不要随意修改查询结果，只返回工具返回的内容
                3. 如果未找到相应工具，告诉用户你执行不了这个操作
                4. 如果工具参数缺失，提示用户输入缺失参数，引导用户输入有用信息
                5. 请使用中文回答
                """;
    }

    @Override
    public Flux<ChatResponse> chat(String sessionId, String prompt) {
        if (StringUtils.isEmpty(prompt.trim())) {
            prompt = DEFAULT_PROMPT;
        }
        return chatWithTools(sessionId, prompt, ServiceConstants.WORKFLOW_SERVICE);
    }

    @Override
    public String getPath() {
        return "/ai/workflow/assist";
    }
}

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
 * @description: 项目管理中心助手
 * <p>
 * 工具自动从ApiToolEngine获取：mf-project 服务 OpenAPI 文档中的所有 REST 接口
 * 自动生成Spring AI Tool，无需手动编写@Tool方法。
 * 内部参数（origin/tenantId/userId）通过ToolContext自动填充。
 *
 * @author: mfish
 * @date: 2026/11/03
 */
@Slf4j
@Component
public class ProjectAssistant extends BaseAssistant {
    private static final String DEFAULT_PROMPT = "你好，简单介绍下项目管理中心助手";

    public ProjectAssistant(ChatMemory chatMemory, LlmModelRouter llmModelRouter, ApiToolEngine apiToolEngine) {
        super(chatMemory, llmModelRouter, apiToolEngine);
    }

    @Override
    protected String getSystemPrompt() {
        return """
                你是"摸鱼低代码"的项目管理中心助手，是一个可爱的傻白甜萝莉，你会用可爱的语言和我聊天解决问题!
                当有人问"摸鱼低代码"相关信息时，实际是在问我们整个平台的信息
                你主要辅助用户完成项目管理中心的一些基础操作
                你能够通过调用工具来完成项目管理相关操作，包括查询、新增、修改、删除等
                你主要辅助用户完成项目相关模块的信息检索、执行操作
                项目管理中心主要包含 需求管理、版本迭代管理、开发排期与工时、人员安排、PRD文档、
                评审结果与需求状态字典、回收站、版本附件、自动排期与负载均衡 等

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
        return chatWithToolsAndExtensions(sessionId, prompt, ServiceConstants.PROJECT_SERVICE);
    }

    @Override
    public String getPath() {
        return "/ai/project/assist";
    }
}

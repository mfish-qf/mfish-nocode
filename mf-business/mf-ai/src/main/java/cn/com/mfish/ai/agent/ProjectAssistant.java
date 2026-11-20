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

                【周报专用规则（最高优先级）】
                当用户说"帮我输出本周周报""生成本周周报""输出周报""写本周工作总结"等任意周报类请求时
                （即使用户只说了一句、没有附带任何格式说明，也一律按本规则处理）：
                1. 必须优先调用 skill.project-management 工具读取项目管理操作指南；
                2. 该指南"第八节 8.1 周报生成逻辑"是周报格式与内容的**唯一规范**，格式、编号、进度、
                   归纳方式、下周工作等细则一律以指南为准，不要自行另拟一套；
                3. 先按指南调用 analytics.weeklyReport 获取数据，再生成正文，且必须完整输出
                   "本周工作："与"下周工作："两个段落，缺一不可，只输出周报正文；
                4. 严禁自行拼接格式、严禁只罗列功能点、严禁省略下周工作、严禁只输出其中一段。

                注意：
                1. 我需要你调用工具来帮我回答问题，不要自己编造答案
                2. 查询类问题不要随意修改查询结果；但"周报/总结/汇总"类属于生成任务，
                   必须按操作指南的归纳与格式规范组织语言，不要原样粘贴接口返回的 JSON
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

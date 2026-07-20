package cn.com.mfish.common.ai.tool;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 容错工具调用管理器 —— 解决LLM幻觉工具名问题
 * <p>
 * 当LLM虚构了不存在的工具名（如 list_dashboards）时，
 * {@link DefaultToolCallingManager} 会直接抛出 {@link IllegalStateException}，
 * 中断整个对话流程，用户看到错误信息。
 * </p>
 * <p>
 * 本类替换默认实现，在工具不存在时返回提示信息给LLM，让其重新选择可用工具：
 * <pre>
 * "工具 [list_dashboards] 不存在。可用工具列表：[queryDashboard, getDashboardById, ...]。
 *  请从上述可用工具中选择一个合适的工具重新调用。"
 * </p>
 * </p>
 * <p>
 * 同时兼容工具执行异常（如权限不足、远程调用失败），将异常信息返回给LLM而非中断流程。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/15
 */
@Slf4j
public class FaultTolerantToolCallingManager implements ToolCallingManager {

    private final ToolCallingManager delegate;

    public FaultTolerantToolCallingManager(ToolCallingManager delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NonNull List<ToolDefinition> resolveToolDefinitions(@NonNull ToolCallingChatOptions chatOptions) {
        return delegate.resolveToolDefinitions(chatOptions);
    }

    /**
     * 执行工具调用，捕获工具不存在异常，返回提示信息让LLM重新选择
     */
    @Override
    public @NonNull ToolExecutionResult executeToolCalls(@NonNull Prompt prompt, @NonNull ChatResponse chatResponse) {
        try {
            return delegate.executeToolCalls(prompt, chatResponse);
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("No ToolCallback found for tool name")) {
                // LLM 幻觉了工具名，返回提示信息让LLM重新选择
                log.warn("[FaultTolerantToolCallingManager] LLM 虚构了不存在的工具: {}", e.getMessage());
                return buildToolNotFoundResult(prompt, chatResponse, e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            // 其他异常也返回给LLM，避免中断流程
            log.warn("[FaultTolerantToolCallingManager] 工具执行异常，返回错误信息给LLM: {}", e.getMessage());
            return buildErrorResult(prompt, chatResponse, e.getMessage());
        }
    }

    /**
     * 构造"工具不存在"的结果，返回可用工具列表让LLM重新选择
     */
    private ToolExecutionResult buildToolNotFoundResult(Prompt prompt, ChatResponse chatResponse,
                                                        String errorMessage) {
        // 提取LLM请求调用的工具名
        String toolName = extractToolNameFromError(errorMessage);

        // 获取可用工具列表
        String availableTools = getAvailableToolNames(prompt);

        // 构造提示信息
        String hint = String.format(
                "工具 [%s] 不存在。可用工具列表: [%s]。请从上述可用工具中选择一个最合适的工具重新调用。" +
                        "不要虚构工具名，只能使用上述列表中的工具。",
                toolName, availableTools);

        return buildToolResponseResult(prompt, chatResponse, toolName, hint);
    }

    /**
     * 构造通用错误结果，返回错误信息给LLM
     */
    private ToolExecutionResult buildErrorResult(Prompt prompt, ChatResponse chatResponse,
                                                 String errorMessage) {
        // 从 chatResponse 中提取工具调用信息
        List<AssistantMessage.ToolCall> toolCalls = extractToolCalls(chatResponse);
        String toolName = toolCalls.isEmpty() ? "unknown" : toolCalls.getFirst().name();

        String hint = String.format("工具 [%s] 执行失败，错误信息: %s。请根据错误信息调整参数后重试，或选择其他工具。",
                toolName, errorMessage);

        return buildToolResponseResult(prompt, chatResponse, toolName, hint);
    }

    /**
     * 构造工具响应结果（把提示信息作为 ToolResponseMessage 返回给LLM）
     */
    private ToolExecutionResult buildToolResponseResult(Prompt prompt, ChatResponse chatResponse,
                                                        String toolName, String responseData) {
        List<AssistantMessage.ToolCall> toolCalls = extractToolCalls(chatResponse);

        List<ToolResponseMessage.ToolResponse> responses = new ArrayList<>();
        for (AssistantMessage.ToolCall toolCall : toolCalls) {
            responses.add(new ToolResponseMessage.ToolResponse(
                    toolCall.id(),
                    toolCall.name(),
                    toolCall.name().equals(toolName) ? responseData : "工具执行完成"
            ));
        }

        ToolResponseMessage toolResponseMessage = ToolResponseMessage.builder()
                .responses(responses)
                .build();

        // 构造对话历史：原始消息 + AssistantMessage + ToolResponseMessage
        List<Message> conversationHistory = new ArrayList<>(prompt.getInstructions());

        // 添加 AssistantMessage（包含工具调用请求）
        chatResponse.getResults().stream()
                .map(Generation::getOutput)
                .filter(msg -> !CollectionUtils.isEmpty(msg.getToolCalls()))
                .findFirst().ifPresent(conversationHistory::add);

        conversationHistory.add(toolResponseMessage);

        return ToolExecutionResult.builder()
                .conversationHistory(conversationHistory)
                .returnDirect(false)
                .build();
    }

    /**
     * 从异常消息中提取工具名
     * <p>
     * 异常消息格式: "No ToolCallback found for tool name: list_dashboards"
     * </p>
     */
    private String extractToolNameFromError(String errorMessage) {
        int idx = errorMessage.lastIndexOf(':');
        if (idx > 0 && idx < errorMessage.length() - 1) {
            return errorMessage.substring(idx + 1).trim();
        }
        return "unknown";
    }

    /**
     * 获取本次请求中可用的工具名列表
     */
    private String getAvailableToolNames(Prompt prompt) {
        if (prompt.getOptions() instanceof ToolCallingChatOptions chatOptions
                && !CollectionUtils.isEmpty(chatOptions.getToolCallbacks())) {
            return chatOptions.getToolCallbacks().stream()
                    .map(tc -> tc.getToolDefinition().name())
                    .collect(Collectors.joining(", "));
        }
        return "";
    }

    /**
     * 从 ChatResponse 中提取工具调用列表
     */
    private List<AssistantMessage.ToolCall> extractToolCalls(ChatResponse chatResponse) {
        return chatResponse.getResults().stream()
                .map(Generation::getOutput)
                .filter(msg -> !CollectionUtils.isEmpty(msg.getToolCalls()))
                .flatMap(msg -> msg.getToolCalls().stream())
                .toList();
    }

    /**
     * 创建默认配置的容错 ToolCallingManager
     * <p>
     * 内部委托给 {@link DefaultToolCallingManager}，使用 Spring AI 默认配置。
     * </p>
     */
    public static FaultTolerantToolCallingManager createDefault() {
        return new FaultTolerantToolCallingManager(DefaultToolCallingManager.builder().build());
    }
}

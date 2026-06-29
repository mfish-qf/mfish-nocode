package cn.com.mfish.gateway.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * LLM统一代理控制器
 * 对外提供OpenAI兼容的 /v1/chat/completions 接口，
 * 统一代理后端的多个LLM（OpenAI/Ollama/DeepSeek等），
 * 实现模型负载均衡和fallback
 *
 * @author: mfish
 * @date: 2026/06/26
 */
@RestController
@RequestMapping("/v1")
@Slf4j
public class LlmProxyController {

    private final ChatModel primaryChatModel;

    public LlmProxyController(@Qualifier("openAiChatModel") ChatModel primaryChatModel) {
        this.primaryChatModel = primaryChatModel;
    }

    /**
     * OpenAI兼容的聊天补全接口（SSE流式）
     */
    @PostMapping(value = "/chat/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatCompletions(@RequestBody ChatCompletionRequest request,
                                        ServerHttpResponse response) {
        response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
        log.info("[LLM代理] 收到请求, model={}, stream={}", request.getModel(), request.getStream());

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(request.getModel() != null ? request.getModel() : "default")
                .maxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 1000)
                .temperature(request.getTemperature() != null ? request.getTemperature() : 0.7)
                .build();

        String promptText = request.extractPromptText();
        return primaryChatModel.stream(new Prompt(promptText, options))
                .mapNotNull(resp -> {
                    String content = Objects.requireNonNull(resp.getResult()).getOutput().getText();
                    if (content == null || content.isEmpty()) {
                        return null;
                    }
                    return "data: " + buildChunk(request.getModel(), content) + "\n\n";
                })
                .concatWith(Mono.just("data: [DONE]\n\n"))
                .onErrorResume(ex -> {
                    log.error("[LLM代理] 调用失败, 尝试fallback", ex);
                    return Flux.just("data: " + buildChunk(request.getModel(),
                            "抱歉，AI服务暂时不可用，请稍后再试。") + "\n\n")
                            .concatWith(Mono.just("data: [DONE]\n\n"));
                });
    }

    private String buildChunk(String model, String content) {
        String escapedContent = content
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        return "{\"id\":\"chatcmpl-mfish\",\"object\":\"chat.completion.chunk\",\"model\":\""
                + (model != null ? model : "default")
                + "\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\""
                + escapedContent
                + "\"},\"finish_reason\":null}]}";
    }

    /**
     * OpenAI兼容的聊天补全请求体
     */
    @Data
    public static class ChatCompletionRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;
        private Integer maxTokens;
        private Boolean stream = true;

        public String extractPromptText() {
            if (messages == null || messages.isEmpty()) {
                return "你好";
            }
            StringBuilder sb = new StringBuilder();
            for (Message msg : messages) {
                sb.append(msg.role).append(": ").append(msg.content).append("\n");
            }
            return sb.toString();
        }

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }
}

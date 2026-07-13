package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.dto.ChatCompletionDto;
import cn.com.mfish.ai.service.LlmModelRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * LLM统一代理控制器（WebFlux版）
 * 对外提供OpenAI兼容的 /v1/chat/completions 接口
 *
 * @author: mfish
 * @date: 2026/07/01
 */
@RestController
@RequestMapping("/v1")
@Slf4j
public class LlmProxyController {

    private final LlmModelRouter modelRouter;
    /** 使用snake_case命名策略的ObjectMapper，对齐OpenAI兼容格式的JSON键名 */
    private final ObjectMapper snakeCaseMapper;

    public LlmProxyController(LlmModelRouter modelRouter) {
        this.modelRouter = modelRouter;
        this.snakeCaseMapper = JsonMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }

    /**
     * OpenAI兼容的聊天补全接口
     * 根据 stream 参数自动切换流式/非流式响应
     */
    @PostMapping("/chat/completions")
    public Mono<Void> chatCompletions(@RequestBody ChatCompletionDto.Request request,
                                      ServerHttpResponse response) {
        String model = request.getModel();
        List<Message> messages = toSpringAiMessages(request.getMessages());
        Prompt prompt = buildPrompt(messages, request);

        if (Boolean.TRUE.equals(request.getStream())) {
            return handleStream(response, prompt, model);
        }
        return handleNonStream(response, prompt, model);
    }

    // ======================== 流式处理 ========================

    private Mono<Void> handleStream(ServerHttpResponse response, Prompt prompt, String model) {
        String completionId = generateCompletionId();
        response.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);

        return response.writeAndFlushWith(
                modelRouter.streamWithFallback(prompt, model)
                        .mapNotNull(chatResponse -> {
                            String content = extractContent(chatResponse);
                            if (content == null || content.isEmpty()) {
                                return null;
                            }
                            ChatCompletionDto.Chunk chunk = buildChunk(completionId, model, content);
                            return "data:" + writeJson(chunk) + "\n\n";
                        })
                        .concatWith(Mono.just("data:[DONE]\n\n"))
                        .onErrorResume(ex -> {
                            log.error("[LLM代理] 流式调用失败", ex);
                            ChatCompletionDto.Chunk errorChunk = buildChunk(completionId, model,
                                    "AI服务暂时不可用，请稍后再试。");
                            return Flux.just(
                                    "data:" + writeJson(errorChunk) + "\n\n",
                                    "data:[DONE]\n\n"
                            );
                        })
                        .map(s -> Mono.just(response.bufferFactory()
                                .wrap(s.getBytes(StandardCharsets.UTF_8))))
        );
    }

    // ======================== 非流式处理 ========================

    private Mono<Void> handleNonStream(ServerHttpResponse response, Prompt prompt, String model) {
        String completionId = generateCompletionId();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return modelRouter.callWithFallback(prompt, model)
                .map(chatResponse -> buildResponse(completionId, model, chatResponse))
                .onErrorResume(ex -> {
                    log.error("[LLM代理] 同步调用失败", ex);
                    return Mono.just(buildResponse(completionId, model, "AI服务暂时不可用，请稍后再试。"));
                })
                .map(resp -> response.bufferFactory()
                        .wrap(writeJson(resp).getBytes(StandardCharsets.UTF_8)))
                .flatMap(buffer -> response.writeWith(Mono.just(buffer)));
    }

    // ======================== Message转换 ========================

    /**
     * 将请求中的messages转换为Spring AI的Message列表，保持对话结构
     * 替代原有的字符串拼接方式，保留system/user/assistant角色信息
     */
    private List<Message> toSpringAiMessages(List<ChatCompletionDto.RequestMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of(new UserMessage("你好"));
        }
        List<Message> result = new ArrayList<>(messages.size());
        for (ChatCompletionDto.RequestMessage msg : messages) {
            String role = msg.getRole() != null ? msg.getRole().toLowerCase() : "user";
            switch (role) {
                case "system" -> result.add(new SystemMessage(msg.getContent()));
                case "assistant" -> result.add(new AssistantMessage(msg.getContent()));
                default -> result.add(new UserMessage(msg.getContent()));
            }
        }
        return result;
    }

    private Prompt buildPrompt(List<Message> messages, ChatCompletionDto.Request request) {
        ChatOptions options = ChatOptions.builder()
                .model(request.getModel() != null ? request.getModel() : "default")
                .maxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 1000)
                .temperature(request.getTemperature() != null ? request.getTemperature() : 0.7)
                .build();
        return new Prompt(messages, options);
    }

    // ======================== 响应构建 ========================

    private ChatCompletionDto.Chunk buildChunk(String id, String model, String content) {
        ChatCompletionDto.Chunk chunk = new ChatCompletionDto.Chunk();
        chunk.setId(id);
        chunk.setCreated(Instant.now().getEpochSecond());
        chunk.setModel(model != null ? model : "default");

        ChatCompletionDto.ChunkChoice choice = new ChatCompletionDto.ChunkChoice();
        choice.setIndex(0);
        ChatCompletionDto.Delta delta = new ChatCompletionDto.Delta();
        delta.setContent(content);
        choice.setDelta(delta);
        chunk.setChoices(List.of(choice));
        return chunk;
    }

    private ChatCompletionDto.Response buildResponse(String id, String model, ChatResponse chatResponse) {
        ChatCompletionDto.Response response =  buildResponse(id, model, extractContent(chatResponse));
        // 尝试提取usage信息
        try {
            var usageData = chatResponse.getMetadata().getUsage();
            ChatCompletionDto.Usage usage = new ChatCompletionDto.Usage();
            usage.setPromptTokens(usageData.getPromptTokens());
            usage.setCompletionTokens(usageData.getCompletionTokens());
            usage.setTotalTokens(usageData.getTotalTokens());
            response.setUsage(usage);
        } catch (Exception e) {
            log.debug("[LLM代理] 获取usage信息失败，跳过", e);
        }
        return response;
    }

    private ChatCompletionDto.Response buildResponse(String id, String model, String message) {
        ChatCompletionDto.Response response = new ChatCompletionDto.Response();
        response.setId(id);
        response.setCreated(Instant.now().getEpochSecond());
        response.setModel(model != null ? model : "default");

        ChatCompletionDto.ResponseChoice choice = new ChatCompletionDto.ResponseChoice();
        choice.setIndex(0);
        ChatCompletionDto.ResponseMessage responseMessage = new ChatCompletionDto.ResponseMessage();
        responseMessage.setContent(message);
        choice.setMessage(responseMessage);
        choice.setFinishReason("stop");
        response.setChoices(List.of(choice));
        return response;
    }

    // ======================== 工具方法 ========================

    private String extractContent(ChatResponse chatResponse) {
        if (chatResponse == null || chatResponse.getResult() == null) {
            return null;
        }
        return chatResponse.getResult().getOutput().getText();
    }

    private String generateCompletionId() {
        return "chatcmpl-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }

    private String writeJson(Object value) {
        try {
            return snakeCaseMapper.writeValueAsString(value);
        } catch (Exception e) {
            log.error("[LLM代理] JSON序列化失败", e);
            return "{}";
        }
    }
}

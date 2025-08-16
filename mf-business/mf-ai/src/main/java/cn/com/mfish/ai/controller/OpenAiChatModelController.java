package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.entity.ChatResponseVo;
import cn.com.mfish.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @description: openai_chat_model
 * @author: mfish
 * @date: 2025-08-13
 * @version: V2.1.0
 */
@Tag(name = "AI聊天模型-openai")
@RestController
@RequestMapping("/openai")
public class OpenAiChatModelController {

    private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。";

    private final ChatModel openAiChatModel;

    public OpenAiChatModelController(ChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    /**
     * 最简单的使用方式，没有任何 LLMs 参数注入。
     *
     * @return String types.
     */
    @GetMapping("/chat/simple")
    @Parameters({
            @Parameter(name = "prompt", description = "提示词")
    })
    public String simpleChat(String prompt) {
        if (StringUtils.isEmpty(prompt)) {
            prompt = DEFAULT_PROMPT;
        }
        return openAiChatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Parameters({
            @Parameter(name = "id", description = "会话id"),
            @Parameter(name = "prompt", description = "提示词")
    })
    public Flux<ChatResponseVo> streamChat(String id, String prompt) {
        if (StringUtils.isEmpty(prompt)) {
            prompt = DEFAULT_PROMPT;
        }
        return openAiChatModel.stream(new Prompt(prompt))
                .mapNotNull(resp -> new ChatResponseVo().setId(id)
                        .setContent(resp.getResult().getOutput().getText()));
    }

    /**
     * 使用编程方式自定义 LLMs ChatOptions 参数， {@link org.springframework.ai.openai.OpenAiChatOptions}
     * 优先级高于在 application.yml 中配置的 LLMs 参数！
     */
    @GetMapping("/chat/custom")
    @Parameters({
            @Parameter(name = "prompt", description = "提示词")
    })
    public String customChat(String prompt) {
        if (StringUtils.isEmpty(prompt)) {
            prompt = DEFAULT_PROMPT;
        }
        OpenAiChatOptions customOptions = OpenAiChatOptions.builder()
                .topP(0.7)
                .model("gpt-4o")
                .maxTokens(1000)
                .temperature(0.8)
                .build();
        return openAiChatModel.call(new Prompt(prompt, customOptions)).getResult().getOutput().getText();
    }

}

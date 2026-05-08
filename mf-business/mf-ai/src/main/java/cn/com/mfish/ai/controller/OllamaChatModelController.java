package cn.com.mfish.ai.controller;

import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @description: ollama_chat_model
 * @author: mfish
 * @date: 2025-08-13
 * @version: V2.3.1
 */
@Tag(name = "AI聊天模型-ollama")
@RestController
@RequestMapping("/ollama")
public class OllamaChatModelController {

    /** 默认提示词 */
    private static final String DEFAULT_PROMPT = "你好，介绍下你自己吧。请用中文回答。";

    /** Ollama聊天模型 */
    private final ChatModel ollamaChatModel;

    /**
     * 构造函数，注入Ollama聊天模型
     *
     * @param ollamaChatModel Ollama聊天模型实例
     */
    public OllamaChatModelController(ChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
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
        return ollamaChatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
    }

    /**
     * 流式聊天接口，以SSE方式返回AI响应内容
     *
     * @param id     会话ID，用于标识一次对话
     * @param prompt 用户输入的提示词，为空时使用默认提示词
     * @return 流式响应内容
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Parameters({
            @Parameter(name = "id", description = "会话id"),
            @Parameter(name = "prompt", description = "提示词")
    })
    public Flux<ChatResponseVo> streamChat(String id, String prompt) {
        if (StringUtils.isEmpty(prompt)) {
            prompt = DEFAULT_PROMPT;
        }
        return ollamaChatModel.stream(new Prompt(prompt))
                .mapNotNull(resp -> new ChatResponseVo().setId(id)
                        .setContent(resp.getResult().getOutput().getText()));
    }

}

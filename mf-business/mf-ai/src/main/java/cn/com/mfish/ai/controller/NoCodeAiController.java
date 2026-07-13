package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.agent.NoCodeAssistant;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @description: 低代码中心助手
 * @author: mfish
 * @date: 2025/8/22
 */
@Tag(name = "低代码中心助手")
@RestController
@RequestMapping("/nocode")
@RequiredArgsConstructor
public class NoCodeAiController {

    private final NoCodeAssistant noCodeAssistant;

    /**
     * 低代码AI助手对话接口（流式响应）
     *
     * @param aiRequest AI请求参数
     * @return 返回流式对话响应
     */
    @PostMapping(value = "/assist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> noCodeAssistant(@RequestBody AiRequest aiRequest) {
        return noCodeAssistant.chat(aiRequest);
    }
}

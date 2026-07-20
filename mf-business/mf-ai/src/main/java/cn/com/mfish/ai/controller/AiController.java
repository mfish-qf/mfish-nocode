package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.agent.MfishAssistant;
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
 * @description: 摸鱼小助手
 * @author: mfish
 * @date: 2026/7/2
 */
@Tag(name = "摸鱼小助手")
@RestController
@RequestMapping("/assist")
@RequiredArgsConstructor
public class AiController {
    private final MfishAssistant mfishAssistant;
    /**
     * 与摸鱼小助手聊天
     *
     * @param aiRequest AI请求参数，包含会话ID和提示词
     * @return 聊天响应流
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> chat(@RequestBody AiRequest aiRequest) {
        return mfishAssistant.chat(aiRequest);
    }
}

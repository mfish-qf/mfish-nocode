package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.oauth.agent.OauthAssistant;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @description: 认证小助手
 * @author: mfish
 * @date: 2025/8/22
 */
@Tag(name = "认证小助手")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class OauthAiController {

    private final OauthAssistant oauthAssistant;

    @GetMapping(value = "/assist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Parameters({
            @Parameter(name = "id", description = "会话id"),
            @Parameter(name = "prompt", description = "提示词")
    })
    public Flux<ChatResponseVo> oauthAssistant(String id, String prompt) {
        return oauthAssistant.chat(id, prompt);
    }
}

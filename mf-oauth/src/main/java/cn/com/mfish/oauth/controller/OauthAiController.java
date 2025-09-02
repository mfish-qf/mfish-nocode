package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.oauth.agent.OauthAssistant;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping(value = "/assist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> oauthAssistant(@RequestBody AiRequest aiRequest) {
        return oauthAssistant.chat(aiRequest);
    }
}

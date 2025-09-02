package cn.com.mfish.sys.controller;

import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.sys.agent.MfishAssistant;
import cn.com.mfish.sys.agent.SysAssistant;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @description: 系统小助手
 * @author: mfish
 * @date: 2025/8/22
 */
@Tag(name = "系统小助手")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class SysAiController {

    private final MfishAssistant mfishAssistant;

    private final SysAssistant sysAssistant;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> chat(@RequestBody AiRequest aiRequest) {
        return mfishAssistant.chat(aiRequest);
    }

    @PostMapping(value = "/assist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> sysAssistant(@RequestBody AiRequest aiRequest) {
        return sysAssistant.chat(aiRequest);
    }
}

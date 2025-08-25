package cn.com.mfish.sys.controller;

import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.sys.agent.MfishAssistant;
import cn.com.mfish.sys.agent.SysAssistant;
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

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Parameters({
            @Parameter(name = "id", description = "会话id"),
            @Parameter(name = "prompt", description = "提示词")
    })
    public Flux<ChatResponseVo> chat(String id, String prompt) {
        return mfishAssistant.chat(id, prompt);
    }

    @GetMapping(value = "/assist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Parameters({
            @Parameter(name = "id", description = "会话id"),
            @Parameter(name = "prompt", description = "提示词")
    })
    public Flux<ChatResponseVo> sysAssistant(String id, String prompt) {
        return sysAssistant.chat(id, prompt);
    }
}

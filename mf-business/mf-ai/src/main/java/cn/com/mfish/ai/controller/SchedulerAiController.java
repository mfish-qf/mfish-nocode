package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.agent.SchedulerAssistant;
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
 * @description: 调度中心小助手
 * @author: mfish
 * @date: 2026/07/15
 */
@Tag(name = "调度中心小助手")
@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class SchedulerAiController {

    private final SchedulerAssistant schedulerAssistant;

    @PostMapping(value = "/assist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> schedulerAssistant(@RequestBody AiRequest aiRequest) {
        return schedulerAssistant.chat(aiRequest);
    }
}

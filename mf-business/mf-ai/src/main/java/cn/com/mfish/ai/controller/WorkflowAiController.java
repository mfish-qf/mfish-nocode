package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.agent.WorkflowAssistant;
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
 * @description: 工作流小助手
 * @author: mfish
 * @date: 2026/07/15
 */
@Tag(name = "工作流小助手")
@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
public class WorkflowAiController {

    private final WorkflowAssistant workflowAssistant;

    @PostMapping(value = "/assist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> workflowAssistant(@RequestBody AiRequest aiRequest) {
        return workflowAssistant.chat(aiRequest);
    }
}

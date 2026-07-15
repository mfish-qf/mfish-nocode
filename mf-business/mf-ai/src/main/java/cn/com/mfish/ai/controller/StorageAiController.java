package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.agent.StorageAssistant;
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
 * @description: 存储中心小助手
 * @author: mfish
 * @date: 2026/07/15
 */
@Tag(name = "存储中心小助手")
@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageAiController {

    private final StorageAssistant storageAssistant;

    @PostMapping(value = "/assist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> storageAssistant(@RequestBody AiRequest aiRequest) {
        return storageAssistant.chat(aiRequest);
    }
}

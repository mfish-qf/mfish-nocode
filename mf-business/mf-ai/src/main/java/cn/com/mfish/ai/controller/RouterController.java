package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.agent.AssistantRegistry;
import cn.com.mfish.ai.service.AiRouteService;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.AiRouterVo;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.prom.annotation.MetricsMonitor;
import cn.com.mfish.common.prom.annotation.MetricsMonitors;
import cn.com.mfish.common.prom.enums.MetricEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @description: 路由控制器
 * @author: mfish
 * @date: 2026/7/6
 */
@Tag(name = "路由控制器")
@RestController
@RequestMapping("/router")
@RequiredArgsConstructor
@Slf4j
public class RouterController {
    @Resource
    AiRouteService aiRouteService;
    @Resource
    AssistantRegistry assistantRegistry;

    /**
     * AI智能路由（单实例模式）
     * 同步阻塞等待AI路由决策完成后，通过AssistantRegistry直接调用目标助手，
     * 返回Flux<ChatResponseVo>由Spring MVC原生处理SSE流式响应，
     * 避免Servlet forward缓冲整个响应导致流式失效的问题
     */
    @Operation(summary = "Ai路由", description = "Ai路由")
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @MetricsMonitors({
            @MetricsMonitor(metricEnum = MetricEnum.MFISH_REQUEST_COUNT, tagValues = {"POST", "/ai/router"}),
            @MetricsMonitor(metricEnum = MetricEnum.MFISH_REQUEST_DURATION, tagValues = {"POST", "/ai/router"})
    })
    public Flux<ChatResponseVo> aiRouter(@RequestBody AiRequest aiRequest) {
        String sessionId = aiRequest.getSessionId();
        String prompt = aiRequest.getMessage() != null ? aiRequest.getMessage().getContent() : null;
        log.info("[AI路由-单实例] 接收到智能路由请求, sessionId={}, prompt={}", sessionId, prompt);

        return aiRouteService.resolve(sessionId, prompt)
                .defaultIfEmpty(new AiRouterVo())
                .flatMapMany(vo -> {
                    String targetPath = vo.getPath();
                    log.info("[AI路由-单实例] 路由到, path={}", targetPath);
                    return assistantRegistry.chat(targetPath, aiRequest);
                });
    }
}

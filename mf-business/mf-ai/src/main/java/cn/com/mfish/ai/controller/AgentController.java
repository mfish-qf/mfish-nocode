package cn.com.mfish.ai.controller;

import cn.com.mfish.ai.agent.AgentRuntime;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 智能体控制器
 * <p>
 * 自主规划+多轮工具调用的 SSE 入口。
 * 接收用户需求，通过 {@link AgentRuntime} 拆解为步骤、按步调用工具，
 * 以事件流形式返回思考过程和最终结果。
 * </p>
 * <p>
 * 与 {@link RouterController} 的区别：
 * <ul>
 *   <li>RouterController：LLM 路由到单一助手，助手内部单次工具调用</li>
 *   <li>AgentController：先规划再执行，支持跨服务多步工具调用</li>
 * </ul>
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
@Tag(name = "智能体")
@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
@Slf4j
public class AgentController {

    private final AgentRuntime agentRuntime;

    /**
     * 智能体对话入口
     * <p>
     * 接收用户需求，AgentRuntime 拆解为步骤并逐步执行，
     * 返回事件流包含：计划生成、步骤开始/完成、流式 token、最终汇总。
     * </p>
     *
     * @param aiRequest AI请求参数
     * @return 事件流（SSE）
     */
    @Operation(summary = "智能体对话", description = "自主规划+多轮工具调用")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> agentChat(@RequestBody AiRequest aiRequest) {
        log.info("[AgentController] 接收智能体请求, id={}, sessionId={}",
                aiRequest.getId(), aiRequest.getSessionId());
        return agentRuntime.run(aiRequest);
    }
}

package cn.com.mfish.common.ai.agent;

import cn.com.mfish.common.ai.entity.ChatResponseVo;
import cn.com.mfish.common.ai.entity.EventType;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

/**
 * 事件总线
 * <p>
 * 基于 Reactor {@link Sinks.Many} 实现的事件广播器，
 * 用于在 AgentRuntime 编排过程中，把 Planner/Executor 产生的各类事件
 * 推送给下游订阅者（通常是 SSE Controller）。
 * </p>
 * <p>
 * 统一返回 {@link ChatResponseVo}，与普通聊天返回结构一致，前端按同一协议解析。
 * 每次会话创建独立实例，会话结束后调用 {@link #complete()} 关闭。
 * </p>
 * <p>
 * requestId 作为请求标识，会自动填充到所有 emit 出去的 ChatResponseVo.id 中，
 * 与普通聊天返回结构一致，前端可据此关联请求与响应。
 * </p>
 *
 * @author: mfish
 * @date: 2026/07/17
 */
@Slf4j
public class EventBus {

    /**
     * multicast 允许多订阅者；显式放大缓冲，避免长回复产生的大量 TOKEN_STREAM 事件溢出。
     * tryEmitNext 在缓冲满 / 无订阅者 / 并发未串行时会返回失败并静默丢弃事件，必须检查结果。
     */
    private final Sinks.Many<ChatResponseVo> sink = Sinks.many().multicast()
            .onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE * 16);

    /** 请求ID（来自 AiRequest.id），自动填充到所有事件的 id 字段 */
    private final String requestId;

    public EventBus(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 发布事件
     * <p>
     * 自动把 requestId 填充到 event.id（若 event.id 为空）。
     * </p>
     *
     * @param event 事件对象
     */
    public synchronized void emit(ChatResponseVo event) {
        if (event == null) return;
        if (event.getId() == null && requestId != null) {
            event.setId(requestId);
        }
        try {
            // synchronized 保证 emit 串行（multicast sink 要求串行发布，否则 FAIL_NON_SERIALIZED）
            Sinks.EmitResult result = sink.tryEmitNext(event);
            if (result.isFailure()) {
                log.warn("[EventBus] 事件发布失败: type={}, contentLen={}, result={}",
                        event.getType(),
                        event.getContent() != null ? event.getContent().length() : 0,
                        result);
            }
        } catch (Exception e) {
            log.warn("[EventBus] 事件发布异常: type={}", event.getType(), e);
        }
    }

    /**
     * 便捷方法：发布事件（自动包装）
     */
    public void emit(EventType type, String content) {
        emit(ChatResponseVo.ofEvent(type, content));
    }

    /**
     * 便捷方法：发布带步骤索引的事件
     */
    public void emit(EventType type, int stepIndex, String content) {
        emit(ChatResponseVo.ofEvent(type, stepIndex, content));
    }

    /**
     * 发布错误事件并关闭
     */
    public void emitError(String message) {
        emit(EventType.ERROR, message);
        complete();
    }

    /**
     * 关闭事件流，通知下游订阅者结束。
     * 与 emit 一样必须串行发布：若 tryEmitComplete 返回 FAIL_NON_SERIALIZED 而不重试，
     * 下游永远收不到 onComplete，SSE 连接会挂住直到网关超时，表现就是"回复停在中途"。
     */
    public synchronized void complete() {
        Sinks.EmitResult result = sink.tryEmitComplete();
        if (result.isFailure()) {
            log.warn("[EventBus] 关闭事件流失败: {}, 串行重试一次", result);
            result = sink.tryEmitComplete();
            if (result.isFailure()) {
                log.error("[EventBus] 关闭事件流仍失败: {}", result);
            }
        }
    }

    /**
     * 订阅事件流
     *
     * @return Flux of ChatResponseVo
     */
    public Flux<ChatResponseVo> asFlux() {
        return sink.asFlux();
    }
}

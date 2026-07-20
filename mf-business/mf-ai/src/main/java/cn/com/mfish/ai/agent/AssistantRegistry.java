package cn.com.mfish.ai.agent;

import cn.com.mfish.common.ai.client.IClientAssistant;
import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.ChatResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI助手注册中心
 * 自动发现所有IClientAssistant实现，按getPath()注册到路由映射表
 * 用于单实例模式下替代Servlet forward，直接调用助手实现真正的SSE流式传输
 *
 * @author: mfish
 * @date: 2026/07/02
 */
@Slf4j
@Component
public class AssistantRegistry {
    private static final String DEFAULT_PATH = "/ai/assist/chat";

    private final Map<String, IClientAssistant> assistantMap;
    private final IClientAssistant defaultAssistant;

    public AssistantRegistry(@Autowired(required = false) List<IClientAssistant> assistants) {
        if (assistants != null && !assistants.isEmpty()) {
            this.assistantMap = assistants.stream()
                    .collect(Collectors.toMap(IClientAssistant::getPath, a -> a));
            this.defaultAssistant = assistantMap.get(DEFAULT_PATH);
        } else {
            this.assistantMap = Map.of();
            this.defaultAssistant = null;
        }
        if (!assistantMap.isEmpty()) {
            log.info("[助手注册] 已注册AI助手: {}", assistantMap.keySet());
        }
    }

    /**
     * 根据路由路径获取助手
     *
     * @param path 网关路由路径，如/ai/assist/chat
     * @return 对应的助手实例，未找到时回退到默认助手
     */
    public IClientAssistant getAssistant(String path) {
        IClientAssistant assistant = assistantMap.get(path);
        if (assistant != null) {
            return assistant;
        }
        if (defaultAssistant != null) {
            log.warn("[助手注册] 未找到路径[{}]对应的助手，回退到默认助手", path);
            return defaultAssistant;
        }
        throw new IllegalStateException("无可用的AI助手，请检查IClientAssistant实现是否已注册");
    }

    /**
     * 根据路由路径调用助手聊天
     *
     * @param path      网关路由路径
     * @param aiRequest 聊天请求
     * @return 流式聊天响应
     */
    public Flux<ChatResponseVo> chat(String path, AiRequest aiRequest) {
        return getAssistant(path).chat(aiRequest);
    }
}
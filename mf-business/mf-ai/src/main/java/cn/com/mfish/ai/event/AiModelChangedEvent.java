package cn.com.mfish.ai.event;

import org.springframework.context.ApplicationEvent;

/**
 * 数据库中的AI模型配置发生变更（新增/编辑/删除）时发布此事件，
 * 用于触发 {@code LlmModelRouter} 重新加载模型。
 *
 * @author mfish
 */
public class AiModelChangedEvent extends ApplicationEvent {
    public AiModelChangedEvent(Object source) {
        super(source);
    }
}

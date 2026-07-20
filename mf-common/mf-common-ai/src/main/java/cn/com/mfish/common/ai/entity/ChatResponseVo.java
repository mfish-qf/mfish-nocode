package cn.com.mfish.common.ai.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 聊天返回对象
 * <p>
 * 统一封装普通聊天和智能体编排的返回内容，前端按同一结构解析。
 * </p>
 * <p>
 * 字段使用场景：
 * <ul>
 *   <li>普通聊天：使用 id/content/finishReason，type 和 stepIndex 为 null</li>
 *   <li>智能体编排：使用 type/stepIndex/content/timestamp，id 可为会话ID，finishReason 仅在结束时设置</li>
 * </ul>
 * </p>
 *
 * @author: mfish
 * @date: 2025/8/13
 */
@Data
@Accessors(chain = true)
public class ChatResponseVo {
    /** 消息ID（普通聊天用），智能体场景可为会话ID或步骤标识 */
    private String id;
    /** 事件类型（智能体编排用），普通聊天为 null */
    private EventType type;
    /** 当前步骤索引（智能体编排用，从 0 开始），普通聊天为 null */
    private Integer stepIndex;
    /** 消息内容：普通聊天为文本片段，智能体场景按 type 不同含义不同 */
    private String content;
    /** 结束原因：普通聊天为 STOP，智能体场景的 PLAN_COMPLETED 也可复用此字段 */
    private String finishReason;
    /** 时间戳（毫秒） */
    private Long timestamp;

    public ChatResponseVo() {
        this.timestamp = System.currentTimeMillis();
    }

    public ChatResponseVo(String content) {
        this();
        this.content = content;
    }

    public ChatResponseVo(EventType type, String content) {
        this(content);
        this.type = type;
    }

    public ChatResponseVo(EventType type, int stepIndex, String content) {
        this(type, content);
        this.stepIndex = stepIndex;
    }

    /**
     * 构建普通聊天的返回对象
     */
    public static ChatResponseVo ofChat(String id, String content, String finishReason) {
        return new ChatResponseVo()
                .setId(id)
                .setContent(content)
                .setFinishReason(finishReason);
    }

    /**
     * 构建智能体事件返回对象
     */
    public static ChatResponseVo ofEvent(EventType type, String content) {
        return new ChatResponseVo(type, content);
    }

    /**
     * 构建带步骤索引的智能体事件返回对象
     */
    public static ChatResponseVo ofEvent(EventType type, int stepIndex, String content) {
        return new ChatResponseVo(type, stepIndex, content);
    }
}

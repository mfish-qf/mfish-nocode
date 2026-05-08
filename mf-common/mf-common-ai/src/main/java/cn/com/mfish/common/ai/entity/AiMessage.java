package cn.com.mfish.common.ai.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: Ai消息
 * @author: mfish
 * @date: 2025/8/29
 */
@Data
@Accessors(chain = true)
public class AiMessage {
    /** 消息角色 */
    private String role;
    /** 消息内容 */
    private String content;
}

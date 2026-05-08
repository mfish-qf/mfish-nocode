package cn.com.mfish.common.ai.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 聊天返回对象
 * @author: mfish
 * @date: 2025/8/13
 */
@Data
@Accessors(chain = true)
public class ChatResponseVo {
    /** 消息ID */
    private String id;
    /** 消息内容 */
    private String content;
    /** 结束原因 */
    private String finishReason;

}

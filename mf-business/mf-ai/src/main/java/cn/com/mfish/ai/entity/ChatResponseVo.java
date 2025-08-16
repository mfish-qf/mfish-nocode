package cn.com.mfish.ai.entity;

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
    private String id;
    private String content;

}

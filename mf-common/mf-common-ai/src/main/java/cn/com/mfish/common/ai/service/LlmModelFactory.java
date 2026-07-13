package cn.com.mfish.common.ai.service;

import cn.com.mfish.common.ai.enums.LlmProtocol;
import org.springframework.ai.chat.model.ChatModel;

/**
 * LLM模型工厂
 * @author mfish
 * @date 2026/07/10
 */
public class LlmModelFactory {

    /**
     * 创建LLM模型
     * @param protocol 接入协议
     * @param apiKey API密钥
     * @param baseUrl 基础URL
     * @param modelName 模型名称
     * @param maxTokens 最大令牌数
     * @param temperature 温度
     * @return LLM模型
     */
       public static ChatModel create(String protocol, String apiKey,
                                   String baseUrl, String modelName,
                                   Integer maxTokens, Double temperature) {
        return LlmProtocol.fromValue(protocol).create(apiKey, baseUrl, modelName, maxTokens, temperature);
    }
}
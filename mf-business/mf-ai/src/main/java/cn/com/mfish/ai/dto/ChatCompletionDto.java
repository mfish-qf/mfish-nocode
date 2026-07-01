package cn.com.mfish.ai.dto;

import lombok.Data;

import java.util.List;

/**
 * OpenAI兼容的Chat Completion请求/响应DTO
 * 字段名直接对齐JSON键名，无需额外注解
 *
 * @author: mfish
 * @date: 2026/07/01
 */
public class ChatCompletionDto {

    // ======================== 请求 ========================

    @Data
    public static class Request {
        private String model;
        private List<RequestMessage> messages;
        private Double temperature;
        /** max_tokens */
        private Integer maxTokens;
        private Boolean stream = true;
    }

    @Data
    public static class RequestMessage {
        private String role;
        private String content;
    }

    // ======================== 流式响应 ========================

    @Data
    public static class Chunk {
        private String id = "chatcmpl-" + java.util.UUID.randomUUID().toString()
                .replace("-", "").substring(0, 24);
        private String object = "chat.completion.chunk";
        private Long created;
        private String model;
        private List<ChunkChoice> choices;
    }

    @Data
    public static class ChunkChoice {
        private Integer index = 0;
        private Delta delta;
        /** finish_reason */
        private String finishReason;
    }

    @Data
    public static class Delta {
        private String role;
        private String content;
    }

    // ======================== 非流式响应 ========================

    @Data
    public static class Response {
        private String id;
        private String object = "chat.completion";
        private Long created;
        private String model;
        private List<ResponseChoice> choices;
        private Usage usage;
    }

    @Data
    public static class ResponseChoice {
        private Integer index = 0;
        private ResponseMessage message;
        /** finish_reason */
        private String finishReason;
    }

    @Data
    public static class ResponseMessage {
        private String role = "assistant";
        private String content;
    }

    @Data
    public static class Usage {
        /** prompt_tokens */
        private Integer promptTokens;
        /** completion_tokens */
        private Integer completionTokens;
        /** total_tokens */
        private Integer totalTokens;
    }
}

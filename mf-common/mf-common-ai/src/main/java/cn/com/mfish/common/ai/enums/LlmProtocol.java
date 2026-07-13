package cn.com.mfish.common.ai.enums;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.Arrays;

/**
 * LLM接入协议
 * @author mfish
 * @date 2026/07/10
 */
public enum LlmProtocol {

    OPENAI("openai") {
        @Override
        public ChatModel create(String apiKey, String baseUrl, String model, Integer maxTokens, Double temperature) {
            return OpenAiChatModel.builder()
                    .options(OpenAiChatOptions.builder()
                            .apiKey(apiKey)
                            .model(model)
                            .temperature(temperature)
                            .maxTokens(maxTokens)
                            .baseUrl(baseUrl)
                            .build())
                    .build();
        }
    },
    OLLAMA("ollama") {
        @Override
        public ChatModel create(String apiKey, String baseUrl, String model, Integer maxTokens, Double temperature) {
            var api = OllamaApi.builder()
                    .baseUrl(baseUrl)
                    .build();
            var options = OllamaChatOptions.builder()
                    .model(OllamaModel.valueOf(model))
                    .numPredict(maxTokens)
                    .temperature(temperature)
                    .build();
            return OllamaChatModel.builder().ollamaApi(api).options(options).build();
        }
    },
    DEEPSEEK("deepseek") {
        @Override
        public ChatModel create(String apiKey, String baseUrl, String model, Integer maxTokens, Double temperature) {
            DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                    .apiKey(apiKey)
                    .baseUrl(baseUrl)
                    .build();
            DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                    .model(model)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .build();
            return DeepSeekChatModel.builder()
                    .deepSeekApi(deepSeekApi)
                    .options(options)
                    .build();
        }
    },
    ANTHROPIC("anthropic") {
        @Override
        public ChatModel create(String apiKey, String baseUrl, String model, Integer maxTokens, Double temperature) {
            return AnthropicChatModel.builder()
                    .options(AnthropicChatOptions.builder()
                            .model(model)
                            .maxTokens(maxTokens)
                            .temperature(temperature)
                            .apiKey(apiKey)
                            .baseUrl(baseUrl)
                            .build())
                    .build();
        }
    };

    private final String value;

    LlmProtocol(String value) {
        this.value = value;
    }

    public abstract ChatModel create(String apiKey, String baseUrl, String model, Integer maxTokens, Double temperature);

    public static LlmProtocol fromValue(String value) {
        return Arrays.stream(values())
                .filter(p -> p.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的接入协议: " + value));
    }

    @Override
    public String toString() {
        return value;
    }
}
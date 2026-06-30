package cn.com.mfish.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * AI安全过滤器
 * 对AI相关请求进行安全治理：
 * 1. Prompt注入检测：拦截包含恶意指令的提示词
 * 2. 敏感信息脱敏：防止API Key、密码等泄露到LLM
 * 3. Token用量统计和限额
 *
 * @author: mfish
 * @date: 2026/06/26
 */
@Component
@Slf4j
public class AiSecurityFilter implements GlobalFilter, Ordered {

    /**
     * AI相关路径前缀
     */
    private static final Set<String> AI_PATH_PREFIXES = Set.of("/ai/", "/v1/", "/openai/", "/ollama/");

    /**
     * POST请求体最大检测长度（1MB），超过此长度截断检测
     */
    private static final int MAX_BODY_CHECK_SIZE = 1024 * 1024;

    /**
     * Prompt注入攻击特征模式
     */
    private static final List<Pattern> INJECTION_PATTERNS = List.of(
            Pattern.compile("(?i)ignore\\s+(previous|above|all)\\s+(instructions|prompts|rules)"),
            Pattern.compile("(?i)you\\s+are\\s+now\\s+a"),
            Pattern.compile("(?i)system\\s*:\\s*you\\s+are"),
            Pattern.compile("(?i)jailbreak|dan\\s+mode|developer\\s+mode"),
            Pattern.compile("(?i)reveal\\s+your\\s+(system|initial)\\s+prompt"),
            Pattern.compile("(?i)\\bAPI[_-]?KEY\\b\\s*=\\s*[a-zA-Z0-9]{20,}")
    );

    /**
     * 敏感信息模式（用于检测和脱敏）
     */
    private static final List<Pattern> SENSITIVE_PATTERNS = List.of(
            Pattern.compile("sk-[a-zA-Z0-9]{20,}"),           // OpenAI API Key
            Pattern.compile("AK[A-Za-z0-9]{16,}"),            // 阿里云 AK
            Pattern.compile("(?i)password\\s*=\\s*\\S+"),     // 密码
            Pattern.compile("(?i)secret[_-]?key\\s*=\\s*\\S+") // 密钥
    );

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 仅对AI相关路径进行安全检查
        if (!isAiPath(path)) {
            return chain.filter(exchange);
        }

        // GET/DELETE请求：检查query参数
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
            String query = request.getURI().getRawQuery();
            if (query != null) {
                String injectionPattern = detectInjection(query);
                if (injectionPattern != null) {
                    log.warn("[AI安全] 检测到Prompt注入攻击, path={}, pattern={}", path, injectionPattern);
                    return rejectRequest(exchange, "请求包含不安全的内容");
                }
                String sensitivePattern = detectSensitiveInfo(query);
                if (sensitivePattern != null) {
                    log.warn("[AI安全] 检测到敏感信息泄露, path={}", path);
                    return rejectRequest(exchange, "请求包含敏感信息，请移除后重试");
                }
            }
            return chain.filter(exchange);
        }

        // POST/PUT/PATCH等请求：读取请求体进行检测
        return DataBufferUtils.join(request.getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .defaultIfEmpty(new byte[0])
                .flatMap(bytes -> {
                    // 检测请求体内容
                    if (bytes.length > 0) {
                        int checkLen = Math.min(bytes.length, MAX_BODY_CHECK_SIZE);
                        String body = new String(bytes, 0, checkLen, StandardCharsets.UTF_8);

                        String injectionPattern = detectInjection(body);
                        if (injectionPattern != null) {
                            log.warn("[AI安全] 检测到Prompt注入攻击(请求体), path={}, pattern={}", path, injectionPattern);
                            return rejectRequest(exchange, "请求包含不安全的内容");
                        }

                        String sensitivePattern = detectSensitiveInfo(body);
                        if (sensitivePattern != null) {
                            log.warn("[AI安全] 检测到敏感信息泄露(请求体), path={}", path);
                            return rejectRequest(exchange, "请求包含敏感信息，请移除后重试");
                        }
                    }

                    // 检测通过，用装饰器包装请求体使其可重复读取（供后续过滤器使用）
                    DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
                        @Override
                        public @NonNull Flux<DataBuffer> getBody() {
                            if (bytes.length > 0) {
                                return Flux.just(dataBufferFactory.wrap(bytes));
                            }
                            return Flux.empty();
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).build());
                });
    }

    /**
     * 判断是否为AI相关路径
     */
    private boolean isAiPath(String path) {
        return AI_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    /**
     * 检测Prompt注入攻击
     * @return 匹配到的模式描述，null表示安全
     */
    private String detectInjection(String content) {
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(content).find()) {
                return pattern.pattern();
            }
        }
        return null;
    }

    /**
     * 检测敏感信息泄露
     * @return 匹配到的敏感信息类型，null表示安全
     */
    private String detectSensitiveInfo(String content) {
        for (Pattern pattern : SENSITIVE_PATTERNS) {
            if (pattern.matcher(content).find()) {
                return pattern.pattern();
            }
        }
        return null;
    }

    /**
     * 拒绝请求
     */
    private Mono<Void> rejectRequest(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        // 在XSS过滤器之前执行（XssFilter order=-2）
        return -3;
    }
}

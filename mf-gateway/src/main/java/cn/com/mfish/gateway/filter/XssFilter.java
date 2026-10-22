package cn.com.mfish.gateway.filter;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.http.EscapeUtil;
import cn.com.mfish.common.core.config.XssProperties;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.Resource;

import java.nio.charset.StandardCharsets;

/**
 * @author: mfish
 * @description: XSS攻击防护过滤器，对请求体中的非法字符进行转义清理
 * @date: 2021/8/12 10:12
 */
@Component
@ConditionalOnProperty(value = "security.xss.enabled", havingValue = "true")
public class XssFilter implements GlobalFilter, Ordered {
    @Resource
    XssProperties xssProperties;

    /**
     * XSS过滤核心方法，对非GET/DELETE的JSON类型请求体进行XSS字符清理
     *
     * @param exchange 服务端Web交换对象
     * @param chain    网关过滤器链
     * @return 过滤结果
     */
    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        if (!xssProperties.getEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        // GET DELETE 不过滤
        HttpMethod method = request.getMethod();
        if (method.matches("GET") || method.matches("DELETE")) {
            return chain.filter(exchange);
        }
        // 非json类型，不过滤
        if (!isJsonRequest(exchange)) {
            return chain.filter(exchange);
        }
        // excludeUrls 不过滤
        String url = request.getURI().getPath();
        if (StringUtils.matches(url, xssProperties.getExcludeUrls())) {
            return chain.filter(exchange);
        }
        ServerHttpRequestDecorator httpRequestDecorator = requestDecorator(exchange);
        return chain.filter(exchange.mutate().request(httpRequestDecorator).build());
    }

    /**
     * 判断请求是否为JSON类型
     *
     * @param exchange 服务端Web交换对象
     * @return 是否为JSON请求
     */
    public boolean isJsonRequest(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        return StringUtils.startsWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 创建请求装饰器，对请求体内容进行XSS字符清理并重新封装请求
     *
     * @param exchange 服务端Web交换对象
     * @return 经过XSS清理的请求装饰器
     */
    private ServerHttpRequestDecorator requestDecorator(ServerWebExchange exchange) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @NotNull
            @Override
            public Flux<DataBuffer> getBody() {
                // 注意：request body 可能被 Netty 切成多个 DataBuffer 下发。
                // 必须先用 DataBufferUtils.join 聚合成完整字节流，再整体按 UTF-8 解码；
                // 否则多字节中文字符一旦跨越分片边界，逐片 new String(..., UTF_8) 会把跨边界
                // 的字符解码成非法序列并替换为 U+FFFD，表现为中文乱码
                // （如「竞赛名单列表」→「竞赛?单列表」局部损坏）。
                return DataBufferUtils.join(super.getBody())
                        .map(joined -> {
                            byte[] content = new byte[joined.readableByteCount()];
                            joined.read(content);
                            DataBufferUtils.release(joined);
                            String bodyStr = new String(content, StandardCharsets.UTF_8);
                            // 防xss攻击过滤
                            bodyStr = EscapeUtil.clean(bodyStr).trim();
                            // 转成字节：必须显式指定 UTF-8，禁止依赖 JVM 默认字符集(getBytes())
                            byte[] bytes = bodyStr.getBytes(StandardCharsets.UTF_8);
                            DataBuffer buffer = exchange.getResponse().bufferFactory().allocateBuffer(bytes.length);
                            buffer.write(bytes);
                            return buffer;
                        })
                        .switchIfEmpty(Mono.defer(() ->
                                Mono.just(exchange.getResponse().bufferFactory().allocateBuffer(0))))
                        .flux();
            }

            @NotNull
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                // 由于修改了请求体的body，导致content-length长度不确定，因此需要删除原先的content-length
                httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                return httpHeaders;
            }

        };
    }

    /**
     * 获取过滤器执行顺序，值为-2，优先级高于认证过滤器
     *
     * @return 过滤器顺序值
     */
    @Override
    public int getOrder() {
        return -2;
    }
}

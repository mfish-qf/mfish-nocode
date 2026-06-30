package cn.com.mfish.gateway.filter;

import cn.com.mfish.common.ai.entity.AiRouterVo;
import cn.com.mfish.common.ai.service.AiRouteService;
import cn.com.mfish.common.core.constants.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * AI智能路由过滤器（微服务网关模式）
 * 拦截 /aiRouter POST请求，读取请求体，
 * 通过AiRouteService决策后动态路由到目标微服务，
 * 请求体原样透传到目标接口，后续AiRequest增加字段无需修改此处
 * <p>
 * 过滤器顺序说明：
 * 必须在 RouteToRequestUrlFilter(10000) 之后、ReactiveLoadBalancerClientFilter(10150) 之前执行，
 * 这样才能覆盖RouteToRequestUrlFilter设置的GATEWAY_REQUEST_URL_ATTR，使路由指向AI决策的目标服务
 *
 * @author: mfish
 * @date: 2025/8/22
 */
@Component
@Slf4j
public class AiRouteFilter implements GlobalFilter, Ordered {

    private static final String AI_ROUTE_PATH = "/aiRouter";

    private final AiRouteService aiRouteService;

    public AiRouteFilter(AiRouteService aiRouteService) {
        this.aiRouteService = aiRouteService;
    }

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 只拦截AI路由POST请求
        if (!AI_ROUTE_PATH.equals(path) || request.getMethod() != HttpMethod.POST) {
            return chain.filter(exchange);
        }

        // 读取请求体，缓存后用于解析参数和透传到下游
        return DataBufferUtils.join(request.getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .defaultIfEmpty(new byte[0])
                .flatMap(bytes -> {
                    // 从请求体解析sessionId和prompt用于AI路由决策
                    String sessionId = null;
                    String prompt = null;
                    if (bytes.length > 0) {
                        String body = new String(bytes, StandardCharsets.UTF_8);
                        sessionId = extractJsonValue(body, "sessionId");
                        String content = extractJsonValue(body, "content");
                        if (content != null) {
                            prompt = content;
                        }
                    }
                    log.info("[AI路由] 接收到智能路由请求, sessionId={}, prompt={}", sessionId, prompt);

                    return aiRouteService.resolve(sessionId, prompt)
                            .flatMap(vo -> forwardTo(exchange, chain, vo, bytes));
                });
    }

    /**
     * 动态路由到目标微服务
     * 1. 将网关路径（如/sys/ai/assist）转换为服务实际路径（如/ai/assist）
     * 2. 通过 GATEWAY_REQUEST_URL_ATTR 设置目标URL，lb:// 协议触发 LoadBalancer 解析
     * 3. 同时修改请求URI路径，确保下游服务接收到正确的路径（而非原始/aiRouter）
     * 4. 请求体原样透传（用装饰器包装缓存后的byte数组供下游重复读取）
     */
    private Mono<Void> forwardTo(ServerWebExchange exchange, GatewayFilterChain chain, AiRouterVo vo, byte[] bodyBytes) {
        String serviceId = vo.getServiceId();
        String gatewayPath = vo.getPath();

        // 将网关路径转换为服务实际路径（去掉网关前缀，如/sys/ai/assist → /ai/assist）
        ServiceConstants.MfService mfService = ServiceConstants.MfService.fromValue(serviceId);
        String servicePath = mfService != null ? mfService.toServicePath(gatewayPath) : gatewayPath;

        // 构建目标URI，使用服务实际路径
        URI targetUri = URI.create("lb://" + serviceId + servicePath);
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, targetUri);

        // 修改请求URI，将原始路径/aiRouter替换为服务实际路径
        URI originalUri = exchange.getRequest().getURI();
        URI newUri = rebuildUri(originalUri, servicePath);

        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public @NonNull URI getURI() {
                return newUri;
            }

            @Override
            public @NonNull Flux<DataBuffer> getBody() {
                if (bodyBytes.length > 0) {
                    return Flux.just(dataBufferFactory.wrap(bodyBytes));
                }
                return Flux.empty();
            }
        };

        log.info("[AI路由] 动态路由到, targetUri={}, 网关路径={}, 服务路径={}", targetUri, gatewayPath, servicePath);
        return chain.filter(exchange.mutate().request(decorator).build());
    }

    /**
     * 重建URI，将原始路径替换为新路径，保留原始的query和fragment
     */
    private URI rebuildUri(URI originalUri, String newPath) {
        return URI.create(originalUri.getScheme() + "://"
                + originalUri.getRawAuthority()
                + newPath
                + (originalUri.getRawQuery() != null ? "?" + originalUri.getRawQuery() : "")
                + (originalUri.getRawFragment() != null ? "#" + originalUri.getRawFragment() : ""));
    }

    /**
     * 从JSON字符串中简单提取字段值（避免引入额外JSON库依赖）
     */
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(pattern);
        if (keyIndex < 0) {
            return null;
        }
        int colonIndex = json.indexOf(':', keyIndex + pattern.length());
        if (colonIndex < 0) {
            return null;
        }
        // 找值的起始位置
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && json.charAt(valueStart) == ' ') {
            valueStart++;
        }
        if (valueStart >= json.length()) {
            return null;
        }
        if (json.charAt(valueStart) == '"') {
            // 字符串值
            int valueEnd = json.indexOf('"', valueStart + 1);
            if (valueEnd < 0) {
                return null;
            }
            return json.substring(valueStart + 1, valueEnd);
        }
        // 非字符串值（数字、布尔等），取到逗号或}为止
        int valueEnd = valueStart;
        while (valueEnd < json.length() && json.charAt(valueEnd) != ',' && json.charAt(valueEnd) != '}') {
            valueEnd++;
        }
        return json.substring(valueStart, valueEnd).trim();
    }

    @Override
    public int getOrder() {
        // 必须在 RouteToRequestUrlFilter(10000) 之后，ReactiveLoadBalancerClientFilter(10150) 之前
        return 10100;
    }
}

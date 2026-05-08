package cn.com.mfish.gateway.filter;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author: mfish
 * @description: 获取请求参数，解决参数不能重复读取问题
 * @date: 2021/12/23 15:30
 */
@Component
public class CacheFilter extends AbstractGatewayFilterFactory<CacheFilter.Config> {

    /**
     * 构造函数，初始化缓存过滤器配置类
     */
    public CacheFilter() {
        super(Config.class);
    }

    /**
     * 缓存过滤器配置类，用于设置过滤器执行顺序
     */
    @Data
    public static class Config {
        /** 过滤器执行顺序 */
        private Integer order;
    }

    /**
     * 根据配置创建缓存请求的网关过滤器
     *
     * @param config 过滤器配置
     * @return 网关过滤器实例
     */
    @Override
    public @NonNull GatewayFilter apply(Config config) {
        CacheRequestGatewayFilter cacheRequestGatewayFilter = new CacheRequestGatewayFilter();
        Integer order = config.getOrder();
        if (order == null) {
            return cacheRequestGatewayFilter;
        }
        return new OrderedGatewayFilter(cacheRequestGatewayFilter, order);
    }

    /**
     * 缓存请求体数据的网关过滤器，将请求体缓存到字节数组中以支持重复读取
     */
    public static class CacheRequestGatewayFilter implements GatewayFilter {
        /**
         * 过滤请求，将非GET/DELETE请求的请求体数据进行缓存以支持重复读取
         *
         * @param exchange 服务端Web交换对象
         * @param chain    网关过滤器链
         * @return 过滤结果
         */
        @Override
        public @NonNull Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            // GET DELETE 不过滤
            HttpMethod method = exchange.getRequest().getMethod();
            if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
                return chain.filter(exchange);
            }
            return DataBufferUtils.join(exchange.getRequest().getBody()).map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                return bytes;
            }).defaultIfEmpty(new byte[0]).flatMap(bytes -> {
                DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
                ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @NotNull
                    @Override
                    public Flux<DataBuffer> getBody() {
                        if (bytes.length > 0) {
                            return Flux.just(dataBufferFactory.wrap(bytes));
                        }
                        return Flux.empty();
                    }
                };
                return chain.filter(exchange.mutate().request(decorator).build());
            });
        }
    }
}

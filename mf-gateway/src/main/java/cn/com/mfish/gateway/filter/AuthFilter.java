package cn.com.mfish.gateway.filter;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.entity.WeChatToken;
import cn.com.mfish.common.oauth.validator.TokenValidator;
import cn.com.mfish.gateway.common.GatewayUtils;
import cn.com.mfish.gateway.config.properties.IgnoreWhiteProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author: mfish
 * @description: 认证过滤器
 * @date: 2021/11/18 17:59
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Resource
    private IgnoreWhiteProperties ignoreWhiteProperties;
    @Resource
    private TokenValidator tokenValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        ServerHttpRequest.Builder mutate = request.mutate();
        // 内部请求来源参数清除
        GatewayUtils.removeHeader(mutate, RPCConstants.REQ_ORIGIN);
        Result<?> result = tokenValidator.validator(request);
        if (!result.isSuccess()) {
            // 如果校验不成功，但该页面在白名单内则直接跳过
            if (StringUtils.matches(url, ignoreWhiteProperties.getWhites())) {
                return chain.filter(exchange);
            }
            return unauthorizedResponse(exchange, result.getMsg());
        }
        if (result.getData() instanceof WeChatToken) {
            weChatTokenDeal((WeChatToken) result.getData(), mutate);
        } else {
            defaultTokenDeal((RedisAccessToken) result.getData(), mutate);
        }
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    /**
     * 默认token处理
     *
     * @param accessToken
     * @param mutate
     * @return
     */
    private void defaultTokenDeal(RedisAccessToken accessToken, ServerHttpRequest.Builder mutate) {
        GatewayUtils.addHeader(mutate, RPCConstants.REQ_TENANT_ID, accessToken.getTenantId());
        GatewayUtils.addHeader(mutate, RPCConstants.REQ_USER_ID, accessToken.getUserId());
        GatewayUtils.addHeader(mutate, RPCConstants.REQ_ACCOUNT, accessToken.getAccount());
    }

    /**
     * 微信token处理
     *
     * @param weChatToken
     * @param mutate
     * @return
     */
    private void weChatTokenDeal(WeChatToken weChatToken, ServerHttpRequest.Builder mutate) {
        GatewayUtils.addHeader(mutate, RPCConstants.REQ_TENANT_ID, weChatToken.getTenantId());
        GatewayUtils.addHeader(mutate, RPCConstants.REQ_USER_ID, weChatToken.getUserId());
        GatewayUtils.addHeader(mutate, RPCConstants.REQ_ACCOUNT, weChatToken.getAccount());
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED.value());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

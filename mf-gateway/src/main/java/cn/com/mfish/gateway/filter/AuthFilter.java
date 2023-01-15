package cn.com.mfish.gateway.filter;

import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.constants.HttpStatus;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.validator.AccessTokenValidator;
import cn.com.mfish.gateway.common.GatewayUtils;
import cn.com.mfish.gateway.config.properties.IgnoreWhiteProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
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
    private IgnoreWhiteProperties ignoreWhite;
    @Resource
    private AccessTokenValidator accessTokenValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        // 跳过不需要验证的路径
        if (StringUtils.matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        }
        Result<RedisAccessToken> result = accessTokenValidator.validate(request);
        if (!result.isSuccess()) {
            return unauthorizedResponse(exchange, result.getMsg());
        }
        ServerHttpRequest.Builder mutate = request.mutate();
        // 内部请求来源参数清除
        GatewayUtils.removeHeader(mutate, CredentialConstants.REQ_ORIGIN);
        GatewayUtils.addHeader(mutate, CredentialConstants.REQ_CLIENT_ID, result.getData().getClientId());
        GatewayUtils.addHeader(mutate, CredentialConstants.REQ_USER_ID, result.getData().getUserId());
        GatewayUtils.addHeader(mutate, CredentialConstants.REQ_ACCOUNT, result.getData().getAccount());
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        return ServletUtils.webFluxResponseWriter(exchange.getResponse(), msg, HttpStatus.UNAUTHORIZED);
    }


    @Override
    public int getOrder() {
        return -1;
    }
}

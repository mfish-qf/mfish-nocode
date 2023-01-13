package cn.com.mfish.gateway.filter;

import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.exception.CaptchaException;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.gateway.common.GatewayUtils;
import cn.com.mfish.gateway.config.properties.CaptchaProperties;
import cn.com.mfish.gateway.service.CheckCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: mfish
 * @date: 2021/8/12 9:59
 */
@Component
@Slf4j
public class CheckCodeFilter extends AbstractGatewayFilterFactory<Object> {
    private static final String CAPTCHA_VALUE = "captchaValue";
    private static final String CAPTCHA_KEY = "captchaKey";

    @Resource
    CheckCodeService checkCodeService;
    @Resource
    CaptchaProperties captchaProperties;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            boolean gatewayCheck = StringUtils.containsAnyIgnoreCase(request.getURI().getPath(), captchaProperties.getGatewayCheckCaptcha());
            boolean selfCheck = StringUtils.containsAnyIgnoreCase(request.getURI().getPath(), captchaProperties.getSelfCheckCaptcha());
            // 不需要校验验证码的地址、验证码关闭、get请求，不处理校验
            if ((!gatewayCheck && !selfCheck) || !captchaProperties.getEnabled() || request.getMethod() == HttpMethod.GET) {
                return chain.filter(exchange);
            }
            ServerHttpRequest.Builder mutate = request.mutate();
            GatewayUtils.removeHeader(mutate, CredentialConstants.REQ_CHECK_CAPTCHA_EXCEPTION);
            try {
                String rspStr = resolveBodyFromRequest(request);
                String[] params = rspStr.split("&");
                Map<String, String> map = new HashMap<>();
                for (String s : params) {
                    String[] kv = s.split("=");
                    if (kv != null && kv.length == 2) {
                        map.put(kv[0], kv[1]);
                    }
                }
                checkCodeService.checkCaptcha(map.get(CAPTCHA_VALUE), map.get(CAPTCHA_KEY));
            } catch (CaptchaException e) {
                if (selfCheck) {
                    GatewayUtils.addHeader(mutate, CredentialConstants.REQ_CHECK_CAPTCHA_EXCEPTION, e.getMessage());
                } else {
                    return ServletUtils.webFluxResponseWriter(exchange.getResponse(), e.getMessage());
                }
            }
            return chain.filter(exchange);
        };
    }

    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        // 获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }


}

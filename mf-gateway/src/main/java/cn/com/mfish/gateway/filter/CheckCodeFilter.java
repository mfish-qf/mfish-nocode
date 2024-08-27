package cn.com.mfish.gateway.filter;

import cn.com.mfish.common.captcha.common.CaptchaConstant;
import cn.com.mfish.common.captcha.config.properties.CaptchaProperties;
import cn.com.mfish.common.captcha.service.CheckCodeService;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.CaptchaException;
import cn.com.mfish.gateway.common.GatewayUtils;
import cn.com.mfish.gateway.common.ServletUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: mfish
 * @date: 2021/8/12 9:59
 */
@Component
@Slf4j
public class CheckCodeFilter extends AbstractGatewayFilterFactory<Object> {

    enum CheckStatus {
        需要校验,
        无需校验,
        参数校验
    }

    @Resource
    CheckCodeService checkCodeService;
    @Resource
    CaptchaProperties captchaProperties;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            CheckStatus gatewayCheck = isMatch(request.getURI().getPath(), captchaProperties.getGatewayCheckCaptcha());
            CheckStatus selfCheck = isMatch(request.getURI().getPath(), captchaProperties.getSelfCheckCaptcha());
            // 不需要校验验证码的地址、验证码关闭、get请求，不处理校验
            if (!captchaProperties.getEnabled() || request.getMethod() == HttpMethod.GET || (gatewayCheck == CheckStatus.无需校验 && selfCheck == CheckStatus.无需校验)) {
                return chain.filter(exchange);
            }
            ServerHttpRequest.Builder mutate = request.mutate();
            GatewayUtils.removeHeader(mutate, RPCConstants.REQ_CHECK_CAPTCHA_EXCEPTION);
            try {
                String rspStr = resolveBodyFromRequest(request);
                Map<String, String> map = getParamMap(rspStr);
                //判断是否还需要进行参数比对
                if (gatewayCheck == CheckStatus.参数校验 && unMatchParam(map, captchaProperties.getGatewayCheckCaptcha())) {
                    return chain.filter(exchange);
                }
                if (selfCheck == CheckStatus.参数校验 && unMatchParam(map, captchaProperties.getSelfCheckCaptcha())) {
                    return chain.filter(exchange);
                }
                checkCodeService.checkCaptcha(map.get(CaptchaConstant.CAPTCHA_VALUE), map.get(CaptchaConstant.CAPTCHA_KEY));
            } catch (CaptchaException e) {
                //如果需要自身校验，不直接返回校验结果，由网关转发到自身服务处理
                if (selfCheck != CheckStatus.无需校验) {
                    GatewayUtils.addHeader(mutate, RPCConstants.REQ_CHECK_CAPTCHA_EXCEPTION, e.getMessage());
                } else {
                    return ServletUtils.webFluxResponseWriter(exchange.getResponse(), e.getMessage());
                }
            }
            return chain.filter(exchange);
        };
    }

    /**
     * 获取请求中的参数串
     *
     * @param serverHttpRequest
     * @return
     */
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

    /**
     * 判断地址是否匹配，匹配则需要校验
     *
     * @param path
     * @param checkList
     * @return
     */
    private CheckStatus isMatch(String path, String[] checkList) {
        for (String cPath : checkList) {
            if (cPath.equals(path)) {
                return CheckStatus.需要校验;
            }
            if (cPath.startsWith(path + "?")) {
                return CheckStatus.参数校验;
            }
        }
        return CheckStatus.无需校验;
    }

    /**
     * 检测参数
     *
     * @param map       参数
     * @param checkList 检测列表
     * @return
     */
    private boolean unMatchParam(Map<String, String> map, String[] checkList) {
        List<Boolean> matchList = new ArrayList<>();
        for (String check : checkList) {
            int index = check.indexOf("?");
            //不带参数无需匹配
            if (index < 0) {
                continue;
            }
            boolean isMatch = true;
            Map<String, String> param = getParamMap(check.substring(index + 1));
            for (Map.Entry<String, String> entry : param.entrySet()) {
                //如果存在参数值不相等则没匹配到
                if (!entry.getValue().equals(map.get(entry.getKey()))) {
                    isMatch = false;
                    break;
                }
            }
            matchList.add(isMatch);
        }
        for (Boolean match : matchList) {
            //如果存在一个匹配到的表示已匹配到链接
            if (match) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取参数map
     *
     * @param param
     * @return
     */
    private Map<String, String> getParamMap(String param) {
        String[] params = param.split("&");
        Map<String, String> map = new HashMap<>();
        for (String s : params) {
            String[] kv = s.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

}

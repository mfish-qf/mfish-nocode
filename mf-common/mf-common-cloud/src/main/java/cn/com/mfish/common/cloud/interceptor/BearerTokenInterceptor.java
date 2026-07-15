package cn.com.mfish.common.cloud.interceptor;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;

/**
 * @author: mfish
 * @description: Feign请求令牌中继，防止令牌丢失（兼容 Servlet + WebFlux）
 * <p>
 * Servlet环境：从 RequestContextHolder → HttpServletRequest 转发头部
 * WebFlux环境：从 ServletUtils.EXCHANGE_HOLDER → ServerHttpRequest 转发头部
 * @date: 2021/12/13 10:19
 */
@Component
public class BearerTokenInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // Servlet环境
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            applyFromServlet(requestAttributes.getRequest(), requestTemplate);
            return;
        }
        // WebFlux环境
        ServerHttpRequest serverHttpRequest = ServletUtils.getServerHttpRequest();
        if (Objects.nonNull(serverHttpRequest)) {
            applyFromWebFlux(serverHttpRequest, requestTemplate);
        }
    }

    /**
     * Servlet环境：从HttpServletRequest转发所有头部
     */
    private void applyFromServlet(HttpServletRequest request, RequestTemplate requestTemplate) {
        //循环所有添加所有head信息
        Enumeration<String> headerNames = request.getHeaderNames();
        //是否存在头部token
        boolean hasHeadAuth = false;
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                //过滤content-length防止长度与body长度不一致
                if (Constants.CONTENT_LENGTH.equals(name.toLowerCase(Locale.ROOT))) {
                    continue;
                }
                if (Constants.AUTHENTICATION.equals(name)) {
                    hasHeadAuth = true;
                }
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    // 转发 header 到 Feign 请求
                    requestTemplate.header(name, values.nextElement());
                }
            }
        }
        if (hasHeadAuth) {
            return;
        }
        //重新添加token信息,防止token使用非head传入被遗漏问题
        String token = AuthInfoUtils.getAccessToken(request);
        if (StringUtils.isNotBlank(token)) {
            // 清除token头 避免传染
            requestTemplate.removeHeader(Constants.AUTHENTICATION);
            requestTemplate.header(Constants.AUTHENTICATION, Constants.OAUTH_HEADER_NAME + token);
        }
    }

    /**
     * WebFlux环境：从ServerHttpRequest转发所有头部
     */
    private void applyFromWebFlux(ServerHttpRequest serverRequest, RequestTemplate requestTemplate) {
        boolean[] hasHeadAuth = {false};
        org.springframework.http.HttpHeaders headers = serverRequest.getHeaders();
        headers.forEach((name, values) -> {
            //过滤content-length防止长度与body长度不一致
            if (Constants.CONTENT_LENGTH.equals(name.toLowerCase(Locale.ROOT))) {
                return;
            }
            if (Constants.AUTHENTICATION.equals(name)) {
                hasHeadAuth[0] = true;
            }
            for (String value : values) {
                // 转发 header 到 Feign 请求
                requestTemplate.header(name, value);
            }
        });
        if (hasHeadAuth[0]) {
            return;
        }
        //重新添加token信息,防止token使用非head传入被遗漏问题
        String token = AuthInfoUtils.getAccessToken(serverRequest);
        if (StringUtils.isNotBlank(token)) {
            // 清除token头 避免传染
            requestTemplate.removeHeader(Constants.AUTHENTICATION);
            requestTemplate.header(Constants.AUTHENTICATION, Constants.OAUTH_HEADER_NAME + token);
        }
    }
}

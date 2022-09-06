package cn.com.mfish.oauth.interceptor;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.AuthUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author ：qiufeng
 * @description：Feign请求令牌中继，防止令牌丢失
 * @date ：2021/12/13 10:19
 */
@Component
public class BearerTokenInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return;
        }
        //循环所有添加所有head信息
        HttpServletRequest request = requestAttributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                //feign.RetryableException: too many bytes written executing POST http://mf-sys/sysLog
                //过滤content-length防止长度与body长度不一致
                if (name.equals("content-length")) {
                    continue;
                }
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    requestTemplate.header(name, value);
                }
            }
        }
        //重新添加token信息,防止token试用非head传入被遗漏问题
        String token = AuthUtils.getAccessToken(request);
        if (!StringUtils.isEmpty(token)) {
            // 清除token头 避免传染
            requestTemplate.removeHeader(Constants.AUTHENTICATION);
            requestTemplate.header(Constants.AUTHENTICATION, Constants.OAUTH_HEADER_NAME + token);
        }
    }
}

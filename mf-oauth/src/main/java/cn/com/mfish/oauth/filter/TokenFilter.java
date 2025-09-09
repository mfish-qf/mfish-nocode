package cn.com.mfish.oauth.filter;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.entity.WeChatToken;
import cn.com.mfish.common.oauth.validator.TokenValidator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @description: token过滤器
 * @author: mfish
 * @date: 2024/1/30
 */
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Result<?> result = isAccessAllowed(servletRequest);
        if (result.isSuccess()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().print(result);
    }

    protected Result<?> isAccessAllowed(ServletRequest request) {
        TokenValidator tokenValidator = SpringBeanFactory.getBean(TokenValidator.class);
        Result<?> result = tokenValidator.validator(request);
        if (!result.isSuccess()) {
            return result;
        }
        //验证通过后请求中补充用户相关信息，单实例服务用户属性放在attribute中传递
        if (result.getData() instanceof WeChatToken token) {
            request.setAttribute(RPCConstants.REQ_ACCOUNT, token.getAccount());
            request.setAttribute(RPCConstants.REQ_USER_ID, token.getUserId());
            request.setAttribute(RPCConstants.REQ_TENANT_ID, token.getTenantId());
        } else {
            RedisAccessToken token = (RedisAccessToken) result.getData();
            request.setAttribute(RPCConstants.REQ_ACCOUNT, token.getAccount());
            request.setAttribute(RPCConstants.REQ_USER_ID, token.getUserId());
            request.setAttribute(RPCConstants.REQ_TENANT_ID, token.getTenantId());
        }
        return Result.ok();
    }

}

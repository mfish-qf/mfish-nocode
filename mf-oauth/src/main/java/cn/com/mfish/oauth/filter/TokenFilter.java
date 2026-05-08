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
 * @description: Token过滤器，校验请求中的访问令牌并在请求中注入用户信息
 * @author: mfish
 * @date: 2024/1/30
 */
public class TokenFilter implements Filter {

    /**
     * 过滤请求，校验Token有效性
     *
     * @param servletRequest  请求对象
     * @param servletResponse 响应对象
     * @param filterChain     过滤链
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
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

    /**
     * 校验请求是否允许访问，验证Token有效性并注入用户信息到请求属性
     *
     * @param request 请求对象
     * @return 校验结果
     */
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

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
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @description: Token过滤器，校验请求中的访问令牌并在请求中注入用户信息
 * @author: mfish
 * @date: 2024/1/30
 */
public class TokenFilter implements Filter {

    private List<String> whiteUrls = Collections.emptyList();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public TokenFilter() {
    }

    public TokenFilter(List<String> whiteUrls) {
        this.whiteUrls = whiteUrls != null ? whiteUrls : Collections.emptyList();
    }

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
        if (isWhiteUrl(servletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
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
     * 判断请求URL是否在白名单中
     */
    private boolean isWhiteUrl(ServletRequest request) {
        if (whiteUrls.isEmpty()) {
            return false;
        }
        if (request instanceof jakarta.servlet.http.HttpServletRequest httpRequest) {
            String uri = httpRequest.getRequestURI();
            // 去掉 context path 前缀
            String contextPath = httpRequest.getContextPath();
            if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
                uri = uri.substring(contextPath.length());
            }
            for (String pattern : whiteUrls) {
                if (pathMatcher.match(pattern, uri)) {
                    return true;
                }
            }
        }
        return false;
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

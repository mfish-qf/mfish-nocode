package cn.com.mfish.oauth.filter;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.entity.RedisAccessToken;
import cn.com.mfish.common.oauth.entity.WeChatToken;
import cn.com.mfish.common.oauth.validator.TokenValidator;
import cn.com.mfish.oauth.config.properties.WhitesProperties;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * @description: Token过滤器，校验请求中的访问令牌并在请求中注入用户信息
 * @author: mfish
 * @date: 2024/1/30
 */
@Component
public class TokenFilter extends OncePerRequestFilter {

    @Resource
    WhitesProperties whitesProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 过滤请求，校验Token有效性
     *
     * @param request  请求对象
     * @param response 响应对象
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Result<?> result = isAccessAllowed(request);
        if (result.isSuccess()) {
            filterChain.doFilter(request, response);
            return;
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().print(result);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
       return isWhiteUrl(request);
    }

    /**
     * 判断请求URL是否在白名单中
     */
    private boolean isWhiteUrl(ServletRequest request) {
        if (whitesProperties.getWhites().isEmpty()) {
            return false;
        }
        if (request instanceof jakarta.servlet.http.HttpServletRequest httpRequest) {
            String uri = httpRequest.getRequestURI();
            // 去掉 context path 前缀
            String contextPath = httpRequest.getContextPath();
            if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
                uri = uri.substring(contextPath.length());
            }
            for (String pattern : whitesProperties.getWhites()) {
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
        //验证通过后请求中补充用户相关信息，单实例服务用户属性放在attribute中传递
        String account = null;
        String userId = null;
        String tenantId = null;
        if (result.getData() instanceof WeChatToken token) {
            account = token.getAccount();
            userId = token.getUserId();
            tenantId = token.getTenantId();
        } else {
            RedisAccessToken token = (RedisAccessToken) result.getData();
            account = token.getAccount();
            userId = token.getUserId();
            tenantId = token.getTenantId();
        }
        request.setAttribute(RPCConstants.REQ_ACCOUNT, account);
        request.setAttribute(RPCConstants.REQ_USER_ID, userId);
        request.setAttribute(RPCConstants.REQ_TENANT_ID, tenantId);
        // 将认证信息写入SecurityContext，使Spring Security授权检查通过
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(account, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return Result.ok();
    }

}

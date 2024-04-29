package cn.com.mfish.boot.filter;

import cn.com.mfish.common.core.config.XssProperties;
import cn.com.mfish.common.core.utils.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @description: xss跨站脚本攻击过滤器
 * @author: mfish
 * @date: 2024/2/1
 */
@Component
@Order(-1)
public class XssFilter implements Filter {
    @Resource
    XssProperties xssProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (handleExcludeURL(req)) {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request) {
        if (!xssProperties.getEnabled()) {
            return true;
        }
        // GET DELETE 不过滤
        String method = request.getMethod();
        if (method == null || method.matches("GET") || method.matches("DELETE")) {
            return true;
        }
        // 非json类型，不过滤
        if (!isJsonRequest(request)) {
            return true;
        }
        String url = request.getServletPath();
        // excludeUrls 不过滤
        return StringUtils.matches(url, xssProperties.getExcludeUrls());
    }

    public boolean isJsonRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.CONTENT_TYPE);
        if (StringUtils.isEmpty(header)) {
            return false;
        }
        return header.startsWith(MediaType.APPLICATION_JSON_VALUE);
    }
}
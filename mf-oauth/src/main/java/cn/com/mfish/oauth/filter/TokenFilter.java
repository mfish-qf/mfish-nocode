package cn.com.mfish.oauth.filter;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @description: token过滤器
 * @author: mfish
 * @date: 2024/1/30
 */
public class TokenFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return true;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) {
        return true;
    }
}

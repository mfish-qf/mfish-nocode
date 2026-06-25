package cn.com.mfish.monitor.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

/**
 * @description: 自定义CSRF过滤器，将CSRF Token写入Cookie供前端JavaScript读取
 * Spring Security 7 默认延迟加载CSRF Token，CookieCsrfTokenRepository不会自动设置Cookie，
 * 必须通过此Filter强制将Token从请求属性中取出并写入XSRF-TOKEN Cookie，
 * 否则前端SPA无法获取Token，导致登录及所有POST请求被403拒绝
 *
 * @author: mfish
 * @date: 2026/6/25 22:24
 */
public class CustomCsrfFilter extends OncePerRequestFilter {

    public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
            Cookie cookie = WebUtils.getCookie(request, CSRF_COOKIE_NAME);
            String token = csrf.getToken();
            if (cookie == null || !token.equals(cookie.getValue())) {
                cookie = new Cookie(CSRF_COOKIE_NAME, token);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
        filterChain.doFilter(request, response);
    }
}

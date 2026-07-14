package cn.com.mfish.common.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;

import java.util.*;

/**
 * @description: Servlet工具类，提供请求、响应、会话等操作
 * @author: mfish
 * @date: 2021/8/13 9:38
 */
public class ServletUtils {

    private static final ThreadLocal<ServerWebExchange> EXCHANGE_HOLDER = new ThreadLocal<>();

    public static void setExchange(ServerWebExchange exchange) {
        EXCHANGE_HOLDER.set(exchange);
    }

    public static void removeExchange() {
        EXCHANGE_HOLDER.remove();
    }

    public static ServerWebExchange getExchange() {
        return EXCHANGE_HOLDER.get();
    }

    public static ServerHttpRequest getServerHttpRequest() {
        ServerWebExchange exchange = EXCHANGE_HOLDER.get();
        return exchange != null ? exchange.getRequest() : null;
    }
    /**
     * 获取String参数
     *
     * @return 返回与header名称相对应的header值如果请求对象为null或指定的header不存在，则返回null
     */
    public static String getHeader(String header) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getHeader(header);
        }
        ServerHttpRequest serverHttpRequest = getServerHttpRequest();
        if (serverHttpRequest != null) {
            return serverHttpRequest.getHeaders().getFirst(header);
        }
        return null;
    }

    /**
     * 获取String参数（双栈兼容）
     * Servlet环境通过HttpServletRequest获取，WebFlux环境通过ServerHttpRequest查询参数获取
     */
    public static String getParameter(String name) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getParameter(name);
        }
        ServerHttpRequest serverHttpRequest = getServerHttpRequest();
        if (serverHttpRequest != null) {
            List<String> values = serverHttpRequest.getQueryParams().get(name);
            if (values != null && !values.isEmpty()) {
                return values.getFirst();
            }
        }
        return null;
    }

    /**
     * 获取请求URI（双栈兼容）
     */
    public static String getRequestURI() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getRequestURI();
        }
        ServerHttpRequest serverHttpRequest = getServerHttpRequest();
        if (serverHttpRequest != null) {
            return serverHttpRequest.getURI().getPath();
        }
        return null;
    }

    /**
     * 获取请求方法（双栈兼容）
     */
    public static String getMethod() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getMethod();
        }
        ServerHttpRequest serverHttpRequest = getServerHttpRequest();
        if (serverHttpRequest != null) {
            return serverHttpRequest.getMethod().name();
        }
        return null;
    }

    /**
     * 获取客户端真实IP（双栈兼容）
     * 优先从Forwarded相关header获取，回退到远程地址
     */
    public static String getRemoteIP() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return Utils.getRemoteIP(request);
        }
        ServerHttpRequest serverHttpRequest = getServerHttpRequest();
        if (serverHttpRequest != null) {
            String ip = Utils.getFirstValidIp(
                    serverHttpRequest.getHeaders().getFirst("X-Client-IP"),
                    serverHttpRequest.getHeaders().getFirst("X-Forwarded-For"),
                    serverHttpRequest.getHeaders().getFirst("X-Real-IP"),
                    serverHttpRequest.getHeaders().getFirst("Forwarded"),
                    serverHttpRequest.getHeaders().getFirst("Proxy-Client-IP"),
                    serverHttpRequest.getHeaders().getFirst("WL-Proxy-Client-IP"),
                    serverHttpRequest.getRemoteAddress() != null
                            ? serverHttpRequest.getRemoteAddress().getAddress().getHostAddress()
                            : null);
            if (ip != null && ip.contains(",")) {
                ip = ip.substring(0, ip.indexOf(',')).trim();
            }
            if ("0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "127.0.0.1";
            }
            return ip;
        }
        return null;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        try {
            return Objects.requireNonNull(getRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 获取请求属性
     * @return 如果请求存在且指定的属性存在，则返回该属性值；否则返回null
     */
    public static Object getAttribute(String attr) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getAttribute(attr);
        }
        ServerWebExchange exchange = getExchange();
        if (exchange != null) {
            return exchange.getAttributes().get(attr);
        }
        return null;
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        try {
            return Objects.requireNonNull(getRequestAttributes()).getResponse();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return Objects.requireNonNull(getRequest()).getSession();
    }

    /**
     * 获取ServletRequestAttributes
     *
     * @return ServletRequestAttributes对象，获取失败返回null
     */
    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取请求中所有header信息
     *
     * @param request HTTP请求对象
     * @return header键值对Map
     */
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 将ParameterMap转换成map<string,string>格式
     *
     * @param request 请求
     * @return map
     */
    public static Map<String, String> getParameterStringMap(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();
        Map<String, String> returnMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : properties.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            String value;
            if (null == values || values.length == 0) {
                value = "";
            } else if (values.length > 1) {
                // 用于请求参数中有多个相同名称
                value = String.join(",", values);
            } else {
                value = values[0];
            }
            returnMap.put(key, value);
        }
        return returnMap;
    }
}
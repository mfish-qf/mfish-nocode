package cn.com.mfish.common.core.utils.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;

/**
 * @author: mfish
 * @description: 统一web请求
 * @date: 2021/12/9 11:46
 */
public class WebRequest {

    /**
     * 从不同类型的请求对象中获取参数值
     *
     * @param t    请求对象（支持NativeWebRequest、HttpServletRequest、ServerHttpRequest）
     * @param var1 参数名
     * @param <T>  请求类型
     * @return 参数值，未找到返回null
     */
    public static <T> String getParameter(T t, String var1) {
        if (t instanceof NativeWebRequest) {
            return ((NativeWebRequest) t).getParameter(var1);
        }
        if (t instanceof HttpServletRequest) {
            return ((HttpServletRequest) t).getParameter(var1);
        }
        if (t instanceof ServerHttpRequest) {
            List<String> list = ((ServerHttpRequest) t).getQueryParams().get(var1);
            if (list != null && !list.isEmpty()) {
                return list.getFirst();
            }
        }
        return null;
    }

    /**
     * 从不同类型的请求对象中获取请求头值
     *
     * @param t    请求对象（支持NativeWebRequest、HttpServletRequest、ServerHttpRequest）
     * @param var1 请求头名称
     * @param <T>  请求类型
     * @return 请求头值，未找到返回null
     */
    public static <T> String getHeader(T t, String var1) {
        if (t instanceof NativeWebRequest) {
            return ((NativeWebRequest) t).getHeader(var1);
        }
        if (t instanceof HttpServletRequest) {
            return ((HttpServletRequest) t).getHeader(var1);
        }
        if (t instanceof ServerHttpRequest) {
            return ((ServerHttpRequest) t).getHeaders().getFirst(var1);
        }
        return null;
    }
}

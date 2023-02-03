package cn.com.mfish.common.core.utils.http;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: mfish
 * @description: 统一web请求
 * @date: 2021/12/9 11:46
 */
public class WebRequest {

    public static <T> String getParameter(T t, String var1) {
        if (t instanceof NativeWebRequest) {
            return ((NativeWebRequest) t).getParameter(var1);
        }
        if (t instanceof HttpServletRequest) {
            return ((HttpServletRequest) t).getParameter(var1);
        }
        if (t instanceof ServerHttpRequest) {
            List<String> list = ((ServerHttpRequest) t).getQueryParams().get(var1);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        }
        return null;
    }

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

package cn.com.mfish.common.core.utils.html;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：qiufeng
 * @description：统一web请求
 * @date ：2021/12/9 11:46
 */
public class WebRequest<T> {
    NativeWebRequest nativeWebRequest = null;
    HttpServletRequest httpServletRequest = null;

    public WebRequest(T t) {
        if (t instanceof NativeWebRequest) {
            nativeWebRequest = (NativeWebRequest) t;
        } else if (t instanceof HttpServletRequest) {
            httpServletRequest = (HttpServletRequest) t;
        } else {
            throw new MyRuntimeException("错误:初始化类型不正确");
        }
    }

    public String getParameter(String var1) {
        if (nativeWebRequest != null) {
            return nativeWebRequest.getParameter(var1);
        }
        if (httpServletRequest != null) {
            return httpServletRequest.getParameter(var1);
        }
        return null;
    }

    public String getHeader(String var1) {
        if (nativeWebRequest != null) {
            return nativeWebRequest.getHeader(var1);
        }
        if (httpServletRequest != null) {
            return httpServletRequest.getHeader(var1);
        }
        return null;
    }
}

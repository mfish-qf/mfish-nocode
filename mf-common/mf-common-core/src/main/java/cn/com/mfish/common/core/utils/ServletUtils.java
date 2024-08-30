package cn.com.mfish.common.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * @author: mfish
 * @date: 2021/8/13 9:38
 */
public class ServletUtils {

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return Objects.requireNonNull(getRequest()).getParameter(name);
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
     * 获取header信息
     * 该方法用于从HTTP请求中提取指定的header信息
     *
     * @param header 指定的header名称，不能为空
     * @return 返回与header名称相对应的header值如果请求对象为null或指定的header不存在，则返回null
     */
    public static String getHeader(String header) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(header);
    }

    /**
     * 获取请求属性
     * <p>
     * 本方法旨在从HTTP请求中获取特定的属性值通过提供属性名称，它首先确保有一个有效的请求对象存在
     * 如果请求对象不存在，方法直接返回null表明在没有有效请求的情况下，无法获取属性
     * 当请求对象存在时，方法则根据提供的属性名从请求中提取相应的属性值
     *
     * @param attr 请求属性的名称，用于标识所需的属性
     * @return 如果请求存在且指定的属性存在，则返回该属性值；否则返回null
     */
    public static Object getAttribute(String attr) {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return request.getAttribute(attr);
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

    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes) attributes;
        } catch (Exception e) {
            return null;
        }
    }

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
        String value = "";
        for (Map.Entry<String, String[]> entry : properties.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (null == values) {
                value = "";

            } else if (values.length > 1) {
                //用于请求参数中有多个相同名称
                for (String s : values) {
                    value = "," + s;
                }
                value = value.substring(1);
            } else {
                value = values[0];
            }
            returnMap.put(key, value);
        }
        return returnMap;
    }
}
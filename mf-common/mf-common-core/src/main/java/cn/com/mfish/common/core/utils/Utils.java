package cn.com.mfish.common.core.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author ：qiufeng
 * @description：通用方法
 * @date ：2022/11/4 15:38
 */
public class Utils {
    /**
     * 反射获取类的所有字段
     *
     * @param object 对象
     * @return
     */
    public static List<Field> getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 32位UUID
     *
     * @return
     */
    public static String uuid32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 手机号脱敏
     *
     * @param value
     * @return
     */
    public static String phoneMasking(String value) {
        if (StringUtils.isEmpty(value) || value.length() != 11) {
            return value;
        }
        return value.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 获取请求用户IP
     *
     * @param request
     * @return
     */
    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(',') > 0) {
                ip = ip.substring(0, ip.indexOf(','));
            }
        }
        return ip;
    }
}

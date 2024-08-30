package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.config.ServiceProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author: mfish
 * @description: 通用方法
 * @date: 2022/11/4 15:38
 */
public class Utils {

    /**
     * 反射获取类的所有字段
     * 此方法通过使用反射机制，能够深入类的内部结构
     * 从而获取一个对象所属类的所有字段，包括继承而来的字段
     *
     * @param object 对象 用于获取其所属类的字段
     * @return 返回包含所有字段的列表
     */
    public static List<Field> getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        return getAllFields(clazz);
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 将一个Java对象转换为Map对象
     * 该方法通过反射机制获取对象的字段，并将字段名和对应的值存储到Map中
     *
     * @param object 需要转换的Java对象
     * @return 包含对象所有字段和对应值的Map
     * @throws IllegalAccessException 如果无法访问对象的字段，抛出该异常
     */
    public static Map<String, Object> beanToMap(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }

    /**
     * 将Map中的数据转换为指定的Bean对象
     *
     * @param map       待转换的Map对象，键为Bean属性名，值为属性值
     * @param beanClass 目标Bean的类类型
     * @param <T>       泛型标记，表示Bean的类型
     * @return 转换后的Bean对象
     * @throws Exception 如果转换过程中发生错误，抛出异常
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) throws Exception {
        T object = beanClass.getDeclaredConstructor().newInstance();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            if (map.containsKey(field.getName())) {
                field.set(object, map.get(field.getName()));
            }
        }
        return object;
    }

    /**
     * 32位UUID
     *
     * @return 返回32uuid
     */
    public static String uuid32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 手机号脱敏
     * 对给定的手机号进行脱敏处理，以保护个人隐私。脱敏后的手机号将中间四位数字替换为星号。
     * 例如，手机号13012345678将被脱敏为130****5678。
     *
     * @param value 待脱敏的手机号
     * @return 脱敏后的手机号。如果输入为空或长度不为11，则直接返回原手机号。
     */
    public static String phoneMasking(String value) {
        if (StringUtils.isEmpty(value) || value.length() != 11) {
            return value;
        }
        return value.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 获取请求用户IP
     * 该方法主要用于获取HTTP请求发起者的IP地址。它尝试通过多种方式获取IP，以应对不同的网络环境和代理配置。
     * 首先尝试从"X-Forwarded-For"头获取IP，这是HTTP协议中没有规定的扩展头，用来记载原始客户端IP地址。
     * 如果"X-Forwarded-For"不可用或为"unknown"，则尝试从"Proxy-Client-IP"和"WL-Proxy-Client-IP"中获取IP，
     * 这些通常是代理服务器设置的头信息。如果这些尝试都失败，则最终回退到使用请求对象的远程地址。
     * 对于通过多个代理的情况，取第一个IP作为客户端真实IP。
     *
     * @param request HTTP请求对象，包含了获取IP所需的所有头信息和请求数据。
     * @return 客户端的IP地址字符串。
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

    /**
     * 获取当前服务类型 是微服务还是单体服务
     *
     * @return 当前服务类型，如果是微服务则返回"cloud"，否则返回自定义类型
     */
    public static String getServiceType() {
        ServiceProperties properties = SpringBeanFactory.getBean(ServiceProperties.class);
        return StringUtils.isEmpty(properties.getType()) ? "cloud" : properties.getType();
    }
}

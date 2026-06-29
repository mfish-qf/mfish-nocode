package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.config.ServiceProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author: mfish
 * @description: 通用方法
 * @date: 2022/11/4 15:38
 */
@Slf4j
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

    /**
     * 反射获取类的所有字段，包括父类字段
     *
     * @param clazz 类对象
     * @return 返回包含所有字段的列表
     */
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
     * 获取请求用户真实IP
     * 优先从Forwarded头获取（Spring Boot 4.x默认启用ForwardedHeaderFilter，
     * 会将X-Forwarded-For合并到Forwarded头并在处理后移除X-Forwarded-*头，
     * 因此直接读取X-Forwarded-For可能为空），
     * 再依次尝试X-Forwarded-For、Proxy-Client-IP、WL-Proxy-Client-IP，
     * 最后回退到request.getRemoteAddr()。
     * 对于多级代理，取第一个IP作为客户端真实IP。
     *
     * @param request HTTP请求对象
     * @return 客户端真实IP地址
     */
    public static String getRemoteIP(HttpServletRequest request) {
        // Spring Boot 4.x: ForwardedHeaderFilter会合并X-Forwarded-For到Forwarded头
        // 格式: Forwarded: for=192.168.1.1;by=proxy;host=example.com;proto=https
        String forwarded = request.getHeader("Forwarded");
        if (StringUtils.isNotEmpty(forwarded)) {
            String ip = extractForwardedFor(forwarded);
            if (StringUtils.isNotEmpty(ip)) {
                return ip;
            }
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时，第一个IP为客户端真实IP，多个IP按','分割
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(',')).trim();
        }
        return ip;
    }

    /**
     * 从Forwarded头中提取for字段的IP地址
     * Forwarded头格式: for=192.168.1.1;by=proxy;host=example.com;proto=https
     * 多级代理格式: for=192.168.1.1, for=10.0.0.1
     *
     * @param forwarded Forwarded头内容
     * @return 第一个for字段的IP地址，提取失败返回null
     */
    private static String extractForwardedFor(String forwarded) {
        // 匹配 for=xxx 部分，取第一个
        for (String segment : forwarded.split(";")) {
            String trimmed = segment.trim();
            if (trimmed.toLowerCase().startsWith("for=")) {
                String value = trimmed.substring(4).trim();
                // 多个for值按逗号分隔，取第一个
                if (value.contains(",")) {
                    value = value.substring(0, value.indexOf(',')).trim();
                }
                // 去掉可能的引号包裹
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }
        }
        return null;
    }

    /**
     * 获取当前主机IP地址
     *
     * @return 主机IP地址，获取失败时返回127.0.0.1
     */
    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        }
        return "127.0.0.1";
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

    /**
     * 打印服务启动信息，包括swagger地址
     *
     * @param application Spring应用上下文
     */
    public static void printServerRun(ConfigurableApplicationContext application) {
        Environment env = application.getEnvironment();
        String ip = getHostIp();
        String port = env.getProperty("server.port");
        String title = env.getProperty("swagger.title");
        log.info("\n\t"
                + "\n\t-------------------------------------------------------------"
                + "\n\t"
                + "\n\t--------------------" + title + "启动成功-----------------------"
                + "\n\t"
                + "\n\tswagger地址:\thttp://" + ip + ":" + port + "/swagger-ui/index.html"
                + "\n\t"
        );
    }
}

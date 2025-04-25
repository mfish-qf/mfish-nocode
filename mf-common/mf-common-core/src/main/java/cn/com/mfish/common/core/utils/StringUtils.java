package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.constants.Constants;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author: mfish
 * @date: 2021/8/12 10:34
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';

    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * 是否为http(s)://开头
     *
     * @param link 链接
     * @return 结果
     */
    public static boolean isHttp(String link) {
        return StringUtils.startsWithAny(link, Constants.HTTP, Constants.HTTPS);
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderCase(String str) {
        return toSeparatorCase(str, SEPARATOR);
    }

    /**
     * 驼峰转串式命名
     */
    public static String toKebabCase(String str) {
        return toSeparatorCase(str, '-');
    }

    /**
     * 驼峰转符号分割
     *
     * @param str       字符串
     * @param separator 分隔符
     * @return 结果
     */
    public static String toSeparatorCase(String str, char separator) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase;
        // 当前字符是否大写
        boolean curCharIsUpperCase;
        // 下一字符是否大写
        boolean nextCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }
            curCharIsUpperCase = Character.isUpperCase(c);
            if (i < (str.length() - 1)) {
                nextCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }
            if (preCharIsUpperCase && curCharIsUpperCase && !nextCharIsUpperCase) {
                sb.append(separator);
            } else if ((i != 0 && !preCharIsUpperCase) && curCharIsUpperCase) {
                sb.append(separator);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 将输入的字符串转换为驼峰式命名法例如：user_name->userName
     *
     * @param s 输入的字符串，可以是null
     * @return 转换后的驼峰式命名字符串，如果输入为null，则返回空字符串
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return "";
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转首字母大写驼峰 例如：user_name->UserName
     *
     * @param str 字符串
     * @return 首字母大写
     */
    public static String toCamelBigCase(String str) {
        return firstUpperCase(toCamelCase(str));
    }

    /**
     * 首字母大写
     * 将给定字符串的第一个字符转换为大写形式，其余字符保持不变
     *
     * @param str 输入的字符串
     * @return 转换后首字母为大写的字符串
     */
    public static String firstUpperCase(String str) {
        return firstCase(str, (val) -> val.toUpperCase(Locale.ROOT));
    }

    /**
     * 首字母小写
     *
     * @param str 待处理的字符串
     * @return 首字母转换为小写后的字符串
     */
    public static String firstLowerCase(String str) {
        return firstCase(str, (val) -> val.toLowerCase(Locale.ROOT));
    }

    /**
     * 首字母转换
     *
     * @param str      字符串
     * @param function 转换函数
     * @return 转换后的字符串
     */
    private static String firstCase(String str, Function<String, String> function) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        char[] strArray = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArray.length; i++) {
            if (i == 0) {
                sb.append(function.apply(String.valueOf(strArray[i])));
                continue;
            }
            sb.append(strArray[i]);
        }
        return sb.toString();
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str  指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs) {
        if (isEmpty(str) || strs == null || strs.isEmpty()) {
            return false;
        }
        for (String pattern : strs) {
            if (isPathMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断URL是否与规则配置匹配
     * 单个字符表示任意字符
     * * 表示一层路径内的任意字符串，但不可跨层级
     * ** 表示任意层路径
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     * @return 如果url与规则匹配，则返回true，否则返回false
     */
    public static boolean isPathMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    /**
     * 判断字符串是否符合正则规则
     *
     * @param pattern 规则
     * @param str     字符串
     * @return 是否
     */
    public static boolean isMatch(String pattern, String str) {
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(str);
        return matcher.matches();
    }

    /**
     * 是否手机号
     *
     * @param phone 手机号
     * @return 是否
     */
    public static boolean isPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return StringUtils.isMatch("^1[3-9][0-9]\\d{8}$", phone);
    }

    /**
     * 是否邮箱地址
     *
     * @param email 邮箱
     * @return
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return StringUtils.isMatch("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }


    public static void main(String[] args) {

        System.out.println(isMatch("^[A-Za-z]+[A-Za-z0-9]*$", "22aaaaa"));
        System.out.println(isMatch("^[A-Za-z]+[A-Za-z0-9]*$", "aa2323aaa"));

    }
}
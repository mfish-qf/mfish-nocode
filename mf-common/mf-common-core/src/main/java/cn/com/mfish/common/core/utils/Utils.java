package cn.com.mfish.common.core.utils;

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
     * @return
     */
    public static String uuid32() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

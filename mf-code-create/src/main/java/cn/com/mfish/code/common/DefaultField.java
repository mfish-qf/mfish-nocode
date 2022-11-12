package cn.com.mfish.code.common;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ：qiufeng
 * @description：缺省字段
 * @date ：2022/11/12 21:38
 */
public class DefaultField {
    public static Set<String> values = new HashSet<>();
    static {
        values.add("id");
        values.add("create_by");
        values.add("create_time");
        values.add("update_by");
        values.add("update_time");
    }

}

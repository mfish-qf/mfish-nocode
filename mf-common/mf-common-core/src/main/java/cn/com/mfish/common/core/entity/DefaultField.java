package cn.com.mfish.common.core.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: mfish
 * @description: 缺省字段
 * @date: 2022/11/12 21:38
 */
public class DefaultField {
    public static final String ID = "id";
    public static final String CREATE_BY = "create_by";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_BY = "update_by";
    public static final String UPDATE_TIME = "update_time";

    public static Set<String> values = new HashSet<>();

    static {
        values.add(ID);
        values.add(CREATE_BY);
        values.add(CREATE_TIME);
        values.add(UPDATE_BY);
        values.add(UPDATE_TIME);
    }
}

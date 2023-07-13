package cn.com.mfish.common.dataset.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 过滤条件
 * @author: mfish
 * @date: 2023/5/9 21:04
 */
public enum FilterCondition {
    错误("error", ""),
    等于("eq", " = ''{0}''"),
    不等于("ne", " != ''{0}''"),
    大于("gt", " > ''{0}''"),
    大于等于("ge", " >= ''{0}''"),
    小于("lt", " < ''{0}''"),
    小于等于("le", " <= ''{0}''"),
    介于之间("between", " BETWEEN ''{0}'' AND ''{1}''"),
    为空("isNull", " IS NULL"),
    不为空("isNotNull", " IS NOT NULL"),
    包含("like", " LIKE ''%{0}%''"),
    不包含("notLike", " NOT LIKE ''%{0}%''"),
    以开始("likeLeft", " LIKE ''{0}%''"),
    以结束("likeRight", " LIKE ''%{0}''");
    private String value;
    private final String condition;

    FilterCondition(String value, String condition) {
        this.value = value;
        this.condition = condition;
    }

    private static final Map<String, FilterCondition> conditionMap = new HashMap<>();

    static {
        for (FilterCondition con : FilterCondition.values()) {
            conditionMap.put(con.value, con);
        }
    }

    /**
     * 获取值
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取条件
     *
     * @return
     */
    public String getCondition() {
        return condition;
    }

    /**
     * 根据值获取操作条件
     *
     * @param value
     * @return
     */
    public static FilterCondition forCondition(String value) {
        if (conditionMap.containsKey(value)) {
            return conditionMap.get(value);
        }
        return FilterCondition.错误;
    }
}

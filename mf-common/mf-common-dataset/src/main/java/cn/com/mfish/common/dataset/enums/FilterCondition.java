package cn.com.mfish.common.dataset.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 过滤条件
 * @author: mfish
 * @date: 2023/5/9 21:04
 */
@Getter
public enum FilterCondition {
    错误("error", "", ""),
    等于("eq", " = ''{0}''", " = {0}"),
    不等于("ne", " != ''{0}''", " != {0}"),
    大于("gt", " > ''{0}''", " > {0}"),
    大于等于("ge", " >= ''{0}''", " >= {0}"),
    小于("lt", " < ''{0}''", " < {0}"),
    小于等于("le", " <= ''{0}''", " <= {0}"),
    介于之间("between", " BETWEEN ''{0}'' AND ''{1}''", " BETWEEN {0} AND {1}"),
    为空("isNull", " IS NULL", " IS NULL"),
    不为空("isNotNull", " IS NOT NULL", " IS NOT NULL"),
    包含("like", " LIKE ''%{0}%''", " LIKE CONCAT(''%'',{0},''%'')"),
    不包含("notLike", " NOT LIKE ''%{0}%''", " NOT LIKE CONCAT(''%'',{0},''%'')"),
    以开始("likeLeft", " LIKE ''{0}%''", " LIKE CONCAT({0},''%'')"),
    以结束("likeRight", " LIKE ''%{0}''", " LIKE CONCAT(''%'',{0})");
    /**
     * 获取值
     *
     */
    private final String value;
    /**
     * 普通值条件
     *
     */
    private final String condition;
    /**
     * 字段条件(默认mysql，其他数据库类型需要重写)
     * 实现IAnalysis中的getFieldCondition方法来重写
     */
    private final String fieldCondition;

    FilterCondition(String value, String condition, String fieldCondition) {
        this.value = value;
        this.condition = condition;
        this.fieldCondition = fieldCondition;
    }

    private static final Map<String, FilterCondition> conditionMap = new HashMap<>();

    static {
        for (FilterCondition con : FilterCondition.values()) {
            conditionMap.put(con.value, con);
        }
    }

    /**
     * 根据值获取操作条件
     *
     * @param value 操作条件的字符串表示
     * @return 对应的操作条件，如果找不到则返回错误
     */
    public static FilterCondition forCondition(String value) {
        if (conditionMap.containsKey(value)) {
            return conditionMap.get(value);
        }
        return FilterCondition.错误;
    }
}

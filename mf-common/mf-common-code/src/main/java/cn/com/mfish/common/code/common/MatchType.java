package cn.com.mfish.common.code.common;

import cn.com.mfish.common.core.utils.StringUtils;

/**
 * @author: mfish
 * @description: 匹配枚举
 * @date: 2022/12/23 16:59
 */
public enum MatchType {
    不处理("none"),
    首字母小写("uncap_first"),
    串式("kebab-case"),
    下划线("under-case");

    final String matchType;

    MatchType(String type) {
        this.matchType = type;
    }

    public static MatchType getMatchType(String matchType) {
        for (MatchType type : MatchType.values()) {
            if (type.getValue().equals(matchType)) {
                return type;
            }
        }
        return MatchType.不处理;
    }

    public String getValue() {
        return matchType;
    }

    /**
     * 处理参数值
     *
     * @param value 值
     * @return 处理后的值
     */
    public String dealVariable(String value) {
        return switch (getMatchType(matchType)) {
            case 首字母小写 -> StringUtils.firstLowerCase(value);
            case 下划线 -> StringUtils.toUnderCase(value);
            case 串式 -> StringUtils.toKebabCase(value);
            default -> value;
        };
    }
}

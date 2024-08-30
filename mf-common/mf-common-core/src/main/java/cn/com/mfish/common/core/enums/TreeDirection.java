package cn.com.mfish.common.core.enums;

/**
 * @description: 树形查询方向
 * @author: mfish
 * @date: 2023/5/17 9:58
 */
public enum TreeDirection {
    双向("all"),
    向上("up"),
    向下("down");
    private final String value;

    TreeDirection(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    /**
     * 获取属性检索方向
     * 根据传入的字符串值，匹配并返回对应的TreeDirection枚举值如果传入的字符串与任何TreeDirection枚举值都不匹配，
     * 则返回默认值TreeDirection.双向
     *
     * @param value 需要检索的属性值，用于匹配TreeDirection枚举值
     * @return 匹配的TreeDirection枚举值，如果没有匹配项，则返回TreeDirection.双向
     */
    public static TreeDirection getDirection(String value) {
        for (TreeDirection direction : TreeDirection.values()) {
            if (direction.value.equals(value)) {
                return direction;
            }
        }
        return TreeDirection.双向;
    }
}

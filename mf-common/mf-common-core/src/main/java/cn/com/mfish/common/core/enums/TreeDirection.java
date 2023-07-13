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
     *
     * @param value
     * @return
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

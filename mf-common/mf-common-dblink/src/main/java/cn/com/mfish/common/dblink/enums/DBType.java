package cn.com.mfish.common.dblink.enums;

/**
 * @description: 数据库类型
 * @author: mfish
 * @date: 2023/3/17
 */
public enum DBType {
    mysql(0),
    postgre(1),
    oracle(2);

    private final int value;

    DBType(int value) {
        this.value = value;
    }

    /**
     * 获取数据库类型
     *
     * 根据给定的值，返回对应的数据库类型。此方法主要用于在系统中确定数据库类型，
     * 以便根据不同数据库的特性进行相应的操作或查询优化。
     *
     * @param value 表示数据库类型的整数值，该值应与某个数据库类型枚举的value字段匹配
     * @return 对应的DBType枚举值如果找不到匹配的值，则默认返回mysql数据库类型
     */
    public static DBType getType(int value) {
        for (DBType type : DBType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return DBType.mysql;
    }

    /**
     * 获取数据库驱动
     * 根据当前对象的状态选择并返回相应的数据库驱动类名
     * 这对于数据库连接的建立至关重要，确保使用正确的驱动连接到正确的数据库类型
     *
     * @return 数据库驱动类名
     */
    public String getDriver() {
        return switch (this) {
            case oracle -> "oracle.jdbc.OracleDriver";
            case postgre -> "org.postgresql.Driver";
            default -> "com.mysql.cj.jdbc.Driver";
        };
    }

}

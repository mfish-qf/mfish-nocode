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
     * @param value
     * @return
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
     *
     * @return
     */
    public String getDriver() {
        switch (this) {
            case oracle:
                return "oracle.jdbc.OracleDriver";
            case postgre:
                return "org.postgresql.Driver";
            default:
                return "com.mysql.cj.jdbc.Driver";
        }
    }

}

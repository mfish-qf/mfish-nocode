package cn.com.mfish.common.dblink.enums;

import cn.com.mfish.common.core.utils.StringUtils;

import java.text.MessageFormat;

/**
 * @description: 数据库类型
 * @author: mfish
 * @date: 2023/3/17
 */
public enum DBType {
    mysql(0),
    postgre(1),
    oracle(2);

    private int value;

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
                return "oracle.jdbc.driver.OracleDriver";
            case postgre:
                return "org.postgresql.Driver";
            default:
                return "com.mysql.cj.jdbc.Driver";
        }
    }

    /**
     * 获取jdbcUrl
     *
     * @param host
     * @param port
     * @param databaseName
     * @return
     */
    public String getJdbcUrl(String host, String port, String databaseName) {
        String head = "";
        switch (this) {
            case oracle:
                return "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=" + host + ")(PORT=" + port + ")))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = " + databaseName + ")))";
            case postgre:
                head = "jdbc:postgresql";
                break;
            default:
                head = "jdbc:mysql";
                break;
        }
        String property = "?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai";
        if (!StringUtils.isEmpty(databaseName)) {
            databaseName = "/" + databaseName;
        } else {
            databaseName = "";
        }
        return MessageFormat.format("{0}://{1}:{2}{3}{4}", head, host, port, databaseName, property);
    }

}

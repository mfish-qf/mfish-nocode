package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.dblink.page.BoundSql;

/**
 * @description: mysql数据库相关信息
 * @author: mfish
 * @date: 2023/3/23 22:09
 */
public class MysqlDBDialect extends AbstractDBDialect {
    @Override
    public String getJdbc(String host, String port, String dbName) {
        return getJdbc(host, port, dbName, "jdbc:mysql");
    }

    @Override
    public BoundSql getColumns(String dbName, String tableName) {
        String sql = "select column_name field_name, data_type db_type\n" +
                "        , if(column_key = 'PRI', true, false) is_primary\n" +
                "        , if(is_nullable = 'NO', false, true) null_able\n" +
                "        , column_type\n" +
                "        , column_comment comment\n" +
                "        from information_schema.columns where 1=1";
        return buildCondition(sql, dbName, tableName);
    }

    @Override
    public BoundSql getTableInfo(String dbName, String tableName) {
        String sql = "SELECT table_name, table_comment, table_schema\n" +
                "        FROM INFORMATION_SCHEMA.TABLES where 1=1";
        return buildCondition(sql, dbName, tableName);
    }


}

package cn.com.mfish.code.dialect;

import cn.com.mfish.common.dblink.page.BoundSql;

/**
 * @description: mysql数据相关代码构建
 * @author: mfish
 * @date: 2023/3/23 22:09
 */
public class MysqlCodeBuild implements CodeBuild {
    @Override
    public BoundSql getColumns(String schema, String tableName) {
        String sql = "select column_name field_name, data_type db_type\n" +
                "        , if(column_key = 'PRI', true, false) is_primary\n" +
                "        , if(is_nullable = 'NO', false, true) null_able\n" +
                "        , column_comment comment\n" +
                "        from information_schema.columns";
        return buildCondition(sql, schema, tableName);
    }

    @Override
    public BoundSql getTableInfo(String schema, String tableName) {
        String sql = "SELECT table_name, table_comment\n" +
                "        FROM INFORMATION_SCHEMA.TABLES";
        return buildCondition(sql, schema, tableName);
    }

    private BoundSql buildCondition(String sql, String schema, String tableName) {
        sql += " where table_name = ? and table_schema = ?";
        BoundSql boundSql = new BoundSql(sql);
        boundSql.getParams().add(tableName);
        boundSql.getParams().add(schema);
        return boundSql;
    }

}

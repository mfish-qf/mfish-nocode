package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.core.utils.StringUtils;
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
    public BoundSql getColumns(String dbName, String tableSchema, String tableName) {
        String sql = """
                select column_name field_name, data_type db_type
                        , if(column_key = 'PRI', true, false) is_primary
                        , if(is_nullable = 'NO', false, true) null_able
                        , column_type
                        , column_comment comment
                        from information_schema.columns where 1=1""";
        //mysql如果没传入前缀则默认使用数据库名
        String schema = StringUtils.isEmpty(tableSchema) ? dbName : tableSchema;
        BoundSql boundSql = buildCondition(sql, schema, tableName);
        boundSql.setSql(boundSql.getSql() + " order by ordinal_position");
        return boundSql;
    }

    @Override
    public BoundSql getTableInfo(String dbName, String tableSchema, String tableName) {
        String schema = StringUtils.isEmpty(tableSchema) ? dbName : tableSchema;
        String sql = "SELECT table_name, table_comment, table_schema, if(table_type='VIEW',1,0) table_type" +
                " FROM INFORMATION_SCHEMA.TABLES where 1=1";
        return buildCondition(sql, schema, tableName);
    }
}

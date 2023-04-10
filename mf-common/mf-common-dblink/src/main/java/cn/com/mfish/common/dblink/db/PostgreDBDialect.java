package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.dblink.page.BoundSql;

/**
 * @description: pg数据库相关信息
 * @author: mfish
 * @date: 2023/3/24 20:33
 */
public class PostgreDBDialect extends AbstractDBDialect {
    @Override
    public String getJdbc(String host, String port, String dbName) {
        return getJdbc(host, port, dbName, "jdbc:postgresql");
    }

    @Override
    public BoundSql getColumns(String dbName, String tableName) {
        String strSql = "SELECT A.attname AS FIELD_NAME,\n" +
                "T.typname AS db_type,\n" +
                "concat_ws ('', T.typname, SUBSTRING (format_type ( A.atttypid, A.atttypmod ) FROM '\\(.*\\)')) AS column_type,\n" +
                "(CASE WHEN (SELECT COUNT(*) FROM pg_constraint WHERE conrelid = A.attrelid AND conkey [1]= attnum AND contype = 'p' ) > 0 THEN 1 ELSE 0 END) AS is_primary,\n" +
                "(CASE WHEN A.attnotnull = 't' THEN 0 ELSE 1 END ) AS null_able,\n" +
                "d.description AS COMMENT \n" +
                "FROM\n" +
                "pg_class C \n" +
                "inner join information_schema.tables s on s.TABLE_NAME = c.relname\n" +
                "LEFT JOIN pg_attribute A ON A.attrelid = C.oid \n" +
                "AND A.attnum > 0\n" +
                "LEFT JOIN pg_type T ON A.atttypid = T.oid\n" +
                "LEFT JOIN pg_description d ON d.objoid = A.attrelid \n" +
                "AND d.objsubid = A.attnum where 1=1";
        //pg数据库table_schema是单独定义的schema不是数据库名称，此处暂时不传。参数预留
        return buildCondition(strSql, "", tableName);
    }

    @Override
    public BoundSql getTableInfo(String dbName, String tableName) {
        String strSql = "SELECT d.table_name,\n" +
                "CAST(obj_description(C.relfilenode, 'pg_class') AS VARCHAR) AS TABLE_COMMENT,\n" +
                "d.table_schema \n" +
                "FROM\n" +
                "pg_class c,\n" +
                "information_schema.tables d \n" +
                "WHERE\n" +
                "c.relkind = 'r' \n" +
                "AND c.relname NOT LIKE'pg_%' \n" +
                "AND c.relname NOT LIKE'sql_%' \n" +
                "AND c.relkind = 'r' \n" +
                "AND c.relname = d.TABLE_NAME\n";
        //pg数据库table_schema是单独定义的schema不是数据库名称，此处暂时不传。参数预留
        return buildCondition(strSql, "", tableName);
    }
}

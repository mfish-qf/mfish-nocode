package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.dblink.enums.DBType;
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
    public BoundSql getColumns(String dbName, String tableSchema, String tableName) {
        String strSql = """
                SELECT A.attname AS FIELD_NAME,
                T.typname AS db_type,
                concat_ws ('', T.typname, SUBSTRING (format_type ( A.atttypid, A.atttypmod ) FROM '\\(.*\\)')) AS column_type,
                (CASE WHEN (SELECT COUNT(*) FROM pg_constraint WHERE conrelid = A.attrelid AND conkey [1]= attnum AND contype = 'p' ) > 0 THEN 1 ELSE 0 END) AS is_primary,
                (CASE WHEN A.attnotnull = 't' THEN 0 ELSE 1 END ) AS null_able,
                d.description AS COMMENT
                FROM
                pg_class C
                inner join information_schema.tables s on s.TABLE_NAME = c.relname
                LEFT JOIN pg_attribute A ON A.attrelid = C.oid
                AND A.attnum > 0
                LEFT JOIN pg_type T ON A.atttypid = T.oid
                LEFT JOIN pg_description d ON d.objoid = A.attrelid
                AND d.objsubid = A.attnum where 1=1""";
        //pg数据库table_schema是单独定义的schema不是数据库名称，此处暂时不传。参数预留
        BoundSql boundSql = buildCondition(strSql, tableSchema, tableName);
        boundSql.setDbType(DBType.postgre);
        return boundSql;
    }

    @Override
    public BoundSql getTableInfo(String dbName, String tableSchema, String tableName) {
        String strSql = """
                SELECT
                d.table_name,
                CAST(obj_description(C.relfilenode, 'pg_class') AS VARCHAR) AS TABLE_COMMENT,
                d.table_schema,
                (CASE WHEN c.relkind = 'v' THEN 1 ELSE 0 END ) AS table_type
                FROM
                pg_class c,
                information_schema.tables d
                WHERE
                c.relnamespace IN ( SELECT relnamespace FROM pg_class WHERE relkind = 'r' AND relname NOT LIKE'pg_%' AND relname NOT LIKE'sql_%' )
                AND ( c.relkind = 'r' OR c.relkind = 'v' )
                AND c.relname = d.TABLE_NAME""";
        //pg数据库table_schema是单独定义的schema不是数据库名称，此处暂时不传。参数预留
        BoundSql boundSql = buildCondition(strSql, tableSchema, tableName);
        boundSql.setDbType(DBType.postgre);
        return boundSql;
    }
}

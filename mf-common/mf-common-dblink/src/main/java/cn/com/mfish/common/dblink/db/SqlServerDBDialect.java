package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.page.BoundSql;

/**
 * @description: SQLServer数据库方言
 * @author: mfish
 * @date: 2025/3/4
 */
public class SqlServerDBDialect extends AbstractDBDialect {
    @Override
    public String getJdbc(String host, String port, String dbName) {
        return "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbName + ";encrypt=false";
    }

    @Override
    public BoundSql getColumns(String dbName, String tableSchema, String tableName) {
        String sql = """
                select * from (SELECT
                    t.name AS table_name,
                    sc.name AS table_schema,
                    c.name AS field_name,
                    s.name db_type,
                CASE WHEN i.is_primary_key IS NULL THEN
                        0 ELSE i.is_primary_key
                    END AS is_primary,
                    c.is_nullable nullable,
                    concat ( s.name, '(', c.max_length, ')' ) AS column_type,
                    p.value AS comment
                FROM
                    sys.columns c
                    JOIN sys.types s ON c.user_type_id = s.user_type_id
                    LEFT JOIN sys.index_columns ic ON c.object_id = ic.object_id
                    AND c.column_id = ic.column_id
                    LEFT JOIN sys.indexes i ON ic.object_id = i.object_id
                    AND ic.index_id = i.index_id
                    JOIN sys.tables t ON c.object_id = t.object_id
                    LEFT JOIN sys.schemas sc ON t.schema_id = sc.schema_id
                    LEFT JOIN sys.extended_properties p ON c.object_id = p.major_id
                    AND c.column_id = p.minor_id
                    AND p.name = 'MS_Description' ) a
                WHERE 1=1""";
        BoundSql boundSql = buildCondition(sql, tableSchema, tableName);
        boundSql.setDbType(DBType.sqlserver2012);
        return boundSql;
    }

    @Override
    public BoundSql getTableInfo(String dbName, String tableSchema, String tableName) {
        String sql = """
                SELECT table_name,table_comment,table_schema,table_type FROM
                	(
                	SELECT
                		t.name table_name,
                		p.value table_comment,
                		s.name table_schema,
                		0 table_type,
                		t.create_date
                	FROM
                		sys.tables t
                		LEFT JOIN sys.extended_properties p ON t.object_id = p.major_id
                		AND p.name = 'MS_Description'
                		LEFT JOIN sys.schemas s ON t.schema_id = s.schema_id UNION
                	SELECT
                		v.name table_name,
                		'' table_comment,
                		s.name table_schema,
                		1 table_type,
                		v.create_date
                	FROM
                		sys.views v
                		LEFT JOIN sys.schemas s ON v.schema_id = s.schema_id
                	) a
                where 1=1""";
        BoundSql boundSql = buildCondition(sql, "", tableName);
        boundSql.setSql(boundSql.getSql() + " ORDER BY a.create_date");
        boundSql.setDbType(DBType.sqlserver2012);
        return boundSql;
    }


}

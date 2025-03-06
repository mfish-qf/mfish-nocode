package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.dblink.entity.QueryParam;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.page.BoundSql;

import java.util.Locale;

/**
 * @description: oracle数据库相关信息
 * @author: mfish
 * @date: 2023/3/24 20:32
 */
public class OracleDBDialect implements DBDialect {
    @Override
    public String getJdbc(String host, String port, String dbName) {
        return "jdbc:oracle:thin:@//" + host + ":" + port + "/" + dbName;
    }

    @Override
    public BoundSql getColumns(String dbName, String tableSchema, String tableName) {
        String sql = """
                 select a.COLUMN_NAME as "field_name",
                      a.DATA_TYPE as "db_type",
                      replace(decode(data_type,'VARCHAR2',data_type || '(' || data_length || ')','CHAR',data_type || '(' || data_length || ')','NUMBER',data_type || '(' ||
                                    decode(to_char(DATA_SCALE),'0',to_char(DATA_PRECISION),DATA_PRECISION || ',' || DATA_SCALE) || ')',data_type),'(,)','') "column_type",
                      decode(a.nullable,'N',0,1) as "null_able",
                      b.comments as "comment",
                      case when (select count(*)
                                     from user_cons_columns c
                                     where c.table_name = a.TABLE_NAME
                                     and c.column_name = a.COLUMN_NAME
                                     and c.constraint_name =
                                     (select d.constraint_name
                                      from user_constraints d
                                      where d.table_name = c.table_name
                                      and d.constraint_type = 'P')) > 0 then 1 else 0 end as "is_primary"
                  from USER_TAB_COLS a, SYS.USER_COL_COMMENTS b
                  where a.TABLE_NAME = b.TABLE_NAME
                  and b.COLUMN_NAME = a.COLUMN_NAME
                """;
        BoundSql boundSql = buildCondition(sql, tableName);
        return boundSql.setSql(boundSql.getSql() + "  order by a.TABLE_NAME, a.COLUMN_ID");
    }

    @Override
    public BoundSql getTableInfo(String dbName, String tableSchema, String tableName) {
        String sql = """
                select u.table_name, u.comments table_comment, a.owner table_schema,DECODE(u.table_type,'VIEW', 1, 0) table_type
                 from user_tab_comments u
                 inner join all_tab_comments a
                 on u.table_name = a.table_name
                 where u.table_name not like 'BIN$%'""";
        return buildCondition(sql, tableName);
    }

    private BoundSql buildCondition(String sql, String tableName) {
        BoundSql boundSql = new BoundSql();
        boundSql.setDbType(DBType.oracle);
        if (!StringUtils.isEmpty(tableName)) {
            //这里虽然匹配大小写，但是建议oracle表名视图都创建大写名称，避免数据查询时无法查到
            sql += " and (a.TABLE_NAME = ? or a.TABLE_NAME = ?)";
            boundSql.getParams().add(new QueryParam().setValue(tableName.toUpperCase(Locale.ROOT)));
            boundSql.getParams().add(new QueryParam().setValue(tableName.toLowerCase(Locale.ROOT)));
        }
        return boundSql.setSql(sql);
    }
}

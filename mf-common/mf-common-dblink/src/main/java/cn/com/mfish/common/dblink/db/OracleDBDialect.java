package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.dblink.entity.QueryParam;
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
    public BoundSql getColumns(String dbName, String tableName) {
        String sql = " select a.COLUMN_NAME as \"field_name\",\n" +
                "      a.DATA_TYPE as \"db_type\",\n" +
                "      replace(decode(data_type,'VARCHAR2',data_type || '(' || data_length || ')','CHAR',data_type || '(' || data_length || ')','NUMBER',data_type || '(' ||\n" +
                "                    decode(to_char(DATA_SCALE),'0',to_char(DATA_PRECISION),DATA_PRECISION || ',' || DATA_SCALE) || ')',data_type),'(,)','') \"column_type\",\n" +
                "      decode(a.nullable,'N',0,1) as \"null_able\",\n" +
                "      b.comments as \"comment\",\n" +
                "      case when (select count(*)\n" +
                "                     from user_cons_columns c\n" +
                "                     where c.table_name = a.TABLE_NAME\n" +
                "                     and c.column_name = a.COLUMN_NAME\n" +
                "                     and c.constraint_name =\n" +
                "                     (select d.constraint_name\n" +
                "                      from user_constraints d\n" +
                "                      where d.table_name = c.table_name\n" +
                "                      and d.constraint_type = 'P')) > 0 then 1 else 0 end as \"is_primary\"\n" +
                "  from USER_TAB_COLS a, SYS.USER_COL_COMMENTS b\n" +
                "  where a.TABLE_NAME = b.TABLE_NAME\n" +
                "  and b.COLUMN_NAME = a.COLUMN_NAME\n";
        BoundSql boundSql = buildCondition(sql, tableName);
        return boundSql.setSql(boundSql.getSql() + "  order by a.TABLE_NAME, a.COLUMN_ID");
    }

    @Override
    public BoundSql getTableInfo(String dbName, String tableName) {
        String sql = "select u.table_name, u.comments table_comment, a.owner table_schema\n" +
                " from user_tab_comments u\n" +
                " inner join all_tab_comments a\n" +
                " on u.table_name = a.table_name\n" +
                " where 1 = 1";
        return buildCondition(sql, tableName);
    }

    private BoundSql buildCondition(String sql, String tableName) {
        BoundSql boundSql = new BoundSql();
        if (!StringUtils.isEmpty(tableName)) {
            sql += " and a.TABLE_NAME = ?";
            boundSql.getParams().add(new QueryParam().setValue(tableName.toUpperCase(Locale.ROOT)));
        }
        return boundSql.setSql(sql);
    }
}

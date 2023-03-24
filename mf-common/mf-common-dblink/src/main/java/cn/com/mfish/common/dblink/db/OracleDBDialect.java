package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.dblink.page.BoundSql;

/**
 * @description: oracle数据库相关信息
 * @author: mfish
 * @date: 2023/3/24 20:32
 */
public class OracleDBDialect implements DBDialect {
    @Override
    public String getJdbc(String host, String port, String dbName) {
        return "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=" + host + ")(PORT=" + port + ")))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME = " + dbName + ")))";
    }

    @Override
    public BoundSql getColumns(String dbName, String tableName) {
        String sql = " select t.COLUMN_NAME,t.DATA_TYPE,c.COMMENTS from USER_TAB_COLUMNS t,USER_COL_COMMENTS c where\n" +
                "        t.TABLE_NAME = c.TABLE_NAME and\n" +
                "        t.COLUMN_NAME=c.COLUMN_NAME and t.TABLE_NAME = #{tableName}";
        return null;
    }

    @Override
    public BoundSql getTableInfo(String dbName, String tableName) {
        String sql = " select table_name,comments table_comment from user_tab_comments where 1=1";
        return null;
    }
}

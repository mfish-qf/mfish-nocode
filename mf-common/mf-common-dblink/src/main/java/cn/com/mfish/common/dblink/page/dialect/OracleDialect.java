package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.query.BaseQuery;
import com.github.pagehelper.Page;

import static cn.com.mfish.common.dblink.common.Constant.ORACLE_ROW;

/**
 * @description: oracle相关方言
 * @author: mfish
 * @date: 2023/3/21 23:04
 */
public class OracleDialect extends AbstractDialect {
    public OracleDialect(BaseQuery baseQuery) {
        super(baseQuery);
    }

    @Override
    protected BoundSql getPageSql(BoundSql boundSql, Page page) {
        StringBuilder sqlBuilder = new StringBuilder(boundSql.getSql().length() + 120);
        sqlBuilder.append("SELECT * FROM ( ");
        sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM " + ORACLE_ROW + " FROM (");
        sqlBuilder.append(boundSql.getSql());
        sqlBuilder.append(") TMP_PAGE)");
        sqlBuilder.append(" WHERE " + ORACLE_ROW + " <= ? AND " + ORACLE_ROW + " > ?");
        boundSql.getParams().add(page.getEndRow());
        boundSql.getParams().add(page.getStartRow());
        return boundSql.setSql(sqlBuilder.toString());
    }
}
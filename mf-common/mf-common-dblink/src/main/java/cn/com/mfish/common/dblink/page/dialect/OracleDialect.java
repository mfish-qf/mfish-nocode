package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.query.BaseQuery;
import com.github.pagehelper.Page;

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
    BoundSql getPageSql(BoundSql boundSql, Page page) {
        StringBuilder sqlBuilder = new StringBuilder(boundSql.getSql().length() + 120);
        sqlBuilder.append("SELECT * FROM ( ");
        sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM ROWNUM_ FROM (");
        sqlBuilder.append(boundSql.getSql());
        sqlBuilder.append(") TMP_PAGE)");
        sqlBuilder.append(" WHERE ROWNUM_ <= ? AND ROWNUM_ > ?");
        boundSql.getParams().add(page.getPageSize());
        boundSql.getParams().add(page.getStartRow());
        return boundSql.setSql(sqlBuilder.toString());
    }
}
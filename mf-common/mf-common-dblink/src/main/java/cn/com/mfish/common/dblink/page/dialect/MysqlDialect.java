package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.query.BaseQuery;
import com.github.pagehelper.Page;

/**
 * @description: mysql相关方言
 * @author: mfish
 * @date: 2023/3/21 23:04
 */
public class MysqlDialect extends AbstractDialect {
    public MysqlDialect(final BaseQuery baseQuery) {
        super(baseQuery);
    }

    @Override
    BoundSql getPageSql(BoundSql boundSql, Page page) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM (" + boundSql.getSql());
        if (page.getStartRow() == 0) {
            sqlBuilder.append(") TEMP LIMIT ?");
            boundSql.getParams().add(page.getPageSize());
            return boundSql.setSql(sqlBuilder.toString());
        }
        sqlBuilder.append(") TEMP LIMIT ?, ?");
        boundSql.getParams().add(page.getStartRow());
        boundSql.getParams().add(page.getPageSize());
        return boundSql.setSql(sqlBuilder.toString());
    }
}
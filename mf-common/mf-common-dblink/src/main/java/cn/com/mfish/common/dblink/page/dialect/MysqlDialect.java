package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.core.enums.DataType;
import cn.com.mfish.common.dblink.entity.QueryParam;
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
    protected BoundSql getPageSql(BoundSql boundSql, Page<?> page) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM (");
        sqlBuilder.append(boundSql.getSql());
        if (page.getStartRow() == 0) {
            sqlBuilder.append(") TEMP LIMIT ?");
            boundSql.getParams().add(new QueryParam().setValue(page.getPageSize()).setType(DataType.INTEGER));
            return boundSql.setSql(sqlBuilder.toString());
        }
        sqlBuilder.append(") TEMP LIMIT ?, ?");
        boundSql.getParams().add(new QueryParam().setValue(page.getStartRow()).setType(DataType.INTEGER));
        boundSql.getParams().add(new QueryParam().setValue(page.getPageSize()).setType(DataType.INTEGER));
        return boundSql.setSql(sqlBuilder.toString());
    }
}
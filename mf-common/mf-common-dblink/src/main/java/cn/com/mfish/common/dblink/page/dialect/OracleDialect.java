package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.core.constants.DataConstant;
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
    protected BoundSql getPageSql(BoundSql boundSql, Page page) {
        String sql = "SELECT * FROM ( " +
                " SELECT TMP_PAGE.*, ROWNUM " + DataConstant.ORACLE_ROW + " FROM (" +
                boundSql.getSql() +
                ") TMP_PAGE)" +
                " WHERE " + DataConstant.ORACLE_ROW + " <= ? AND " + DataConstant.ORACLE_ROW + " > ?";
        boundSql.getParams().add(page.getEndRow());
        boundSql.getParams().add(page.getStartRow());
        return boundSql.setSql(sql);
    }
}
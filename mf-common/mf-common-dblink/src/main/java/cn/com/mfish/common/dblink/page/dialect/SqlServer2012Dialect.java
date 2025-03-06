package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.core.enums.DataType;
import cn.com.mfish.common.dblink.entity.QueryParam;
import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.query.BaseQuery;
import com.github.pagehelper.Page;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.List;

/**
 * @description: sqlserver相关方言
 * @author: mfish
 * @date: 2025/3/4
 */
public class SqlServer2012Dialect extends AbstractDialect {
    public SqlServer2012Dialect(BaseQuery baseQuery) {
        super(baseQuery);
    }

    @Override
    public String getSimpleCountSql(final String sql, String name) {
        try {
            PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
            //sqlserver order不允许放在字句中
            select.setOrderByElements(null);
            return "SELECT COUNT(" +
                    name +
                    ") COUNT FROM ( \n" +
                    select +
                    "\n ) TMP_COUNT";
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected BoundSql getPageSql(BoundSql boundSql, Page<?> page) {
        try {
            PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(boundSql.getSql());
            List<OrderByElement> list = select.getOrderByElements();
            StringBuilder sb = new StringBuilder();
            if(list != null && !list.isEmpty()) {
                for (OrderByElement orderByElement : list) {
                    sb.append(orderByElement.getExpression().toString());
                    if (!orderByElement.isAsc()) {
                        sb.append(" DESC");
                    }
                    sb.append(" ,");
                }
            }
            if (!sb.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            } else {
                sb.append("1");
            }
            select.setOrderByElements(null);
            String sql = select +
                    " ORDER BY " + sb + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            boundSql.getParams().add(new QueryParam().setValue(page.getStartRow()).setType(DataType.INTEGER));
            boundSql.getParams().add(new QueryParam().setValue(page.getPageSize()).setType(DataType.INTEGER));
            return boundSql.setSql(sql);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }
}

package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.dblink.datatable.MetaDataTable;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.page.MfPageHelper;
import cn.com.mfish.common.dblink.query.BaseQuery;
import com.github.pagehelper.Page;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;

/**
 * @description: 抽象方言
 * @author: mfish
 * @date: 2023/3/21 23:05
 */
public abstract class AbstractDialect implements Dialect {
    private BaseQuery baseQuery;

    public AbstractDialect(BaseQuery baseQuery) {
        this.baseQuery = baseQuery;
    }

    public BaseQuery getAbQuery() {
        return this.baseQuery;
    }

    public Page getLocalPage() {
        return MfPageHelper.getLocalPage();
    }

    @Override
    public boolean skip(DataSourceOptions<?> dataSourceOptions, RowBounds rowBounds) {
        return true;
    }

    @Override
    public boolean beforeCount() {
        Page page = getLocalPage();
        return !page.isOrderByOnly() && page.isCount();
    }

    @Override
    public boolean afterCount(long count) {
        Page page = getLocalPage();
        page.setTotal(count);
        //pageSize < 0 的时候，不执行分页查询
        //pageSize = 0 的时候，还需要执行后续查询，但是不会分页
        if (page.getPageSizeZero() != null) {
            //PageSizeZero=false&&pageSize<=0
            if (!page.getPageSizeZero() && page.getPageSize() <= 0) {
                return false;
            }
            //PageSizeZero=true&&pageSize<0 返回 false，只有>=0才需要执行后续的
            else if (page.getPageSizeZero() && page.getPageSize() < 0) {
                return false;
            }
        }
        //页码>0 && 开始行数<总行数即可，不需要考虑 pageSize（上面的 if 已经处理不符合要求的值了）
        return page.getPageNum() > 0 && count > page.getStartRow();
    }

    @Override
    public String getCountSql(BoundSql boundSql) {
        return getSimpleCountSql(boundSql.getSql(), "0");
    }

    public String getSimpleCountSql(final String sql, String name) {
        String stringBuilder = "SELECT COUNT(" +
                name +
                ") FROM ( \n" +
                sql +
                "\n ) TMP_COUNT";
        return stringBuilder;
    }

    @Override
    public boolean beforePage() {
        Page page = getLocalPage();
        if (page.isOrderByOnly() || page.getPageSize() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public BoundSql getPageSql(BoundSql boundSql) {
        Page page = getLocalPage();
        return getPageSql(boundSql, page);
    }

    abstract BoundSql getPageSql(BoundSql boundSql, Page page);

    @Override
    public MetaDataTable afterPage(MetaDataTable dataTable) {
        Page page = getLocalPage();
        if (page == null) {
            return dataTable;
        }
        page.addAll(dataTable);
        if (!page.isCount()) {
            page.setTotal(-1);
        } else if ((page.getPageSizeZero() != null && page.getPageSizeZero()) && page.getPageSize() == 0) {
            page.setTotal(dataTable.size());
        } else if (page.isOrderByOnly()) {
            page.setTotal(dataTable.size());
        }
        BeanUtils.copyProperties(page, dataTable);
        return dataTable;
    }

    @Override
    public void afterAll() {

    }

}
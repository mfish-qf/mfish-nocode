package cn.com.mfish.common.dblink.page;

import cn.com.mfish.common.dataset.datatable.MetaDataTable;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.page.dialect.AbstractDialect;
import cn.com.mfish.common.dblink.page.dialect.Dialect;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;

import java.util.Collection;

/**
 * @description: 翻页查询
 * @author: mfish
 * @date: 2023/3/21 22:59
 */
public class MfPageHelper extends PageMethod implements Dialect {
    private final DialectAdapter dialectAdapter = new DialectAdapter();

    public void initAdapter(DataSourceOptions<?> dataSourceOptions) {
        dialectAdapter.initDelegateDialect(dataSourceOptions);
    }

    protected DialectAdapter getDialectAdapter() {
        return dialectAdapter;
    }

    @Override
    public boolean skip(DataSourceOptions<?> dataSourceOptions, RowBounds rowBounds) {
        Page<?> page = MfPageHelper.getLocalPage();
        if (page == null && rowBounds != null) {
            page = new Page<>(rowBounds.getOffset(), rowBounds.getLimit());
            MfPageHelper.setLocalPage(page);
        }
        //设置默认的 count 列
        if (page != null && StringUtils.isEmpty(page.getCountColumn())) {
            page.setCountColumn("0");
        }
        initAdapter(dataSourceOptions);
        return page == null;
    }

    @Override
    public boolean beforeCount() {
        return dialectAdapter.getDelegate().beforeCount();
    }

    @Override
    public boolean afterCount(long count) {
        return dialectAdapter.getDelegate().afterCount(count);
    }

    @Override
    public String getCountSql(BoundSql boundSql) {
        return dialectAdapter.getDelegate().getCountSql(boundSql);
    }

    @Override
    public boolean beforePage() {
        return dialectAdapter.getDelegate().beforePage();
    }

    @Override
    public BoundSql getPageSql(BoundSql boundSql) {
        return dialectAdapter.getDelegate().getPageSql(boundSql);
    }

    @Override
    public void afterAll() {
        AbstractDialect delegate = dialectAdapter.getDelegate();
        if (delegate != null) {
            delegate.afterAll();
            dialectAdapter.clearDelegate();
        }
        clearPage();
    }

    @Override
    public <T extends Collection> T afterPage(T pageList) {
        AbstractDialect delegate = getDialectAdapter().getDelegate();
        if (delegate != null) {
            return delegate.afterPage(pageList);
        }
        return pageList;
    }

    /**
     * 查询数据
     *
     * @param boundSql 包装SQL
     * @return
     */
    public MetaDataTable query(BoundSql boundSql) {
        AbstractDialect delegate = dialectAdapter.getDelegate();
        if (delegate == null) {
            return null;
        }
        return delegate.getAbQuery().query(boundSql);
    }

    /**
     * 查询数据
     *
     * @param boundSql 包装SQL
     * @param cls      对象类
     * @param <T>      泛型
     * @return
     */
    public <T> Page<T> query(BoundSql boundSql, Class<T> cls) {
        AbstractDialect delegate = dialectAdapter.getDelegate();
        if (delegate == null) {
            return null;
        }
        return delegate.getAbQuery().query(boundSql, cls);
    }
}
package cn.com.mfish.common.dblink.page.dialect;

import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.page.BoundSql;
import org.apache.ibatis.session.RowBounds;

import java.util.Collection;

/**
 * @description: 分页方言
 * @author: mfish
 * @date: 2023/3/21 23:00
 */
public interface Dialect {
    /**
     * 跳过 count 和 分页查询
     *
     * @param rowBounds       分页参数
     * @return true 跳过，返回默认查询结果，false 执行分页查询
     */
    boolean skip(DataSourceOptions<?> dataSourceOptions, RowBounds rowBounds);

    /**
     * 执行分页前，返回 true 会进行 count 查询，false 会继续下面的 beforePage 判断
     *
     * @return
     */
    boolean beforeCount();

    /**
     * 生成 count 查询 sql
     *
     * @param boundSql        绑定 SQL 对象
     * @return
     */
    String getCountSql(BoundSql boundSql);

    /**
     * 执行完 count 查询后
     *
     * @param count           查询结果总数
     * @return true 继续分页查询，false 直接返回
     */
    boolean afterCount(long count);

    /**
     * 执行分页前，返回 true 会进行分页查询，false 会返回默认查询结果
     *
     * @return
     */
    boolean beforePage();

    /**
     * 生成分页查询 sql
     *
     * @param boundSql        绑定 SQL 对象
     * @return
     */
    BoundSql getPageSql(BoundSql boundSql);

    /**
     * 分页查询后，处理分页结果，拦截器中直接 return 该方法的返回值
     *
     * @param pageList        分页查询结果
     * @return
     */
    <T extends Collection> T afterPage(T pageList);

    /**
     * 完成所有任务后
     */
    void afterAll();
}

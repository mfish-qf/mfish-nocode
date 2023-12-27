package cn.com.mfish.common.dblink.query;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.secret.SM2Utils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.dataset.datatable.MetaDataRow;
import cn.com.mfish.common.dataset.datatable.MetaDataTable;
import cn.com.mfish.common.dblink.db.DBAdapter;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.entity.QueryParam;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.page.BoundSql;
import cn.com.mfish.common.dblink.page.MfPageHelper;
import cn.com.mfish.sys.api.entity.DbConnect;
import com.github.pagehelper.Page;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.function.Function;

/**
 * @description: 分页查询
 * @author: mfish
 * @date: 2023/3/21 22:58
 */
public class QueryHandler {
    static final MfPageHelper pageHelper = new MfPageHelper();

    /**
     * 查询数据 通过MfPageHelper.start进行分页
     *
     * @param dataSourceOptions 连接属性
     * @param strSql            无参数sql
     * @return
     */
    public static MetaDataTable query(DataSourceOptions<?> dataSourceOptions, String strSql) {
        return query(dataSourceOptions, strSql, null, null);
    }

    /**
     * 查询数据 通过MfPageHelper.start进行分页
     *
     * @param dataSourceOptions 连接属性
     * @param strSql            sql
     * @param params            参数
     * @return
     */
    public static MetaDataTable query(DataSourceOptions<?> dataSourceOptions, String strSql, List<QueryParam> params) {
        return query(dataSourceOptions, new BoundSql(strSql, params), null);
    }

    /**
     * 查询数据通过RowBounds进行分页
     * 如果存在MfPageHelper.start优先使用MfPageHelper.start分页
     *
     * @param dataSourceOptions 连接属性
     * @param strSql            无参sql
     * @param rowBounds         分页参数
     * @return
     */
    public static MetaDataTable query(DataSourceOptions<?> dataSourceOptions, String strSql, RowBounds rowBounds) {
        return query(dataSourceOptions, strSql, null, rowBounds);
    }

    /**
     * 查询数据通过RowBounds进行分页
     * 如果存在MfPageHelper.start优先使用MfPageHelper.start分页
     *
     * @param dataSourceOptions 连接属性
     * @param strSql            sql
     * @param params            参数列表
     * @param rowBounds         分页参数
     * @return
     */
    public static MetaDataTable query(DataSourceOptions<?> dataSourceOptions, String strSql, List<QueryParam> params, RowBounds rowBounds) {
        return query(dataSourceOptions, new BoundSql(strSql, params), rowBounds);
    }

    /**
     * 查询数据 通过MfPageHelper.start进行分页
     *
     * @param dataSourceOptions 连接属性
     * @param boundSql          sql包装类
     * @return
     */
    public static MetaDataTable query(DataSourceOptions<?> dataSourceOptions, BoundSql boundSql) {
        return query(dataSourceOptions, boundSql, null);
    }

    /**
     * 查询数据 通过MfPageHelper.start进行分页
     *
     * @param dataSourceOptions 连接属性
     * @param strSql            无参数sql
     * @return
     */
    public static <T> Page<T> queryT(DataSourceOptions<?> dataSourceOptions, String strSql, Class<T> cls) {
        return queryT(dataSourceOptions, strSql, null, null, cls);
    }

    /**
     * 查询数据 通过MfPageHelper.start进行分页
     *
     * @param dataSourceOptions 连接属性
     * @param strSql            sql
     * @param params            参数
     * @return
     */
    public static <T> Page<T> queryT(DataSourceOptions<?> dataSourceOptions, String strSql, List<QueryParam> params, Class<T> cls) {
        return queryT(dataSourceOptions, new BoundSql(strSql, params), null, cls);
    }

    /**
     * 查询数据通过RowBounds进行分页
     * 如果存在MfPageHelper.start优先使用MfPageHelper.start分页
     *
     * @param dataSourceOptions 连接属性
     * @param strSql            无参sql
     * @param rowBounds         分页参数
     * @return
     */
    public static <T> Page<T> queryT(DataSourceOptions<?> dataSourceOptions, String strSql, RowBounds rowBounds, Class<T> cls) {
        return queryT(dataSourceOptions, strSql, null, rowBounds, cls);
    }

    /**
     * 查询数据通过RowBounds进行分页
     * 如果存在MfPageHelper.start优先使用MfPageHelper.start分页
     *
     * @param dataSourceOptions 连接属性
     * @param strSql            sql
     * @param params            参数列表
     * @param rowBounds         分页参数
     * @return
     */
    public static <T> Page<T> queryT(DataSourceOptions<?> dataSourceOptions, String strSql, List<QueryParam> params, RowBounds rowBounds, Class<T> cls) {
        return queryT(dataSourceOptions, new BoundSql(strSql, params), rowBounds, cls);
    }

    /**
     * 查询数据 通过MfPageHelper.start进行分页
     *
     * @param dataSourceOptions 连接属性
     * @param boundSql          sql包装类
     * @return
     */
    public static <T> Page<T> queryT(DataSourceOptions<?> dataSourceOptions, BoundSql boundSql, Class<T> cls) {
        return queryT(dataSourceOptions, boundSql, null, cls);
    }

    /**
     * 分页处理
     */
    interface PageHandler {
        /**
         * 计数完成后处理
         *
         * @param <R> 类型
         * @return
         */
        <R extends Page> R afterQuery();

        /**
         * 分页查询实现
         *
         * @param boundSql sql包装
         * @param <R>      类型
         * @return
         */
        <R extends Page> R pQuery(BoundSql boundSql);

        /**
         * 直接查询实现
         *
         * @param boundSql sql包装
         * @param <R>      类型
         * @return
         */
        <R extends Page> R dQuery(BoundSql boundSql);
    }

    /**
     * 查询数据通过RowBounds进行分页
     * 如果存在MfPageHelper.start优先使用MfPageHelper.start分页
     *
     * @param dataSourceOptions
     * @param boundSql
     * @param rowBounds
     * @return
     */
    public static <T> Page<T> queryT(DataSourceOptions<?> dataSourceOptions, BoundSql boundSql, RowBounds rowBounds, Class<T> cls) {
        return query(dataSourceOptions, boundSql, rowBounds, new PageHandler() {
            @Override
            public Page<T> afterQuery() {
                return new Page<>();
            }

            @Override
            public Page<T> pQuery(BoundSql boundSql) {
                return pageQuery(boundSql, cls);
            }

            @Override
            public Page<T> dQuery(BoundSql boundSql) {
                return pageHelper.query(boundSql, cls);
            }
        });
    }

    /**
     * 查询数据通过RowBounds进行分页
     * 如果存在MfPageHelper.start优先使用MfPageHelper.start分页
     *
     * @param dataSourceOptions
     * @param boundSql
     * @param rowBounds
     * @return
     */
    public static MetaDataTable query(DataSourceOptions<?> dataSourceOptions, BoundSql boundSql, RowBounds rowBounds) {
        return query(dataSourceOptions, boundSql, rowBounds, new PageHandler() {
            @Override
            public MetaDataTable pQuery(BoundSql boundSql) {
                return pageQuery(boundSql);
            }

            @Override
            public MetaDataTable dQuery(BoundSql boundSql) {
                return pageHelper.query(boundSql);
            }

            /**
             * 总数为0，需要返回列头信息，所以也做了一次查询
             * @return
             */
            @Override
            public MetaDataTable afterQuery() {
                return pageHelper.query(boundSql);
            }
        });
    }

    private static <R extends Page> R query(DataSourceOptions<?> dataSourceOptions, BoundSql boundSql, RowBounds rowBounds, PageHandler pageHandler) {
        try {
            R resultList = null;
            //调用方法判断是否需要进行分页，如果不需要，直接返回结果
            if (!pageHelper.skip(dataSourceOptions, rowBounds)) {
                //判断是否需要进行 count 查询
                if (pageHelper.beforeCount()) {
                    //查询总数
                    Long count = countQuery(boundSql);
                    //处理查询总数，返回 true 时继续分页查询，false 时直接返回
                    if (!pageHelper.afterCount(count)) {
                        //当查询总数为 0 时，处理逻辑
                        return pageHandler.afterQuery();
                    }
                }
                resultList = pageHandler.pQuery(boundSql);
            } else {
                resultList = pageHandler.dQuery(boundSql);
            }
            return pageHelper.afterPage(resultList);
        } finally {
            pageHelper.afterAll();
        }
    }

    /**
     * 计数查询
     *
     * @param boundSql
     * @return
     */
    private static Long countQuery(BoundSql boundSql) {
        //调用方言获取 count sql
        String countSql = pageHelper.getCountSql(boundSql);
        BoundSql boundCount = new BoundSql(countSql, boundSql.getParams());
        //执行 count 查询
        MetaDataTable countResultList = pageHelper.query(boundCount);
        MetaDataRow row = countResultList.get(0);
        return returnCount(row);
    }


    /**
     * 处理返回计数
     *
     * @param row 行数据
     * @return
     */
    private static Long returnCount(MetaDataRow row) {
        if (row.containsColumn("COUNT")) {
            return Long.parseLong(row.getCellValue("COUNT").toString());
        }
        if (row.containsColumn("count(0)")) {
            return Long.parseLong(row.getCellValue("count(0)").toString());
        }
        if (row.containsColumn("count")) {
            return Long.parseLong(row.getCellValue("count").toString());
        }
        return Long.parseLong(row.getCellValue("COUNT(0)").toString());
    }

    /**
     * 查询某页数据
     *
     * @param boundSql 包装SQL
     * @return
     */
    private static MetaDataTable pageQuery(BoundSql boundSql) {
        return pageQuery(boundSql, pageHelper::query);
    }

    /**
     * 查询某页数据 返回对象
     *
     * @param boundSql 包装SQL
     * @param cls      类型
     * @param <T>      反射类型
     * @return
     */
    private static <T> Page<T> pageQuery(BoundSql boundSql, Class<T> cls) {
        return pageQuery(boundSql, boundSql1 -> pageHelper.query(boundSql1, cls));
    }

    private static <R> R pageQuery(BoundSql boundSql, Function<BoundSql, R> function) {
        //判断是否需要进行分页查询
        if (pageHelper.beforePage()) {
            //调用方言获取分页 sql
            BoundSql pageSql = pageHelper.getPageSql(boundSql);
            //执行分页查询
            return function.apply(pageSql);
        }
        return function.apply(boundSql);
    }

    /**
     * 数据库连接转配置信息
     *
     * @param dbConnect 数据库连接信息
     * @return
     */
    public static DataSourceOptions<?> buildDataSourceOptions(DbConnect dbConnect, String privateKey) {
        String pwd = SM2Utils.decrypt(privateKey, dbConnect.getPassword());
        if (StringUtils.isEmpty(pwd)) {
            throw new MyRuntimeException("错误:密码解密失败");
        }
        DBType dbType = DBType.getType(dbConnect.getDbType());
        return new DataSourceOptions<>().setDbType(dbType)
                .setDbName(dbConnect.getDbName())
                .setPoolType(PoolType.getPoolType(dbConnect.getPoolType()))
                .setUser(dbConnect.getUsername())
                .setPassword(pwd)
                .setJdbcUrl(DBAdapter.getDBDialect(dbType).getJdbc(dbConnect.getHost(), dbConnect.getPort(), dbConnect.getDbName()));
    }
}

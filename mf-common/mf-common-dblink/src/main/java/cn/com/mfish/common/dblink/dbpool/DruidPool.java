package cn.com.mfish.common.dblink.dbpool;

import cn.com.mfish.common.dblink.entity.DruidOption;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: druid连接池
 * @author: mfish
 * @date: 2023/3/16
 */
@Slf4j
public class DruidPool implements PoolWrapper<DruidOption> {
    private DruidDataSource dataSource;

    @Override
    public PoolWrapper<DruidOption> wrap(DataSourceOptions<DruidOption> linkOption) {
        this.dataSource = new DruidDataSource();
        dataSource.setDriverClassName(linkOption.getDriverClass());
        dataSource.setUrl(linkOption.getJdbcUrl());
        dataSource.setUsername(linkOption.getUser());
        dataSource.setPassword(linkOption.getPassword());

        //详细配置
        DruidOption option = linkOption.getOptions();
        if (option == null) {
            return this;
        }
        dataSource.setInitialSize(option.getInitialSize());
        dataSource.setMinIdle(option.getMinIdle());
        dataSource.setMaxActive(option.getMaxActive());
        dataSource.setMaxWait(option.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(option.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(option.getMinEvictableIdleTimeMillis());
//        dataSource.setValidationQuery(option.getValidationQuery());
        dataSource.setTestWhileIdle(option.isTestWhileIdle());
        dataSource.setTestOnBorrow(option.isTestOnBorrow());
        dataSource.setTestOnReturn(option.isTestOnReturn());
        dataSource.setPoolPreparedStatements(option.isPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(option.getMaxPoolPreparedStatementPerConnectionSize());
        //业务请求重试次数3次
        dataSource.setConnectionErrorRetryAttempts(3);
        dataSource.setBreakAfterAcquireFailure(true);
        try {
            dataSource.setFilters(option.getFilters());
        } catch (SQLException e) {
            log.error("错误:datasource设置filters异常!", e);
        }
        dataSource.setConnectionProperties(option.getConnectionProperties());
        return this;
    }

    @Override
    public void close() {
        if (this.dataSource == null) {
            return;
        }
        this.dataSource.close();
        this.dataSource = null;
    }

    @Override
    public DruidDataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public Connection getConnection(long maxWaitMillis) throws SQLException {
        return this.dataSource.getConnection(maxWaitMillis);
    }

    @Override
    public boolean isClosed() {
        return this.dataSource.isClosed();
    }
}

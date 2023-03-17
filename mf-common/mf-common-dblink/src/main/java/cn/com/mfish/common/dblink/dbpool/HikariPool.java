package cn.com.mfish.common.dblink.dbpool;

import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.entity.HikariOption;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: hikari连接池
 * @author: mfish
 * @date: 2023/3/16
 */
public class HikariPool implements PoolWrapper<HikariOption> {
    private HikariDataSource dataSource;

    @Override
    public PoolWrapper<HikariOption> wrap(DataSourceOptions<HikariOption> options) {
        this.dataSource = new HikariDataSource();
        dataSource.setDriverClassName(options.getDbType().getDriver());
        dataSource.setJdbcUrl(options.getJdbcUrl());
        dataSource.setUsername(options.getUser());
        dataSource.setPassword(options.getPassword());

        HikariOption option = options.getOptions();
        if (option == null) {
            return this;
        }
        dataSource.setMinimumIdle(option.getMinimumIdle());
        dataSource.setMaximumPoolSize(option.getMaximumPoolSize());
        dataSource.setAutoCommit(option.isAutoCommit());
        dataSource.setIdleTimeout(option.getIdleTimeout());
        dataSource.setMaxLifetime(option.getMaxLifetime());
        dataSource.setConnectionTimeout(option.getConnectionTimeout());
//        dataSource.setConnectionTestQuery(option.getConnectionTestQuery());
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
    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public Connection getConnection(long maxWaitMillis) throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public boolean isClosed() {
        return this.dataSource.isClosed();
    }
}
package cn.com.mfish.common.dblink.dbpool;

import cn.com.mfish.common.dblink.entity.DataSourceOptions;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @description: 不使用连接池
 * @author: mfish
 * @date: 2023/3/16 10:51
 */
public class NoPool implements PoolWrapper<Object> {
    private NoDataSource noDataSource;

    @Override
    public PoolWrapper<Object> wrap(DataSourceOptions<Object> options) {
        this.noDataSource = new NoDataSource(options);
        return this;
    }

    @Override
    public void close() {
        this.noDataSource = null;
    }

    @Override
    public DataSource getDataSource() {
        return noDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.noDataSource.getConnection();
    }

    @Override
    public Connection getConnection(long maxWaitMillis) throws SQLException {
        return this.noDataSource.getConnection(maxWaitMillis);
    }

    @Override
    public boolean isClosed() {
        return this.noDataSource == null;
    }

    private static class NoDataSource implements DataSource {
        private final DataSourceOptions<?> dataSourceOptions;

        public NoDataSource(DataSourceOptions<?> dataSourceOptions) {
            this.dataSourceOptions = dataSourceOptions;
        }

        public Connection getConnection(long maxWaitMillis) throws SQLException {
            setLoginTimeout((int) maxWaitMillis / 1000);
            return getConnection();
        }

        @Override
        public Connection getConnection() throws SQLException {
            return getConnection(this.dataSourceOptions.getUser(), this.dataSourceOptions.getPassword());
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return DriverManager.getConnection(
                    this.dataSourceOptions.getJdbcUrl(),
                    username,
                    password);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return DriverManager.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            DriverManager.setLogWriter(out);
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            DriverManager.setLoginTimeout(seconds);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return DriverManager.getLoginTimeout();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }
    }
}

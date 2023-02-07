package cn.com.mfish.scheduler.common;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import org.quartz.utils.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: 定义数据库provider
 * @author: qiufeng
 * @date: 2023/2/7 22:28
 */
public class MfConnectionProvider implements ConnectionProvider {
    DataSource dataSource;

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void initialize() {
        dataSource = SpringBeanFactory.getBean("dataSource");
    }
}

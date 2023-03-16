package cn.com.mfish.common.dblink;

import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.manger.PoolManager;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: 测试连接池
 * @author: mfish
 * @date: 2023/3/16 22:00
 */
public class TestConnect {
    @Test
    public void poolTest() throws SQLException {
        DataSourceOptions options = new DataSourceOptions();
        options.setUser("root");
        options.setPassword("123456");
        options.setJdbcUrl("jdbc:mysql://localhost:3306/mf_oauth?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
        options.setDriverClass("com.mysql.cj.jdbc.Driver");
        options.setPoolClass("cn.com.mfish.common.dblink.dbpool.HikariPool");
        for (int i = 0; i < 5; i++) {
            try{
                Connection connection = PoolManager.getConnection(options,5);
                System.out.println(connection.isClosed());
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }

        }
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

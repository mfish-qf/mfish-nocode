package cn.com.mfish.common.dblink;

import cn.com.mfish.common.dblink.dbpool.PoolWrapper;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.manger.PoolManager;
import org.junit.Test;

import java.sql.Connection;

/**
 * @description: 测试连接池
 * @author: mfish
 * @date: 2023/3/16 22:00
 */
public class TestConnect {
    @Test
    public void poolTest() {
        DataSourceOptions options = new DataSourceOptions();
        options.setUser("root");
        options.setPassword("123456");
        options.setJdbcUrl("jdbc:mysql://localhost:3306/mf_oauth?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
        options.setDbType(DBType.mysql);
        for (int i = 0; i < 10; i++) {
            try {
                if (i / 2 == 0) {
                    options.setPoolType(PoolType.Hikari);
                } else {
                    options.setPoolType(PoolType.Druid);
                }
                Connection connection = PoolManager.getConnection(options, 5000);
                System.out.println(connection.isClosed());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            } finally {
                PoolManager.release();
            }
        }
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void poolCreate() {
        for (int i = 0; i < 5; i++) {
            PoolWrapper wrapper = PoolType.Druid.createPool();
            System.out.println(wrapper);
        }
    }
}

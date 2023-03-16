package cn.com.mfish.common.dblink.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: hikari连接池属性
 * @author: mfish
 * @date: 2023/3/16
 */
@Data
@Accessors(chain = true)
public class HikariOption {
    /**
     * 连接池中允许的最小连接数
     */
    private int minimumIdle = 2;
    /**
     * 池中最大连接数，包括闲置和使用中的连接 10 如果maxPoolSize小于1，则会被重置
     * 当minIdle<=0被重置为DEFAULT_POOL_SIZE则为10;如果minIdle>0则重置为minIdle的值
     */
    private int maximumPoolSize = 100;

    /**
     * 自动提交
     */
    private boolean autoCommit = true;
    /**
     * 一个连接idle状态的最大时长（毫秒），超时则被释放（retired）
     */
    private int idleTimeout = 60000;

    /**
     * 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired）
     * 缺省:30分钟，建议设置比数据库超时时长少30秒
     */
    private int maxLifetime = 300000;

    /**
     * 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException
     */
    private int connectionTimeout = 30000;

    /**
     * 数据库连接测试语句
     */
    private String connectionTestQuery = "SELECT 1";

}

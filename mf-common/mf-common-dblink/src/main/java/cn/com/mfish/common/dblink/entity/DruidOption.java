package cn.com.mfish.common.dblink.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: druid连接池属性项
 * @author: mfish
 * @date: 2023/3/16
 */
@Data
@Accessors(chain = true)
public class DruidOption {
    /**
     * 初始连接数
     */
    private int initialSize = 2;

    /**
     * 最小连接池数量
     */
    private int minIdle = 1;

    /**
     * 最大连接池数量
     */
    private int maxActive = 20;

    /**
     * 配置获取连接等待超时的时间
     */
    private int maxWait = 60000;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private int timeBetweenEvictionRunsMillis = 60000;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private int minEvictableIdleTimeMillis = 300000;

    /**
     * 配置一个连接在池中最大生存的时间，单位是毫秒
     */
    private int maxEvictableIdleTimeMillis = 900000;
    /**
     * 配置检测连接是否有效
     */
    private String validationQuery = "SELECT 1 FROM DUAL";
    /**
     * 当应用向连接池申请连接，并且testOnBorrow为false时，连接池将会判断连接是否处于空闲状态，如果是，则验证这条连接是否可用。
     */
    private boolean testWhileIdle = true;

    /**
     * 当应用向连接池申请连接时，连接池会判断这条连接是否是可用的
     */
    private boolean testOnBorrow = false;
    /**
     * 归还连接时是否会进行检查，检查不通过，销毁
     */
    private boolean testOnReturn = false;
    /**
     * 是否缓存游标
     */
    private boolean poolPreparedStatements = true;
    /**
     * 游标缓存大小
     */
    private int maxPoolPreparedStatementPerConnectionSize = 20;
    /**
     * 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
     */
    private String filters = "stat,wall";
    /**
     * 通过connectProperties属性来打开mergeSql功能；慢SQL记录
     */
    private String connectionProperties = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000";
}
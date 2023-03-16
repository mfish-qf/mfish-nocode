package cn.com.mfish.common.dblink.manger;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.secret.DesUtils;
import cn.com.mfish.common.dblink.dbpool.PoolWrapper;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 连接池管理
 * @author: mfish
 * @date: 2023/3/16 11:27
 */
@Slf4j
public class PoolManager {

    private PoolManager() {
    }

    static {
        poolCheck();
    }

    private static final Map<String, PoolContext> poolMap = new ConcurrentHashMap<>(100);
    //检查成功后 下次检测间隔一分钟
    private static final long INTERVAL = 60000;
    //每次检测 休眠5秒
    private static final long SLEEP_TIME = 5000;
    //连接超时时间
    private static final long TIMEOUT = 5000;
    //连接池最长活跃时间，一天不使用的连接池直接关闭
    private static final long EXPIRE = 24 * 60 * 60;
    //获取不到连接的重试次数，超过次数移除该线程池
    private static final long RETRY_COUNT = 2;
    //线程锁
    private static volatile String poolLock = "poolLock";

    /**
     * 连接池连接状态检测
     */
    private static void poolCheck() {
        new Thread(() -> {
            Map<String, Integer> map = new HashMap<>();
            while (true) {
                for (Map.Entry<String, PoolContext> entry : poolMap.entrySet()) {
                    String key = entry.getKey();
                    PoolContext poolContext = entry.getValue();
                    if (poolContext.getExpire() <= 0) {
                        clearPool(key);
                        map.remove(key);
                        continue;
                    }
                    //计算剩余过期时间，稍有误差忽略不计
                    poolContext.setExpire(poolContext.getExpire() - SLEEP_TIME / 1000);
                    //一分钟内只检测一次
                    if (System.currentTimeMillis() - poolContext.getCheckTime() < INTERVAL) {
                        //上次请求在一分钟以内不检查该连接
                        continue;
                    }
                    Integer count = map.get(key);
                    try (Connection conn = poolContext.getConnection(TIMEOUT)) {
                        //测试连接是否正常，异常连接情况下druid存在获取连接超时卡死问题此处设置请求尝试时间
                        count = 0;
                        poolContext.setCheckTime(System.currentTimeMillis());
                    } catch (Exception ex) {
                        count = count != null ? ++count : 1;
                        log.error(MessageFormat.format("连接{0}----连接异常{1}", key, count));
                    }
                    try {
                        if (count > RETRY_COUNT) {
                            clearPool(key);
                            map.remove(key);
                            continue;
                        }
                        map.put(key, count);
                    } catch (Exception ex) {
                        log.error(MessageFormat.format("{0}:连接池移除异常", key), ex);
                    }
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    log.error("错误:线程等待异常!", e);
                }
            }
        }).start();
    }

    /**
     * 创建连接池对象
     *
     * @param className
     * @return
     */
    private static PoolWrapper create(final String className) {
        try {
            return (PoolWrapper) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error("创建连接池异常", e);
            throw new MyRuntimeException("创建连接池异常", e);
        }
    }

    /**
     * 销毁连接
     *
     * @param conn
     * @param stmt
     * @param rs
     */
    public static void release(final Connection conn, final Statement stmt, final ResultSet rs) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (final SQLException ex) {
            log.error("数据库资源释放异常", ex);
            throw new MyRuntimeException("数据库资源释放异常", ex);
        }
    }

    /**
     * 获取连接
     *
     * @param options
     * @param timeout
     * @return
     */
    public static Connection getConnection(final DataSourceOptions options, long timeout) {
        //用数据源用户名,密码,jdbcUrl做为key
        final String key = String.format("%s|%s|%s|%s", options.getUser()
                , DesUtils.getEncryptString(options.getPassword())
                , options.getPoolClass(), options.getJdbcUrl()).toLowerCase();
        if (!poolMap.containsKey(key)) {
            //加锁防止多个线程同时操作时，后初始化的池子覆盖了前面的池子
            synchronized (poolLock) {
                if (!poolMap.containsKey(key)) {
                    poolMap.put(key, new PoolContext(create(options.getPoolClass()).wrap(options)));
                }
            }
        }
        try {
            PoolContext poolContext = poolMap.get(key);
            Connection connection = poolContext.getConnection(timeout);
            //获取连接成功后设置检测计时
            poolContext.setCheckTime(System.currentTimeMillis());
            //获取连接后，延长连接池存活时间
            poolContext.setExpire(EXPIRE);
            return connection;
        } catch (SQLException e) {
            log.error("获取连接异常", e);
            throw new MyRuntimeException(e);
        }
    }

    /**
     * 清理无用连接池
     *
     * @param key
     */
    private static void clearPool(String key) {
        poolMap.get(key).close();
        poolMap.remove(key);
    }


    /**
     * 获取数据连接
     *
     * @param options
     * @return
     */
    public static Connection getConnection(final DataSourceOptions options) {
        return getConnection(options, TIMEOUT);
    }
}
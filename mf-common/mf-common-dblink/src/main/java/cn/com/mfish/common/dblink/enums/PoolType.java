package cn.com.mfish.common.dblink.enums;

import cn.com.mfish.common.dblink.dbpool.DruidPool;
import cn.com.mfish.common.dblink.dbpool.HikariPool;
import cn.com.mfish.common.dblink.dbpool.NoPool;
import cn.com.mfish.common.dblink.dbpool.PoolWrapper;

/**
 * @description: 连接池枚举
 * @author: mfish
 * @date: 2023/3/17
 */
public enum PoolType {
    NoPool("db_no_pool"),
    Hikari("db_pool_hikari"),
    Druid("db_pool_druid");

    private final String value;

    PoolType(String value) {
        this.value = value;
    }

    /**
     * 获取连接池类型
     * <p>
     * 根据输入的字符串值，返回对应的连接池类型枚举值如果找不到匹配的值，则返回NoPool（无连接池）
     *
     * @param value 表示连接池类型的字符串值
     * @return 对应的连接池类型枚举值，如果没有匹配则返回NoPool
     */
    public static PoolType getPoolType(String value) {
        for (PoolType type : PoolType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return NoPool;
    }

    /**
     * 创建连接池
     *
     * @return 返回一个连接池实例，类型根据当前配置决定
     */
    public PoolWrapper<?> createPool() {
        return switch (this) {
            case Druid -> new DruidPool();
            case Hikari -> new HikariPool();
            default -> new NoPool();
        };
    }

}

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
     *
     * @param value
     * @return
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
     * @return
     */
    public PoolWrapper<?> createPool() {
        switch (this) {
            case Druid:
                return new DruidPool();
            case Hikari:
                return new HikariPool();
            default:
                return new NoPool();
        }
    }

}

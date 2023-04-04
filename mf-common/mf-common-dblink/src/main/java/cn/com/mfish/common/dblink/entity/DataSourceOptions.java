package cn.com.mfish.common.dblink.entity;

import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.enums.PoolType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 连接基本信息
 * @author: mfish
 * @date: 2023/3/16
 */
@Data
@Accessors(chain = true)
public class DataSourceOptions<T> {
    /**
     * 数据库登录用户名
     */
    private String user;
    /**
     * 数据库登录密码
     */
    private String password;
    /**
     * 数据库连接字符串(JDBC)
     */
    private String jdbcUrl;
    /**
     * 数据库类型
     */
    private DBType dbType;
    /**
     * 连接池类型
     */
    private PoolType poolType;
    /**
     * 数据库名称
     */
    private String dbName;
    /**
     * 数据源配置选项(JSON格式）
     */
    private T options;
}

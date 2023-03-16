package cn.com.mfish.common.dblink.entity;

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
     * 数据源驱动类
     */
    private String driverClass;
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
     * 引擎查询器使用的数据源连接池类名
     */
    private String poolClass;
    /**
     * 数据源配置选项(JSON格式）
     */
    private T options;
}

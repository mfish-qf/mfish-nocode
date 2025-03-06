package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.dblink.entity.QueryParam;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.page.BoundSql;

import java.text.MessageFormat;

/**
 * @description: 抽象数据库相关信息
 * @author: mfish
 * @date: 2023/3/24 20:35
 */
public abstract class AbstractDBDialect implements DBDialect {
    /**
     * 构建 JDBC URL 用于连接 MySQL 或 PostgreSQL 数据库
     * 该方法专注于组装特定格式的 JDBC URL，确保兼容性和可配置性
     *
     * @param host   数据库服务器地址
     * @param port   服务器端口
     * @param dbName 要连接的数据库名称
     * @param head   JDBC URL 的头部，区别 MySQL 和 PostgreSQL 的标识
     * @return 组装完成的 JDBC URL
     */
    protected String getJdbc(String host, String port, String dbName, String head) {
        dbName = !StringUtils.isEmpty(dbName) ? "/" + dbName : "";
        return MessageFormat.format("{0}://{1}:{2}{3}?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai", head, host, port, dbName);
    }

    /**
     * 构建MySQL和PostgreSQL通用的查询条件
     * 该方法旨在为给定的SQL查询动态添加表名和数据库名作为查询条件
     * 它还负责生成与数据库方案匹配的参数，考虑大小写的情况
     *
     * @param sql       初始SQL查询字符串该查询通常不包含具体的表名或数据库名条件
     * @param tableSchema    表前缀非空时，将被用作查询条件的一部分
     * @param tableName 表名称非空时，将被用作查询条件的一部分
     * @return BoundSql对象，其中包含经过条件补充的SQL查询语句及相关的参数
     */
    protected BoundSql buildCondition(String sql, String tableSchema, String tableName) {
        BoundSql boundSql = new BoundSql();
        boundSql.setDbType(DBType.mysql);
        if (!StringUtils.isEmpty(tableName)) {
            sql += " and (table_name = ? or table_name = ?) ";
            boundSql.getParams().add(new QueryParam().setValue(tableName.toUpperCase()));
            boundSql.getParams().add(new QueryParam().setValue(tableName.toLowerCase()));
        }
        if (!StringUtils.isEmpty(tableSchema)) {
            sql += " and (table_schema = ? or table_schema = ?)";
            boundSql.getParams().add(new QueryParam().setValue(tableSchema.toUpperCase()));
            boundSql.getParams().add(new QueryParam().setValue(tableSchema.toLowerCase()));
        }
        return boundSql.setSql(sql);
    }
}

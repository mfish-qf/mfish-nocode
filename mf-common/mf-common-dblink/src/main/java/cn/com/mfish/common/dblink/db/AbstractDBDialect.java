package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.dblink.page.BoundSql;

import java.text.MessageFormat;

/**
 * @description: 抽象数据库相关信息
 * @author: mfish
 * @date: 2023/3/24 20:35
 */
public abstract class AbstractDBDialect implements DBDialect {
    /**
     * mysql pg通用 jdbc
     * @param host 地址
     * @param port 端口
     * @param dbName 数据库名称
     * @param head 头部标签
     * @return
     */
    protected String getJdbc(String host, String port, String dbName, String head) {
        if (!StringUtils.isEmpty(dbName)) {
            dbName = "/" + dbName;
        } else {
            dbName = "";
        }
        return MessageFormat.format("{0}://{1}:{2}{3}?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&serverTimezone=Asia/Shanghai", head, host, port, dbName);
    }

    /**
     * mysql pg通用查询条件
     * @param sql sql
     * @param dbName 数据库名称
     * @param tableName 表名称
     * @return
     */
    protected BoundSql buildCondition(String sql, String dbName, String tableName) {
        BoundSql boundSql = new BoundSql();
        if (!StringUtils.isEmpty(tableName)) {
            sql += " and table_name = ?";
            boundSql.getParams().add(tableName);
        }
        if (!StringUtils.isEmpty(dbName)) {
            sql += " and table_schema = ?";
            boundSql.getParams().add(dbName);
        }
        return boundSql.setSql(sql);
    }
}

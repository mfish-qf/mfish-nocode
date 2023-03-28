package cn.com.mfish.common.dblink.db;

import cn.com.mfish.common.dblink.page.BoundSql;

/**
 * @description: 代码构建相关语句
 * @author: mfish
 * @date: 2023/3/23 22:08
 */
public interface DBDialect {
    String getJdbc(String host, String port, String dbName);

    /**
     * 获取表字段信息
     *
     * @param dbName    库名
     * @param tableName 表名
     * @return
     */
    BoundSql getColumns(String dbName, String tableName);

    /**
     * 获取表信息
     *
     * @param dbName    库名
     * @param tableName 表名
     * @return
     */
    BoundSql getTableInfo(String dbName, String tableName);
}

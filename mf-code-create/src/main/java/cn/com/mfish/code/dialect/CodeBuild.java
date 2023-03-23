package cn.com.mfish.code.dialect;

import cn.com.mfish.common.dblink.page.BoundSql;

/**
 * @description: 代码构建相关语句
 * @author: mfish
 * @date: 2023/3/23 22:08
 */
public interface CodeBuild {
    /**
     * 获取表字段信息
     *
     * @param schema    库名
     * @param tableName 表名
     * @return
     */
    BoundSql getColumns(String schema, String tableName);

    /**
     * 获取表信息
     *
     * @param schema  库名
     * @param tableName 表名
     * @return
     */
    BoundSql getTableInfo(String schema, String tableName);
}

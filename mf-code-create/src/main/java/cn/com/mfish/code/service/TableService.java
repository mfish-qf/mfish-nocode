package cn.com.mfish.code.service;

import cn.com.mfish.code.entity.FieldInfo;
import cn.com.mfish.code.entity.TableInfo;

import java.util.List;

/**
 * @author: mfish
 * @description: 表信息
 * @date: 2022/8/31 22:59
 */
public interface TableService {
    /**
     * 获取表字段信息
     *
     * @param connectId    数据库连接ID
     * @param tableName 表名
     * @return
     */
    List<FieldInfo> getColumns(String connectId, String tableName);

    /**
     * 获取表信息
     *
     * @param connectId    数据库连接ID
     * @param tableName 表名
     * @return
     */
    TableInfo getTableInfo(String connectId, String tableName);
}

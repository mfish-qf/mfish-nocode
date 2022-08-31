package cn.com.mfish.code.service;

import cn.com.mfish.code.entity.FieldInfo;

import java.util.List;

/**
 * @author ：qiufeng
 * @description：表信息
 * @date ：2022/8/31 22:59
 */
public interface MysqlTableService {
    List<FieldInfo> getColumns(String schema, String tableName);
}

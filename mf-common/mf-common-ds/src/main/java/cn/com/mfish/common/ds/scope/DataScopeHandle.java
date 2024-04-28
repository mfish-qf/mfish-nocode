package cn.com.mfish.common.ds.scope;

/**
 * @description: 数据范围处理
 * @author: mfish
 * @date: 2024/4/26
 */
public interface DataScopeHandle {

    String sqlChange(String sql, String table, String fieldName, String value);
}

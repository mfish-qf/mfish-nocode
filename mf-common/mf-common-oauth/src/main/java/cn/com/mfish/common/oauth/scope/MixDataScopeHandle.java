package cn.com.mfish.common.oauth.scope;


import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.oauth.common.DataScopeUtils;
import cn.com.mfish.common.oauth.common.DataScopeValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 混合数据范围控制
 * @author: mfish
 * @date: 2024/4/29
 */
public class MixDataScopeHandle {

    /**
     * 转换为数据权限SQL
     * 该方法主要用于根据提供的数据权限信息，动态修改SQL查询语句，以实现数据权限的控制
     * 它会检查每个提供的数据权限字段，并在原始SQL语句中加入相应的限制条件，以确保数据查询符合权限设置
     *
     * @param oriSql 原始SQL语句
     * @param map 包含数据权限信息的映射，键为字段名，值为该字段的数据权限值列表
     * @return 修改后的SQL语句
     */
    public static String sqlChange(String oriSql, Map<String, List<DataScopeValue>> map) {
        for (Map.Entry<String, List<DataScopeValue>> entry : map.entrySet()) {
            if (!oriSql.toLowerCase().contains(entry.getKey())) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (DataScopeValue value : entry.getValue()) {
                DataScopeHandle handle = value.getDataScopeHandle();
                if (handle == null) {
                    continue;
                }
                sb.append(handle.buildCondition(value.getFieldName(), value.getValues()));
                sb.append(" and ");
            }
            if (!sb.isEmpty()) {
                sb.delete(sb.length() - 5, sb.length());
            }
            oriSql = sqlChange(oriSql, entry.getKey(), sb.toString());
        }
        return oriSql;
    }

    /**
     * 转换为数据权限SQL
     *
     * @param sql 原始SQL语句
     * @param table 表名
     * @param condition 数据权限条件
     * @return 转换后的SQL语句
     */
    private static String sqlChange(String sql, String table, String condition) {
        List<DataScopeUtils.SqlSplit> list = DataScopeUtils.splitSql(sql, table);
        for (DataScopeUtils.SqlSplit split : list) {
            if (split.isHaveTable()) {
                split.setStatement(split.getStatement().replaceAll(table, buildReplaceSql(table, condition)));
                if (split.isHaveAlias()) {
                    continue;
                }
                split.setStatement(split.getStatement() + " " + table);
            }
        }
        return list.stream().map(DataScopeUtils.SqlSplit::getStatement).collect(Collectors.joining());
    }

    /**
     * 构建替换SQL
     *
     * @param table 表名
     * @param condition 数据权限条件
     * @return 替换后的SQL
     */
    private static String buildReplaceSql(String table, String condition) {
        if (StringUtils.isEmpty(condition)) {
            return table;
        }
        return "(select * from " + table +
                " where " +
                condition +
                ")";
    }
}

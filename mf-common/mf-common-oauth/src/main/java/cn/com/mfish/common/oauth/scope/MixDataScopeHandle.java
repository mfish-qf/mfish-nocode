package cn.com.mfish.common.oauth.scope;


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
     *
     * @param oriSql
     * @param map
     * @return
     */
    public static String sqlChange(String oriSql, Map<String, List<DataScopeValue>> map) {
        for (Map.Entry<String, List<DataScopeValue>> entry : map.entrySet()) {
            if (!oriSql.toLowerCase().contains(entry.getKey())) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (DataScopeValue value : entry.getValue()) {
                sb.append(value.getDataScopeHandle().buildCondition(value.getFieldName(), value.getValues()));
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
     * @param sql
     * @param table
     * @param condition
     * @return
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
     * @param table
     * @param condition
     * @return
     */
    private static String buildReplaceSql(String table, String condition) {
        return "(select * from " + table +
                " where " +
                condition +
                ")";
    }
}

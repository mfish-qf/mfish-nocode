package cn.com.mfish.common.ds.scope;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.ds.common.DataScopeUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 租户数据处理
 * @author: mfish
 * @date: 2024/4/26
 */
public class TenantDataScopeHandle implements DataScopeHandle {
    @Override
    public String sqlChange(String sql, String table, String fieldName,String value) {
        fieldName = StringUtils.isEmpty(fieldName) ? "tenant_id" : fieldName;
        value = StringUtils.isEmpty(value) ? AuthInfoUtils.getCurrentTenantId() : value;
        List<DataScopeUtils.SqlSplit> list = DataScopeUtils.splitSql(sql, table);
        for (DataScopeUtils.SqlSplit split : list) {
            if (split.isHaveTable()) {
                split.setStatement(split.getStatement().replaceAll(table
                        , "(select * from " + table + " where " + fieldName + "='" + value + "')"));
                if (split.isHaveAlias()) {
                    continue;
                }
                split.setStatement(split.getStatement() + " " + table);
            }
        }
        return list.stream().map(DataScopeUtils.SqlSplit::getStatement).collect(Collectors.joining());
    }
}

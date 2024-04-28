package cn.com.mfish.common.ds.scope;

/**
 * @description: 租户数据处理
 * @author: mfish
 * @date: 2024/4/26
 */
public class RoleDataScopeHandle implements DataScopeHandle {

    @Override
    public String sqlChange(String sql, String table, String fieldName) {
        //todo 补充逻辑
        return sql;
    }
}

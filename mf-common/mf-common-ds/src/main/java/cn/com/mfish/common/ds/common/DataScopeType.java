package cn.com.mfish.common.ds.common;

import cn.com.mfish.common.ds.scope.DataScopeHandle;
import cn.com.mfish.common.ds.scope.RoleDataScopeHandle;
import cn.com.mfish.common.ds.scope.TenantDataScopeHandle;
import lombok.Getter;

/**
 * @description: 数据范围
 * @author: mfish
 * @date: 2024/4/26
 */
@Getter
public enum DataScopeType {
    Tenant(0, new TenantDataScopeHandle()),
    Role(1, new RoleDataScopeHandle());
    private final int value;
    private final DataScopeHandle handle;

    DataScopeType(int value, DataScopeHandle handle) {
        this.value = value;
        this.handle = handle;
    }

}

package cn.com.mfish.common.oauth.scope;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.scope.DataScopeHandle;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.api.remote.RemoteRoleService;
import cn.com.mfish.common.oauth.api.remote.RemoteUserService;
import cn.com.mfish.common.oauth.common.DataScopeUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @description: 租户数据处理
 * @author: mfish
 * @date: 2024/4/26
 */
public class RoleDataScopeHandle implements DataScopeHandle {
    private static final String DEFAULT_FIELD = "role_id";

    @Override
    public String buildCondition(String fieldName, String[] values) {
        fieldName = StringUtils.isEmpty(fieldName) ? DEFAULT_FIELD : fieldName;
        if (values == null || values.length == 0) {
            //未传值时使用当前用户的所有角色id
            values = getCurRoles();
        } else {
            values = getRoleIdsByCode(values);
        }
        return DataScopeUtils.buildCondition(fieldName, values);
    }

    private String[] getCurRoles() {
        final String userId = AuthInfoUtils.getCurrentUserId();
        final String tenantId = AuthInfoUtils.getCurrentTenantId();
        RemoteUserService remoteUserService = SpringBeanFactory.getRemoteService(RemoteUserService.class);
        //异步调用防止切面主线程使用了PageHelper分页交叉
        CompletableFuture<Result<List<UserRole>>> future = CompletableFuture.supplyAsync(() -> remoteUserService.getRoles(RPCConstants.INNER, userId, tenantId));
        try {
            Result<List<UserRole>> result = future.get();
            if (!result.isSuccess()) {
                throw new MyRuntimeException(result.getMsg());
            }
            return result.getData().stream().map(UserRole::getId).toList().toArray(String[]::new);
        } catch (InterruptedException | ExecutionException e) {
            throw new MyRuntimeException(e.getMessage());
        }
    }

    private String[] getRoleIdsByCode(final String[] values) {
        final String tenantId = AuthInfoUtils.getCurrentTenantId();
        RemoteRoleService remoteRoleService = SpringBeanFactory.getRemoteService(RemoteRoleService.class);
        //异步调用防止切面主线程使用了PageHelper分页交叉
        CompletableFuture<Result<List<String>>> future = CompletableFuture.supplyAsync(() -> remoteRoleService.getRoleIdsByCode(RPCConstants.INNER, tenantId, String.join(",", values)));
        try {
            Result<List<String>> result = future.get();
            if (!result.isSuccess()) {
                throw new MyRuntimeException(result.getMsg());
            }
            return result.getData().toArray(new String[0]);
        } catch (InterruptedException | ExecutionException e) {
            throw new MyRuntimeException(e.getMessage());
        }
    }
}

package cn.com.mfish.common.oauth.scope;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.remote.RemoteOrgService;
import cn.com.mfish.common.oauth.api.remote.RemoteUserService;
import cn.com.mfish.common.oauth.common.DataScopeUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @description: 组织数据权限控制
 * @author: mfish
 * @date: 2024/4/29
 */
public class OrgDataScopeHandle implements DataScopeHandle {
    private static final String DEFAULT_FIELD = "org_id";

    @Override
    public String buildCondition(String fieldName, String[] values, String[] excludes) {
        fieldName = StringUtils.isEmpty(fieldName) ? DEFAULT_FIELD : fieldName;
        if (values == null || values.length == 0) {
            //未传值时使用当前用户的所有角色id
            values = getCurOrgs();
        } else {
            values = getOrgIdsByCode(values);
        }
        return DataScopeUtils.buildCondition(fieldName, values, excludes);
    }

    private String[] getCurOrgs() {
        final String userId = AuthInfoUtils.getCurrentUserId();
        final String tenantId = AuthInfoUtils.getCurrentTenantId();
        RemoteUserService remoteUserService = SpringBeanFactory.getRemoteService(RemoteUserService.class);
        //异步调用防止切面主线程使用了PageHelper分页交叉
        CompletableFuture<Result<List<String>>> future = CompletableFuture.supplyAsync(() -> remoteUserService.getOrgIds(RPCConstants.INNER, userId, tenantId, TreeDirection.向下.toString()));
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

    private String[] getOrgIdsByCode(final String[] values) {
        final String tenantId = AuthInfoUtils.getCurrentTenantId();
        RemoteOrgService remoteOrgService = SpringBeanFactory.getRemoteService(RemoteOrgService.class);
        //异步调用防止切面主线程使用了PageHelper分页交叉
        CompletableFuture<Result<List<String>>> future = CompletableFuture.supplyAsync(() -> remoteOrgService.getOrgIdsByFixCode(RPCConstants.INNER, tenantId, String.join(",", values), TreeDirection.向下.toString()));
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

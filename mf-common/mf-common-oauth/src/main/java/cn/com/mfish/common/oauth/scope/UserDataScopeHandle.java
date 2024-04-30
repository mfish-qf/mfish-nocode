package cn.com.mfish.common.oauth.scope;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.scope.DataScopeHandle;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.remote.RemoteUserService;
import cn.com.mfish.common.oauth.common.DataScopeUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @description: 用户数据权限限制
 * @author: mfish
 * @date: 2024/4/29
 */
public class UserDataScopeHandle implements DataScopeHandle {
    private static final String DEFAULT_FIELD = "user_id";

    @Override
    public String buildCondition(String fieldName, String[] values) {
        fieldName = StringUtils.isEmpty(fieldName) ? DEFAULT_FIELD : fieldName;
        if (values == null || values.length == 0) {
            //未传值时使用当前用户id
            values = new String[]{AuthInfoUtils.getCurrentUserId()};
        } else {
            values = getUserIdsByAccount(values);
        }
        return DataScopeUtils.buildCondition(fieldName, values);
    }


    private String[] getUserIdsByAccount(final String[] values) {
        RemoteUserService remoteUserService = SpringBeanFactory.getRemoteService(RemoteUserService.class);
        //异步调用防止切面主线程使用了PageHelper分页交叉
        CompletableFuture<Result<List<String>>> future = CompletableFuture.supplyAsync(() -> remoteUserService.getUserIdsByAccounts(RPCConstants.INNER, String.join(",", values)));
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
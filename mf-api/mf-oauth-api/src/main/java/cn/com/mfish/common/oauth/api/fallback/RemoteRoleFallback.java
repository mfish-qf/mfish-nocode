package cn.com.mfish.common.oauth.api.fallback;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.remote.RemoteRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 角色远程调用失败处理
 * @author: mfish
 * @date: 2024/4/29
 */
@Slf4j
@Component
public class RemoteRoleFallback implements FallbackFactory<RemoteRoleService> {
    @Override
    public RemoteRoleService create(Throwable cause) {
        log.error("角色服务调用失败:{}", cause.getMessage());
        return new RemoteRoleService() {
            @Override
            public Result<List<String>> getRoleIdsByCode(String origin, String tenantId, String codes) {
                return Result.fail("错误:获取角色id失败" + cause.getMessage());
            }

            @Override
            public Result<List<String>> getRoleUsers(String origin, String tenantId, String codes) {
                return Result.fail("错误:获取用户id失败" + cause.getMessage());
            }
        };
    }
}

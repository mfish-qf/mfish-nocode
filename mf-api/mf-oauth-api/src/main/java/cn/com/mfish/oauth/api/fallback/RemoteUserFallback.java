package cn.com.mfish.oauth.api.fallback;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.api.entity.UserInfo;
import cn.com.mfish.oauth.api.remote.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author ：qiufeng
 * @description：Feign用户接口降级
 * @date ：2021/12/4 0:27
 */
@Component
@Slf4j
public class RemoteUserFallback implements FallbackFactory<RemoteUserService> {
    @Override
    public RemoteUserService create(Throwable cause) {
        log.error("token服务调用失败:" + cause.getMessage());
        return new RemoteUserService() {
            @Override
            public Result<UserInfo> getUserInfo(String origin, String token) {
                return Result.fail("错误:获取用户失败" + cause.getMessage());
            }

            @Override
            public Result<UserInfo> getUserInfo(String origin) {
                return Result.fail("错误:获取当前用户失败" + cause.getMessage());
            }

        };
    }
}

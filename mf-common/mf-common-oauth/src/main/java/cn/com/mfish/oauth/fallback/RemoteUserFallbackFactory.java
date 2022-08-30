package cn.com.mfish.oauth.fallback;

import cn.com.mfish.common.core.web.AjaxTResult;
import cn.com.mfish.oauth.model.UserInfo;
import cn.com.mfish.oauth.remote.RemoteUserService;
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
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {
    @Override
    public RemoteUserService create(Throwable cause) {
        log.error("token服务调用失败:" + cause.getMessage());
        return new RemoteUserService() {
            @Override
            public AjaxTResult<UserInfo> getUserInfo(String origin, String token) {
                return AjaxTResult.fail("错误:获取用户失败" + cause.getMessage());
            }

            @Override
            public AjaxTResult<UserInfo> getUserInfo(String origin) {
                return AjaxTResult.fail("错误:获取当前用户失败" + cause.getMessage());
            }
        };
    }
}

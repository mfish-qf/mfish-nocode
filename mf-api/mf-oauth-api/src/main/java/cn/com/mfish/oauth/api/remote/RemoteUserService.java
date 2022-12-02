package cn.com.mfish.oauth.api.remote;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.api.entity.UserInfo;
import cn.com.mfish.oauth.api.fallback.RemoteUserFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author ：qiufeng
 * @description：RPC用户服务
 * @date ：2021/12/1 17:10
 */
@FeignClient(contextId = "remoteUserService", value = ServiceConstants.OAUTH_SERVICE, fallbackFactory = RemoteUserFallback.class)
public interface RemoteUserService {
    /**
     * 根据token获取用户信息
     *
     * @param token
     * @param origin
     * @return
     */
    @GetMapping("/user/info")
    Result<UserInfo> getUserInfo(@RequestHeader(CredentialConstants.REQ_ORIGIN) String origin, @RequestHeader(Constants.AUTHENTICATION) String token);

    @GetMapping("/user/{id}")
    Result<UserInfo> getUserById(@RequestHeader(CredentialConstants.REQ_ORIGIN) String origin, @PathVariable("id") String id);

}
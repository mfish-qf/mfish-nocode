package cn.com.mfish.common.oauth.api.fallback;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.remote.RemoteOrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 组织远程调用失败处理
 * @author: mfish
 * @date: 2023/5/22 11:28
 */
@Slf4j
@Component
public class RemoteOrgFallback implements FallbackFactory<RemoteOrgService> {
    @Override
    public RemoteOrgService create(Throwable cause) {
        log.error("组织服务调用失败:{}", cause.getMessage());
        return new RemoteOrgService() {
            @Override
            public Result<List<SsoOrg>> queryById(String origin, String ids) {
                return Result.fail("错误:根据ID获取组织列表失败" + cause.getMessage());
            }

            @Override
            public Result<List<SsoOrg>> queryByFixCode(String origin, String code, String direction) {
                return Result.fail("错误:根据编码获取组织树失败" + cause.getMessage());
            }

            @Override
            public Result<PageResult<UserInfo>> queryUserByCode(String origin, String code, String account, String nickname, String phone, ReqPage reqPage) {
                return Result.fail("错误:根据编码获取组织下用户失败" + cause.getMessage());
            }
        };
    }
}

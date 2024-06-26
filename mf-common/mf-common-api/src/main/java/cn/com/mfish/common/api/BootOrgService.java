package cn.com.mfish.common.api;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.remote.RemoteOrgService;
import cn.com.mfish.common.oauth.req.ReqOrgUser;
import cn.com.mfish.common.oauth.service.SsoOrgService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * @description: 组织远程接口本地实现
 * @author: mfish
 * @date: 2024/4/16
 */
@Service("remoteOrgService")
public class BootOrgService implements RemoteOrgService {
    @Resource
    SsoOrgService ssoOrgService;

    @Override
    public Result<List<SsoOrg>> queryById(String origin, String ids) {
        return ssoOrgService.queryByIds(ids);
    }

    @Override
    public Result<List<SsoOrg>> queryByFixCode(String origin, String code, String direction) {
        return Result.ok(ssoOrgService.queryOrgByCode(code, TreeDirection.getDirection(direction)), "组织结构表-查询成功!");
    }

    @Override
    public Result<PageResult<UserInfo>> queryUserByCode(String origin, String code, String account, String nickname, String phone, ReqPage reqPage) {
        return Result.ok(ssoOrgService.queryUserByCode(code
                        , new ReqOrgUser().setAccount(account).setNickname(nickname).setPhone(phone), reqPage)
                , "组织下用户查询成功");
    }

    @Override
    public Result<List<String>> getOrgIdsByFixCode(String origin, String tenantId, String codes, String direction) {
        return ssoOrgService.getOrgIdsByFixCode(tenantId, List.of(codes.split(",")), TreeDirection.getDirection(direction));
    }

    @Override
    public Result<List<String>> getOrgIdsById(String origin, String tenantId, String orgIds, String direction) {
        return ssoOrgService.getOrgIdsById(tenantId, List.of(orgIds.split(",")), TreeDirection.getDirection(direction));
    }
}

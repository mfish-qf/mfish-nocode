package cn.com.mfish.common.oauth.service;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.req.ReqOrgUser;
import cn.com.mfish.common.oauth.req.ReqSsoOrg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.2.1
 */
public interface SsoOrgService extends IService<SsoOrg> {
    Result<SsoOrg> insertOrg(SsoOrg ssoOrg);

    Result<SsoOrg> updateOrg(SsoOrg ssoOrg);

    List<SsoOrg> queryOrg(ReqSsoOrg reqSsoOrg);

    Result<Boolean> removeOrg(String id);

    int insertOrgRole(String orgId, List<String> roles);

    int deleteOrgRole(String orgId);

    List<SsoOrg> queryOrgByCode(String fixCode, TreeDirection direction);

    List<SsoOrg> queryOrgById(String id, TreeDirection direction);

    List<UserRole> getOrgRoles(String... orgIds);

    Result<List<SsoOrg>> queryByIds(String ids);

    boolean isTenantOrg(String orgId, String tenantId);

    PageResult<UserInfo> queryUserByCode(String code, ReqOrgUser reqOrgUser, ReqPage reqPage);
}

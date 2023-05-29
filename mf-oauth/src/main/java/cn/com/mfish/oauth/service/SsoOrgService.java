package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.0
 */
public interface SsoOrgService extends IService<SsoOrg> {
    Result<SsoOrg> insertOrg(SsoOrg ssoOrg);

    Result<SsoOrg> updateOrg(SsoOrg ssoOrg);

    List<SsoOrg> queryOrg(ReqSsoOrg reqSsoOrg);

    Result<Boolean> removeOrg(String id);

    int insertOrgRole(String orgId, List<String> roles);

    List<SsoOrg> queryOrgByCode(String fixCode, TreeDirection direction);

    List<SsoOrg> queryOrgById(String id, TreeDirection direction);
}

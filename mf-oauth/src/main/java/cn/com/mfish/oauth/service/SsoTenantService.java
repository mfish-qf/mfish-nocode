package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoTenant;
import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.oauth.req.ReqSsoTenant;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.3.2
 */
public interface SsoTenantService extends IService<SsoTenant> {
    List<TenantVo> queryList(ReqSsoTenant reqSsoTenant, ReqPage reqPage);

    TenantVo queryInfo(String id);

    Result<SsoTenant> insertTenant(SsoTenant ssoTenant);

    Result<SsoTenant> updateTenant(SsoTenant ssoTenant);

    Result<Boolean> deleteTenant(String id);

    boolean isTenantMaster(String userId, String tenantId);

    boolean isTenantMasterOrg(String userId, String orgId);

    List<TenantVo> getTenantByRoleCode(String roleCode);

    /**
     * 第三方自动创建用户租户 gitee github等登录创建
     * @param ssoUser 用户信息
     */
    void createTenantUser(SsoUser ssoUser);
}

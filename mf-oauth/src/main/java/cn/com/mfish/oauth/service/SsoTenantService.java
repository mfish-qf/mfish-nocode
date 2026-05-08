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
 * @version: V2.3.1
 */
public interface SsoTenantService extends IService<SsoTenant> {
    /**
     * 分页查询租户列表
     *
     * @param reqSsoTenant 租户查询条件
     * @param reqPage      分页参数
     * @return 租户列表
     */
    List<TenantVo> queryList(ReqSsoTenant reqSsoTenant, ReqPage reqPage);

    /**
     * 查询租户详情
     *
     * @param id 租户ID
     * @return 租户信息
     */
    TenantVo queryInfo(String id);

    /**
     * 新增租户，同时创建对应的组织结构
     *
     * @param ssoTenant 租户信息
     * @return 新增结果
     */
    Result<SsoTenant> insertTenant(SsoTenant ssoTenant);

    /**
     * 更新租户信息，同步更新关联的组织结构
     *
     * @param ssoTenant 租户信息
     * @return 更新结果
     */
    Result<SsoTenant> updateTenant(SsoTenant ssoTenant);

    /**
     * 删除租户，同时逻辑删除关联的组织
     *
     * @param id 租户ID
     * @return 删除结果
     */
    Result<Boolean> deleteTenant(String id);

    /**
     * 判断用户是否为租户管理员
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 是否为管理员
     */
    boolean isTenantMaster(String userId, String tenantId);

    /**
     * 判断用户是否为指定组织的管理员
     *
     * @param userId 用户ID
     * @param orgId  组织ID
     * @return 是否为管理员
     */
    boolean isTenantMasterOrg(String userId, String orgId);

    /**
     * 通过角色编码获取关联的租户列表
     *
     * @param roleCode 角色编码
     * @return 租户列表
     */
    List<TenantVo> getTenantByRoleCode(String roleCode);

    /**
     * 第三方自动创建用户租户 gitee github等登录创建
     * @param ssoUser 用户信息
     */
    void createTenantUser(SsoUser ssoUser);
}

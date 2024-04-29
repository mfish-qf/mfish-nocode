package cn.com.mfish.oauth.mapper;

import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.req.ReqOrgUser;
import cn.com.mfish.common.oauth.req.ReqSsoOrg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.3.0
 */
public interface SsoOrgMapper extends BaseMapper<SsoOrg> {
    int insertOrg(SsoOrg ssoOrg);

    Integer queryMaxOrgLevel(@Param("reqSsoOrg") ReqSsoOrg reqSsoOrg);

    List<SsoOrg> queryOrg(@Param("reqSsoOrg") ReqSsoOrg reqSsoOrg, @Param("levels") List<Integer> levels);

    int orgFixCodeExist(@Param("orgId") String orgId, @Param("orgFixCode") String orgFixCode);

    int insertOrgRole(@Param("orgId") String orgId, @Param("roles") List<String> roles);

    @Delete("delete from sso_org_role where org_id = #{orgId}")
    int deleteOrgRole(String orgId);

    @Select("select count(0) from sso_org_user ou inner join sso_user u on ou.user_id = u.id and u.del_flag = 0 where org_id = #{orgId}")
    int queryUserCount(String orgId);

    @Select("select count(0) from sso_org o where o.parent_id= #{orgId} and o.del_flag =0")
    int queryChildCount(String orgId);

    @Select("select user_id from sso_org_user where org_id = #{orgId}")
    List<String> getOrgUserId(String orgId);

    List<UserRole> getOrgRoles(@Param("orgIds") String... orgIds);

    int isTenantOrg(@Param("orgId") String orgId, @Param("tenantId") String tenantId);

    List<UserInfo> queryUserByCode(@Param("code") String code, @Param("reqOrgUser") ReqOrgUser reqOrgUser);

    List<String> getOrgDownIdsByCode(@Param("orgCodes") List<String> orgCodes);
}

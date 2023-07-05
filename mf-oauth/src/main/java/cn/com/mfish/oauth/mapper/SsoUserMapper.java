package cn.com.mfish.oauth.mapper;

import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.oauth.entity.SsoUser;
import cn.com.mfish.oauth.req.ReqSsoUser;
import cn.com.mfish.common.oauth.api.vo.TenantVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: mfish
 * @date: 2020/2/13 17:13
 */
public interface SsoUserMapper extends BaseMapper<SsoUser> {

    /**
     * 根据账号查询用户信息 账号为account,phone,email,userId任意一种
     *
     * @param account
     * @return
     */
    SsoUser getUserByAccount(@Param("account") String account);

    /**
     * 通过ID获取用户
     *
     * @param userId
     * @return
     */
    SsoUser getUserById(@Param("userId") String userId, @Param("tenantId") String tenantId);

    /**
     * 通过用户ID获取用户角色
     *
     * @param userId
     * @return
     */
    List<UserRole> getUserRoles(@Param("userId") String userId, @Param("tenantId") String tenantId);

    /**
     * 通过用户ID获取按钮权限
     *
     * @param userId
     * @param tenantId
     * @return
     */
    String getUserPermissions(@Param("userId") String userId, @Param("tenantId") String tenantId);


    List<TenantVo> getUserTenants(String userId);

    /**
     * 根据微信openId获取用户id
     *
     * @param openid
     * @return
     */
    @Select("select id from sso_user where openid=#{openId}")
    String getUserIdByOpenId(String openid);

    /**
     * 判断帐号是否存在（帐号可以是邮箱、用户名、手机号）
     *
     * @param account
     * @param userId  排除自己
     * @return
     */
    Integer isAccountExist(@Param("account") String account, @Param("userId") String userId);

    List<UserInfo> getUserList(ReqSsoUser reqSsoUser);

    int insertUserRole(@Param("userId") String userId, @Param("roles") List<String> roles);

    /**
     * 通过角色编码设置用户角色
     *
     * @param userId 用户ID
     * @param roles  角色编码列表
     * @return
     */
    int insertUserRoleByRoleCode(@Param("userId") String userId, @Param("roles") List<String> roles);

    @Delete("delete from sso_user_role where user_id = #{userId}")
    int deleteUserRole(String userId);

    int insertUserOrg(@Param("userId") String userId, @Param("orgList") List<String> orgList);

    int deleteUserOrg(@Param("userId") String userId, @Param("orgList") List<String> orgList);

    int isExistUserOrg(@Param("userId") String userId, @Param("orgId") String orgId);
}

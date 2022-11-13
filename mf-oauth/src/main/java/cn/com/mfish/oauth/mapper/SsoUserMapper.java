package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.SsoUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author qiufeng
 * @date 2020/2/13 17:13
 */
public interface SsoUserMapper {
    /**
     * 插入用户信息
     *
     * @param userInfo
     * @return
     */
    int insert(SsoUser userInfo);

    /**
     * 更新用户信息
     *
     * @param ssoUser
     * @return
     */
    int update(SsoUser ssoUser);

    /**
     * 根据账号查询用户信息 账号为account,phone,email,userId任意一种
     *
     * @param account
     * @return
     */
    SsoUser getUserByAccount(@Param("account") String account);

    /**
     * 根据用户ID查询用户
     *
     * @param id
     * @return
     */
    @Select("select * from sso_user where id = #{id}")
    SsoUser getUserById(String id);

    /**
     * 根据微信openId获取用户id
     *
     * @param openid
     * @return
     */
    @Select("select id from sso_user where openid=#{openId}")
    String getUserIdByOpenId(String openid);

    /**
     * 判断帐号是否存在该客户端权限
     *
     * @param userId
     * @param clientId
     * @return
     */
    @Select("select count(0) from sso_client_user where client_id=#{clientId} and user_id=#{userId}")
    Integer getUserClientExist(@Param("userId") String userId, @Param("clientId") String clientId);
}

package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.SsoUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author qiufeng
 * @date 2020/2/13 17:13
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
    Integer isUserClientExist(@Param("userId") String userId, @Param("clientId") String clientId);

    /**
     * 判断帐号是否存在（帐号可以是邮箱、用户名、手机号）
     * @param account
     * @return
     */
    Integer isAccountExist(@Param("account") String account);
}

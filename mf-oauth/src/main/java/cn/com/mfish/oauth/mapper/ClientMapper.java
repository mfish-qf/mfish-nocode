package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.OAuthClient;
import org.apache.ibatis.annotations.Select;

/**
 * @author: mfish
 * @date: 2020/2/16 16:05
 */
public interface ClientMapper {
    @Select("select * from sso_client_details where client_id = #{clientId}")
    OAuthClient getClientById(String clientId);
}

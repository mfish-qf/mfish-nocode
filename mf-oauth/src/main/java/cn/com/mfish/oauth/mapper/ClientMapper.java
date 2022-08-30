package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.model.OAuthClient;
import org.apache.ibatis.annotations.Select;

/**
 * @author qiufeng
 * @date 2020/2/16 16:05
 */
public interface ClientMapper {
    @Select("select * from sso_client_details where client_id = #{clientId}")
    OAuthClient getClientById(String clientId);
}

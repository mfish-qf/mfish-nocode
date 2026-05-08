package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.OAuthClient;
import org.apache.ibatis.annotations.Select;

/**
 * @description: OAuth客户端数据访问接口，提供客户端信息的数据库查询
 * @author: mfish
 * @date: 2020/2/16 16:05
 */
public interface ClientMapper {
    /**
     * 根据客户端ID查询客户端信息
     *
     * @param clientId 客户端ID
     * @return 客户端信息
     */
    @Select("select * from sso_client_details where client_id = #{clientId} and del_flag = 0")
    OAuthClient getClientById(String clientId);
}

package cn.com.mfish.oauth.service;

import cn.com.mfish.oauth.entity.OAuthClient;

/**
 * @author: mfish
 * @date: 2020/2/16 16:10
 */
public interface ClientService {
    /**
     * 根据客户端ID获取客户端信息
     *
     * @param clientId 客户端id
     * @return 返回客户端信息
     */
    OAuthClient getClientById(String clientId);
}

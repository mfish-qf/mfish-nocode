package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SsoClientDetails;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 客户端信息
 * @author: mfish
 * @date: 2023-05-12
 * @version: V1.3.1
 */
public interface SsoClientDetailsService extends IService<SsoClientDetails> {

    Result<SsoClientDetails> updateClient(SsoClientDetails ssoClientDetails);

    void removeClientCache(String clientId);
}

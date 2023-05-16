package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.oauth.cache.temp.ClientTempCache;
import cn.com.mfish.oauth.entity.SsoClientDetails;
import cn.com.mfish.oauth.mapper.SsoClientDetailsMapper;
import cn.com.mfish.oauth.service.SsoClientDetailsService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

/**
 * @description: 客户端信息
 * @author: mfish
 * @date: 2023-05-12
 * @version: V1.0.0
 */
@Service
public class SsoClientDetailsServiceImpl extends ServiceImpl<SsoClientDetailsMapper, SsoClientDetails> implements SsoClientDetailsService {
    @Resource
    ClientTempCache clientTempCache;

    @Override
    public void removeClientCache(String clientId) {
        clientTempCache.removeOneCache(clientId);
    }
}

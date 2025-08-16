package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.cache.temp.ClientTempCache;
import cn.com.mfish.oauth.entity.SsoClientDetails;
import cn.com.mfish.oauth.mapper.SsoClientDetailsMapper;
import cn.com.mfish.oauth.service.SsoClientDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 客户端信息
 * @author: mfish
 * @date: 2023-05-12
 * @version: V2.1.0
 */
@Service
public class SsoClientDetailsServiceImpl extends ServiceImpl<SsoClientDetailsMapper, SsoClientDetails> implements SsoClientDetailsService {
    @Resource
    ClientTempCache clientTempCache;

    @Override
    @Transactional
    public Result<SsoClientDetails> updateClient(SsoClientDetails ssoClientDetails) {
        if (baseMapper.updateById(ssoClientDetails) > 0) {
            if (StringUtils.isEmpty(ssoClientDetails.getClientId())) {
                ssoClientDetails = baseMapper.selectById(ssoClientDetails.getId());
            }
            removeClientCache(ssoClientDetails.getClientId());
            return Result.ok(ssoClientDetails, "客户端信息-编辑成功!");
        }
        return Result.fail(ssoClientDetails, "错误:客户端信息-编辑失败!");
    }

    @Override
    public void removeClientCache(String clientId) {
        clientTempCache.removeOneCache(clientId);
    }
}

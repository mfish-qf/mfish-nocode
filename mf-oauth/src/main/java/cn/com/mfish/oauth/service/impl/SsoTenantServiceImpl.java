package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.oauth.entity.SsoTenant;
import cn.com.mfish.oauth.mapper.SsoTenantMapper;
import cn.com.mfish.oauth.service.SsoTenantService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.0.0
 */
@Service
public class SsoTenantServiceImpl extends ServiceImpl<SsoTenantMapper, SsoTenant> implements SsoTenantService {

}

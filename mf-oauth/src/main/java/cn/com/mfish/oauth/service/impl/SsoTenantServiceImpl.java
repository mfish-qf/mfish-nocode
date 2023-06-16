package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.oauth.entity.SsoTenant;
import cn.com.mfish.oauth.mapper.SsoTenantMapper;
import cn.com.mfish.oauth.req.ReqSsoTenant;
import cn.com.mfish.oauth.service.SsoTenantService;
import cn.com.mfish.oauth.vo.TenantVo;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.0.0
 */
@Service
public class SsoTenantServiceImpl extends ServiceImpl<SsoTenantMapper, SsoTenant> implements SsoTenantService {

    @Override
    public List<TenantVo> queryList(ReqSsoTenant reqSsoTenant, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return baseMapper.queryList(reqSsoTenant);
    }
}

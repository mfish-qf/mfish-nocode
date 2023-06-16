package cn.com.mfish.oauth.service;

import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.oauth.entity.SsoTenant;
import cn.com.mfish.oauth.req.ReqSsoTenant;
import cn.com.mfish.oauth.vo.TenantVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.0.1
 */
public interface SsoTenantService extends IService<SsoTenant> {
    List<TenantVo> queryList(ReqSsoTenant reqSsoTenant, ReqPage reqPage);
}

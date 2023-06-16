package cn.com.mfish.oauth.mapper;

import cn.com.mfish.oauth.entity.SsoTenant;
import cn.com.mfish.oauth.req.ReqSsoTenant;
import cn.com.mfish.oauth.vo.TenantVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V1.0.1
 */
public interface SsoTenantMapper extends BaseMapper<SsoTenant> {
    List<TenantVo> queryList(@Param("reqSsoTenant") ReqSsoTenant reqSsoTenant);
}

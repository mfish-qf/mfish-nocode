package cn.com.mfish.oauth.service;

import cn.com.mfish.oauth.entity.SsoOrg;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0.0
 */
public interface SsoOrgService extends IService<SsoOrg> {
    boolean insertOrg(SsoOrg ssoOrg);

    List<SsoOrg> queryOrg(ReqSsoOrg reqSsoOrg);

    boolean removeOrg(String id);
}

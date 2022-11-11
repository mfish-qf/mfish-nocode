package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.oauth.entity.SsoOrg;
import cn.com.mfish.oauth.mapper.SsoOrgMapper;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import cn.com.mfish.oauth.service.SsoOrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Service
public class SsoOrgServiceImpl extends ServiceImpl<SsoOrgMapper, SsoOrg> implements SsoOrgService {

    @Override
    public boolean insertOrg(SsoOrg ssoOrg) {
        if (StringUtils.isEmpty(ssoOrg.getParentId())) {
            ssoOrg.setParentId("");
        }
        return baseMapper.insertOrg(ssoOrg) == 1;
    }

    @Override
    public List<SsoOrg> queryOrg(ReqSsoOrg reqSsoOrg) {
        Integer level = baseMapper.queryMaxOrgLevel(reqSsoOrg);
        List<Integer> list = new ArrayList<>();
        if (level != null) {
            for (int i = 1; i < level; i++) {
                list.add(i);
            }
        }
        return baseMapper.queryOrg(reqSsoOrg, list);
    }
}

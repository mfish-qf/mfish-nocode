package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.oauth.entity.SsoOrg;
import cn.com.mfish.oauth.mapper.SsoOrgMapper;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import cn.com.mfish.oauth.service.SsoOrgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.0
 */
@Service
@Slf4j
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

    @Override
    public boolean removeOrg(String id) {
        if (baseMapper.updateById(new SsoOrg().setId(id).setDelFlag(1)) == 1) {
            log.info(MessageFormat.format("删除组织成功,组织ID:{0}", id));
            return true;
        }
        log.error(MessageFormat.format("删除组织失败,组织ID:{0}", id));
        return false;
    }
}

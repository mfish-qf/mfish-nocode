package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.SsoOrg;
import cn.com.mfish.oauth.mapper.SsoOrgMapper;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import cn.com.mfish.oauth.service.SsoOrgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public Result<SsoOrg> insertOrg(SsoOrg ssoOrg) {
        Result<SsoOrg> result = verifyOrg(ssoOrg);
        if (!result.isSuccess()) {
            return result;
        }
        if (baseMapper.insertOrg(ssoOrg) == 1) {
            return Result.ok(ssoOrg, "组织结构表-添加成功!");
        }
        return Result.fail("错误:添加失败!");
    }

    @Override
    public Result<SsoOrg> updateOrg(SsoOrg ssoOrg) {
        Result<SsoOrg> result = verifyOrg(ssoOrg);
        if (!result.isSuccess()) {
            return result;
        }
        SsoOrg oldOrg = baseMapper.selectById(ssoOrg.getId());
        boolean success;
        if ((StringUtils.isEmpty(oldOrg.getParentId()) && StringUtils.isEmpty(ssoOrg.getParentId())) ||
                (!StringUtils.isEmpty(oldOrg.getParentId()) && oldOrg.getParentId().equals(ssoOrg.getParentId()))) {
            success = baseMapper.updateById(ssoOrg) > 0;
        } else {
            List<SsoOrg> list = baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>()
                    .likeRight(SsoOrg::getOrgCode, oldOrg.getOrgCode()).orderByAsc(SsoOrg::getOrgCode));
            if (list == null || list.isEmpty()) {
                throw new MyRuntimeException("错误:未查询到组织");
            }
            list.set(0, ssoOrg);
            //父节点发生变化，重新生成序列
            baseMapper.deleteBatchIds(list.stream().map((org) -> org.getId()).collect(Collectors.toList()));
            for (SsoOrg org : list) {
                if (baseMapper.insertOrg(org) <= 0) {
                    throw new MyRuntimeException("错误:更新组织失败");
                }
            }
            success = true;
        }
        if (success) {
            return Result.ok(ssoOrg, "组织编辑成功!");
        }
        throw new MyRuntimeException("错误:更新组织失败");
    }

    private Result<SsoOrg> verifyOrg(SsoOrg ssoOrg) {
        if (StringUtils.isEmpty(ssoOrg.getParentId())) {
            ssoOrg.setParentId("");
        }
        if (!StringUtils.isEmpty(ssoOrg.getId()) && ssoOrg.getId().equals(ssoOrg.getParentId())) {
            return Result.fail("错误:父节点不允许设置自己");
        }
        return Result.ok("组织校验成功");
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

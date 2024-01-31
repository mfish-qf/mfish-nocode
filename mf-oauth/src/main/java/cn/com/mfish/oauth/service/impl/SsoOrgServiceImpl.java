package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.oauth.cache.common.ClearCache;
import cn.com.mfish.oauth.mapper.SsoOrgMapper;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import cn.com.mfish.oauth.service.SsoOrgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.2.0
 */
@Service
@Slf4j
public class SsoOrgServiceImpl extends ServiceImpl<SsoOrgMapper, SsoOrg> implements SsoOrgService {
    @Resource
    ClearCache clearCache;

    @Override
    @Transactional
    public Result<SsoOrg> insertOrg(SsoOrg ssoOrg) {
        verifyOrg(ssoOrg);
        if(StringUtils.isEmpty(ssoOrg.getTenantId())){
            ssoOrg.setTenantId(null);
        }
        if (baseMapper.insertOrg(ssoOrg) == 1) {
            insertOrgRole(ssoOrg.getId(), ssoOrg.getRoleIds());
            return Result.ok(ssoOrg, "组织结构表-添加成功!");
        }
        return Result.fail("错误:添加失败!");
    }

    @Override
    public int insertOrgRole(String orgId, List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return 0;
        }
        int count = baseMapper.insertOrgRole(orgId, roles);
        if (count > 0) {
            return count;
        }
        throw new MyRuntimeException("错误:插入组织角色失败");
    }

    @Override
    public int deleteOrgRole(String orgId) {
        return baseMapper.deleteOrgRole(orgId);
    }

    @Override
    @Transactional
    public Result<SsoOrg> updateOrg(SsoOrg ssoOrg) {
        verifyOrg(ssoOrg);
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
            baseMapper.deleteBatchIds(list.stream().map(SsoOrg::getId).collect(Collectors.toList()));
            for (SsoOrg org : list) {
                if (baseMapper.insertOrg(org) <= 0) {
                    throw new MyRuntimeException("错误:更新组织失败");
                }
            }
            success = true;
        }
        if (success) {
            if (ssoOrg.getRoleIds() != null) {
                log.info(MessageFormat.format("删除组织角色数量:{0}条", baseMapper.deleteOrgRole(ssoOrg.getId())));
                insertOrgRole(ssoOrg.getId(), ssoOrg.getRoleIds());
            }
            CompletableFuture.runAsync(() -> removeUserAuthCache(ssoOrg.getId()));
            return Result.ok(ssoOrg, "组织编辑成功!");
        }
        throw new MyRuntimeException("错误:更新组织失败");
    }

    private void removeUserAuthCache(String orgId) {
        List<String> userIds = baseMapper.getOrgUserId(orgId);
        clearCache.removeUserAuthCache(userIds);
    }

    /**
     * 校验组织参数
     *
     * @param ssoOrg 组织对象
     */
    private void verifyOrg(SsoOrg ssoOrg) {
        if (StringUtils.isEmpty(ssoOrg.getParentId())) {
            ssoOrg.setParentId("");
        }
        if (!StringUtils.isEmpty(ssoOrg.getId()) && ssoOrg.getId().equals(ssoOrg.getParentId())) {
            throw new MyRuntimeException("错误:父节点不允许设置自己");
        }
        if (!StringUtils.isEmpty(ssoOrg.getOrgFixCode()) &&
                baseMapper.orgFixCodeExist(ssoOrg.getId(), ssoOrg.getOrgFixCode()) > 0) {
            throw new MyRuntimeException("错误:组织固定编码已存在");
        }
        if (!StringUtils.isEmpty(ssoOrg.getPhone()) && !StringUtils.isPhone(ssoOrg.getPhone())) {
            throw new MyRuntimeException("错误:手机号不正确");
        }
        if (AuthInfoUtils.isContainSuperAdmin(ssoOrg.getRoleIds())) {
            throw new MyRuntimeException("错误:不允许设置为超户!");
        }
        Result.ok("组织校验成功");
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
    public Result<Boolean> removeOrg(String id) {
        if (StringUtils.isEmpty(id)) {
            return Result.fail(false, "错误:组织ID不允许为空");
        }
        int count = baseMapper.queryUserCount(id);
        if (count > 0) {
            return Result.fail(false, "错误:组织下存在用户，请先移除用户");
        }
        count = baseMapper.queryChildCount(id);
        if (count > 0) {
            return Result.fail(false, "错误:组织下存子组织，请先删除子组织");
        }
        if (baseMapper.updateById(new SsoOrg().setId(id).setDelFlag(1)) == 1) {
            log.info(MessageFormat.format("删除组织成功,组织ID:{0}", id));
            return Result.ok("删除组织成功");
        }
        log.error(MessageFormat.format("删除组织失败,组织ID:{0}", id));
        return Result.fail("错误:删除组织失败");
    }

    @Override
    public List<SsoOrg> queryOrgByCode(String fixCode, TreeDirection direction) {
        if (StringUtils.isEmpty(fixCode)) {
            throw new MyRuntimeException("错误:固定编码不允许为空");
        }
        SsoOrg org = baseMapper.selectOne(new LambdaQueryWrapper<SsoOrg>().eq(SsoOrg::getOrgFixCode, fixCode));
        return queryOrg(org, direction);
    }

    @Override
    public List<SsoOrg> queryOrgById(String id, TreeDirection direction) {
        if (StringUtils.isEmpty(id)) {
            throw new MyRuntimeException("错误:组织id不允许为空");
        }
        SsoOrg org = baseMapper.selectOne(new LambdaQueryWrapper<SsoOrg>().eq(SsoOrg::getId, id));
        return queryOrg(org, direction);
    }

    @Override
    public List<UserRole> getOrgRoles(String... orgIds) {
        return baseMapper.getOrgRoles(orgIds);
    }

    @Override
    public boolean isTenantOrg(String orgId, String tenantId) {
        if (StringUtils.isEmpty(orgId)) {
            throw new MyRuntimeException("错误:组织ID不允许为空");
        }
        return baseMapper.isTenantOrg(orgId, tenantId) > 0;
    }

    List<SsoOrg> queryOrg(SsoOrg org, TreeDirection direction) {
        if (org == null) {
            return new ArrayList<>();
        }
        List<SsoOrg> orgList;
        switch (direction) {
            case 向下:
                orgList = downOrg(org.getOrgCode());
                List<SsoOrg> orgTree = new ArrayList<>();
                TreeUtils.buildTree(org.getParentId(), orgList, orgTree, SsoOrg.class);
                return orgTree;
            case 向上:
                orgList = upOrg(org.getOrgCode(), org.getOrgLevel());
                break;
            default:
                orgList = downOrg(org.getOrgCode());
                //向下已经查了自己，向上查询不查
                List<SsoOrg> ups = upOrg(org.getOrgCode(), org.getOrgLevel() - 1);
                orgList.addAll(ups);
                break;
        }
        List<SsoOrg> orgTree = new ArrayList<>();
        TreeUtils.buildTree("", orgList, orgTree, SsoOrg.class);
        return orgTree;
    }

    /**
     * 向下查询组织
     *
     * @param code 固定编码
     * @return
     */
    private List<SsoOrg> downOrg(String code) {
        return baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().likeRight(SsoOrg::getOrgCode, code).eq(SsoOrg::getDelFlag, 0).orderByAsc(SsoOrg::getOrgSort));
    }

    /**
     * 向上查询组织
     *
     * @param code  固定编码
     * @param level 等级
     * @return
     */
    private List<SsoOrg> upOrg(String code, int level) {
        List<String> orgList = new ArrayList<>();
        if (level < 1) {
            return new ArrayList<>();
        }
        for (int i = 1; i <= level; i++) {
            orgList.add(code.substring(0, i * 5));
        }
        return baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getOrgCode, orgList));
    }
}

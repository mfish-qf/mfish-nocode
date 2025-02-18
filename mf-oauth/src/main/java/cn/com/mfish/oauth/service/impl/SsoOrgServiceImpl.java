package cn.com.mfish.oauth.service.impl;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import cn.com.mfish.common.oauth.req.ReqOrgUser;
import cn.com.mfish.common.oauth.req.ReqSsoOrg;
import cn.com.mfish.common.oauth.service.SsoOrgService;
import cn.com.mfish.oauth.cache.common.ClearCache;
import cn.com.mfish.oauth.mapper.SsoOrgMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.3.2
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
        if (StringUtils.isEmpty(ssoOrg.getTenantId())) {
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
            baseMapper.deleteByIds(list.stream().map(SsoOrg::getId).collect(Collectors.toList()));
            for (SsoOrg org : list) {
                if (baseMapper.insertOrg(org) <= 0) {
                    throw new MyRuntimeException("错误:更新组织失败");
                }
            }
            success = true;
        }
        if (success) {
            if (ssoOrg.getRoleIds() != null) {
                log.info("删除组织角色数量:{}条", baseMapper.deleteOrgRole(ssoOrg.getId()));
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
        List<Integer> list = buildParentLevel(reqSsoOrg);
        return baseMapper.queryOrg(reqSsoOrg, list);
    }

    @Override
    public Result<PageResult<SsoOrg>> queryOrg(ReqSsoOrg reqSsoOrg, ReqPage reqPage) {
        List<Integer> list = buildParentLevel(reqSsoOrg);
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        PageResult<SsoOrg> result = new PageResult<>(baseMapper.queryOneLevelOrg(reqSsoOrg, list));
        if (!result.getList().isEmpty()) {
            List<SsoOrg> orgList = baseMapper.queryChildOrg(reqSsoOrg, list, result.getList().stream().map(SsoOrg::getOrgCode).collect(Collectors.toList()));
            List<SsoOrg> orgTree = new ArrayList<>();
            TreeUtils.buildTree("", orgList, orgTree, SsoOrg.class);
            return Result.ok(new PageResult<>(orgTree, result.getPageNum(), result.getPageSize(), result.getTotal()), "组织树-查询成功!");
        }
        return Result.ok(result, "组织树-查询成功!");
    }

    private List<Integer> buildParentLevel(ReqSsoOrg reqSsoOrg) {
        //查询满足条件的父级节点等级
        Integer level = baseMapper.queryMaxOrgLevel(reqSsoOrg);
        List<Integer> list = new ArrayList<>();
        if (level != null) {
            for (int i = 1; i < level; i++) {
                list.add(i);
            }
        }
        return list;
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
    public Result<List<String>> queryOrgIdsById(String tenantId, List<String> ids, TreeDirection direction) {
        if (ids == null || ids.isEmpty()) {
            throw new MyRuntimeException("错误:组织id不允许为空");
        }
        List<SsoOrg> list = baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getId, ids));
        return getOrgIdsByOrg(tenantId, list, direction);
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

    @Override
    public Result<List<SsoOrg>> queryByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            return Result.fail(null, "错误:id不允许为空");
        }
        String[] idList = ids.split(",");
        return Result.ok(baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getId, Arrays.asList(idList))), "组织结构表-查询成功!");
    }

    @Override
    public PageResult<UserInfo> queryUserByCode(String code, ReqOrgUser reqOrgUser, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return new PageResult<>(baseMapper.queryUserByCode(code, reqOrgUser));
    }

    @Override
    public Result<List<String>> getOrgIdsByFixCode(String tenantId, List<String> orgCodes, TreeDirection direction) {
        if (orgCodes == null || orgCodes.isEmpty()) {
            return Result.fail("错误：组织编码不允许为空");
        }
        List<SsoOrg> list = baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getOrgFixCode, orgCodes));
        return getOrgIdsByOrg(tenantId, list, direction);
    }

    @Override
    public Result<List<String>> getOrgIdsById(String tenantId, List<String> orgIds, TreeDirection direction) {
        if (orgIds == null || orgIds.isEmpty()) {
            return Result.fail("错误：组织id不允许为空");
        }
        List<SsoOrg> list = baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getId, orgIds));
        if (list == null || list.isEmpty()) {
            return Result.fail(null, "错误：未获取到组织信息");
        }
        return getOrgIdsByOrg(tenantId, list, direction);
    }

    private Result<List<String>> getOrgIdsByOrg(String tenantId, List<SsoOrg> list, TreeDirection direction) {
        //todo tenantId暂时未用到，后续完善
        switch (direction) {
            case 向下:
                return Result.ok(baseMapper.getOrgDownIdsByCode(list.stream().map(SsoOrg::getOrgCode).toList()), "查询下级组织ID成功");
            case 向上:
                List<String> listCode = new ArrayList<>();
                for (SsoOrg org : list) {
                    listCode.addAll(getUpOrgCodes(org.getOrgCode(), org.getOrgLevel()));
                }
                return Result.ok(baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getOrgCode, listCode)).stream().map(SsoOrg::getId).toList(), "查询上级组织ID成功");
            default:
                List<String> ids = baseMapper.getOrgDownIdsByCode(list.stream().map(SsoOrg::getOrgCode).toList());
                List<String> codes = new ArrayList<>();
                for (SsoOrg org : list) {
                    codes.addAll(getUpOrgCodes(org.getOrgCode(), org.getOrgLevel() - 1));
                }
                if (!codes.isEmpty()) {
                    ids.addAll(baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getOrgCode, codes)).stream().map(SsoOrg::getId).toList());
                }
                return Result.ok(ids, "查询所有组织ID成功");
        }
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
     * @return 返回组织列表
     */
    private List<SsoOrg> downOrg(String code) {
        return baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().likeRight(SsoOrg::getOrgCode, code).eq(SsoOrg::getDelFlag, 0).orderByAsc(SsoOrg::getOrgSort));
    }

    /**
     * 向上查询组织
     *
     * @param code  固定编码
     * @param level 等级
     * @return 返回组织列表
     */
    private List<SsoOrg> upOrg(String code, int level) {
        List<String> orgList = getUpOrgCodes(code, level);
        if (orgList.isEmpty()) {
            return new ArrayList<>();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getOrgCode, orgList));
    }

    /**
     * 获取所有上级编码
     *
     * @param code  固定编码
     * @param level 等级
     * @return 返回上级编码列表
     */
    private List<String> getUpOrgCodes(String code, int level) {
        List<String> orgList = new ArrayList<>();
        if (level < 1) {
            return new ArrayList<>();
        }
        for (int i = 1; i <= level; i++) {
            orgList.add(code.substring(0, i * 5));
        }
        return orgList;
    }

}

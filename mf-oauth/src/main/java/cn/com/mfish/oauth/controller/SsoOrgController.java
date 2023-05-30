package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import cn.com.mfish.oauth.service.SsoOrgService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "组织结构表")
@RestController
@RequestMapping("/org")
public class SsoOrgController {
    @Resource
    private SsoOrgService ssoOrgService;

    /**
     * 分页列表查询
     *
     * @param reqSsoOrg
     * @param reqPage
     * @return
     */
    @ApiOperation(value = "组织结构表-分页列表查询", notes = "组织结构表-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:org:query")
    public Result<PageResult<SsoOrg>> queryPageList(ReqSsoOrg reqSsoOrg, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return Result.ok(new PageResult<>(ssoOrgService.list()), "组织结构表-查询成功!");
    }

    @ApiOperation(value = "获取组织结构")
    @GetMapping("/tree")
    public Result<List<SsoOrg>> queryOrgTree(ReqSsoOrg reqSsoOrg) {
        List<SsoOrg> list = ssoOrgService.queryOrg(reqSsoOrg);
        List<SsoOrg> orgList = new ArrayList<>();
        TreeUtils.buildTree("", list, orgList, SsoOrg.class);
        return Result.ok(orgList, "组织结构表-查询成功!");
    }

    /**
     * 添加
     *
     * @param ssoOrg
     * @return
     */
    @Log(title = "组织结构表-添加", operateType = OperateType.INSERT)
    @ApiOperation(value = "组织结构表-添加", notes = "组织结构表-添加")
    @PostMapping
    @RequiresPermissions("sys:org:insert")
    public Result<SsoOrg> add(@RequestBody SsoOrg ssoOrg) {
        return ssoOrgService.insertOrg(ssoOrg);
    }

    /**
     * 编辑
     *
     * @param ssoOrg
     * @return
     */
    @Log(title = "组织结构表-编辑", operateType = OperateType.UPDATE)
    @ApiOperation(value = "组织结构表-编辑", notes = "组织结构表-编辑")
    @PutMapping
    @RequiresPermissions("sys:org:update")
    public Result<SsoOrg> edit(@RequestBody SsoOrg ssoOrg) {
        return ssoOrgService.updateOrg(ssoOrg);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Log(title = "组织结构表-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "组织结构表-通过id删除", notes = "组织结构表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:org:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        return ssoOrgService.removeOrg(id);
    }

    /**
     * 通过id查询
     *
     * @param ids 多个ID逗号分隔
     * @return
     */
    @ApiOperation(value = "组织结构表-通过id查询", notes = "组织结构表-通过id查询")
    @GetMapping("/{ids}")
    @RequiresPermissions("sys:org:query")
    public Result<List<SsoOrg>> queryById(@ApiParam(name = "ids", value = "唯一性ID") @PathVariable("ids") String ids) {
        if (StringUtils.isEmpty(ids)) {
            return Result.fail(null, "错误:id不允许为空");
        }
        String[] idList = ids.split(",");
        return Result.ok(ssoOrgService.list(new LambdaQueryWrapper<SsoOrg>().in(SsoOrg::getId, idList)), "组织结构表-查询成功!");
    }

    @ApiOperation(value = "组织结构表-通过固定编码查询", notes = "组织结构表-通过固定编码查询")
    @GetMapping("/fixCode")
    @RequiresPermissions("sys:org:query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "固定编码", paramType = "query", required = true),
            @ApiImplicitParam(name = "direction", value = "方向 all 返回所有父子节点 up返回父节点 down返回子节点", paramType = "query", required = true),
    })
    public Result<List<SsoOrg>> queryByFixCode(@RequestParam String code, @RequestParam String direction) {
        List<SsoOrg> list = ssoOrgService.queryOrgByCode(code, TreeDirection.getDirection(direction));
        return Result.ok(list, "组织结构表-查询成功!");
    }
}

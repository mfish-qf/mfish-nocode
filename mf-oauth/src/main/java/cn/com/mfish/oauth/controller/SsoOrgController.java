package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.TreeUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.web.common.PageResult;
import cn.com.mfish.common.web.common.ReqPage;
import cn.com.mfish.oauth.entity.SsoOrg;
import cn.com.mfish.oauth.req.ReqSsoOrg;
import cn.com.mfish.oauth.service.SsoOrgService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
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
    public Result<SsoOrg> add(@RequestBody SsoOrg ssoOrg) {
        if (ssoOrgService.insertOrg(ssoOrg)) {
            return Result.ok(ssoOrg, "组织结构表-添加成功!");
        }
        return Result.fail("错误:添加失败!");
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
    public Result<?> edit(@RequestBody SsoOrg ssoOrg) {
        if (ssoOrgService.updateById(ssoOrg)) {
            return Result.ok("组织结构表-编辑成功!");
        }
        return Result.fail("错误:组织结构表-编辑失败!");
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
    public Result<?> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (ssoOrgService.removeOrg(id)) {
            return Result.ok("组织结构表-删除成功!");
        }
        return Result.fail("错误:组织结构表-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @Log(title = "组织结构表-批量删除", operateType = OperateType.DELETE)
    @ApiOperation(value = "组织结构表-批量删除", notes = "组织结构表-批量删除")
    @DeleteMapping("/batch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.ssoOrgService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok("组织结构表-批量删除成功!");
        }
        return Result.fail("错误:组织结构表-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "组织结构表-通过id查询", notes = "组织结构表-通过id查询")
    @GetMapping("/{id}")
    public Result<SsoOrg> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        SsoOrg ssoOrg = ssoOrgService.getById(id);
        return Result.ok(ssoOrg, "组织结构表-查询成功!");
    }
}

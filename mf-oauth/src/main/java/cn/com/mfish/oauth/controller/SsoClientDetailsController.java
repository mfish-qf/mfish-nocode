package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.oauth.entity.SsoClientDetails;
import cn.com.mfish.oauth.req.ReqSsoClientDetails;
import cn.com.mfish.oauth.service.SsoClientDetailsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 客户端信息
 * @author: mfish
 * @date: 2023-05-12
 * @version: V2.2.0
 */
@Slf4j
@Tag(name = "客户端信息")
@RestController
@RequestMapping("/ssoClientDetails")
public class SsoClientDetailsController {
    @Resource
    private SsoClientDetailsService ssoClientDetailsService;

    /**
     * 分页列表查询
     *
     * @param reqSsoClientDetails 客户端信息请求参数
     * @param reqPage             分页参数
     * @return 返回客户端信息-分页列表
     */
    @Operation(summary = "客户端信息-分页列表查询", description = "客户端信息-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:client:query")
    public Result<PageResult<SsoClientDetails>> queryPageList(ReqSsoClientDetails reqSsoClientDetails, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqSsoClientDetails, reqPage)), "客户端信息-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqSsoClientDetails 客户端信息请求参数
     * @param reqPage             分页参数
     * @return 返回客户端信息-分页列表
     */
    private List<SsoClientDetails> queryList(ReqSsoClientDetails reqSsoClientDetails, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<SsoClientDetails> lambdaQueryWrapper = new LambdaQueryWrapper<SsoClientDetails>()
                .eq(SsoClientDetails::getDelFlag, 0)
                .like(!StringUtils.isEmpty(reqSsoClientDetails.getClientName()), SsoClientDetails::getClientName, reqSsoClientDetails.getClientName())
                .eq(!StringUtils.isEmpty(reqSsoClientDetails.getClientId()), SsoClientDetails::getClientId, reqSsoClientDetails.getClientId());
        List<SsoClientDetails> list = ssoClientDetailsService.list(lambdaQueryWrapper);
        for (SsoClientDetails details : list) {
            details.setClientSecret("******************");
        }
        return list;
    }

    /**
     * 添加
     *
     * @param ssoClientDetails 客户端信息对象
     * @return 返回客户端信息-添加结果
     */
    @Log(title = "客户端信息-添加", operateType = OperateType.INSERT)
    @Operation(summary = "客户端信息-添加")
    @PostMapping
    @RequiresPermissions("sys:client:insert")
    public Result<SsoClientDetails> add(@RequestBody SsoClientDetails ssoClientDetails) {
        verifyDetails(ssoClientDetails);
        //内部项目oauth2，资源默认都给权限，跳过授权页。授权页面暂未开发
        //生成客户端ID及密钥
        ssoClientDetails.setClientId(Utils.uuid32()).setClientSecret(Utils.uuid32()).setScope("all").setAutoApprove(1);
        if (ssoClientDetailsService.save(ssoClientDetails)) {
            return Result.ok(ssoClientDetails, "客户端信息-添加成功!");
        }
        return Result.fail(ssoClientDetails, "错误:客户端信息-添加失败!");
    }

    /**
     * 校验客户端信息
     *
     * @param ssoClientDetails 客户端信息
     */
    private void verifyDetails(SsoClientDetails ssoClientDetails) {
        if (StringUtils.isEmpty(ssoClientDetails.getClientName())) {
            throw new MyRuntimeException("错误:客户端名称不允许为空");
        }
        if (StringUtils.isEmpty(ssoClientDetails.getGrantTypes())) {
            throw new MyRuntimeException("错误:认证方式不允许为空");
        }
        if (StringUtils.isEmpty(ssoClientDetails.getRedirectUrl())) {
            throw new MyRuntimeException("错误:回调地址不允许为空");
        }
        if (!StringUtils.isMatch("^(https?://[^,]+?)(,https?://[^,]+?)*$", ssoClientDetails.getRedirectUrl())) {
            throw new MyRuntimeException("错误:回调地址不规范");
        }
    }

    /**
     * 编辑
     *
     * @param ssoClientDetails 客户端信息对象
     * @return 返回客户端信息-编辑结果
     */
    @Log(title = "客户端信息-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "客户端信息-编辑")
    @PutMapping
    @RequiresPermissions("sys:client:update")
    public Result<SsoClientDetails> edit(@RequestBody SsoClientDetails ssoClientDetails) {
        verifyDetails(ssoClientDetails);
        //客户端ID不允许修改
        ssoClientDetails.setClientId(null).setClientSecret(null);
        return ssoClientDetailsService.updateClient(ssoClientDetails);
    }

    @Log(title = "显示密钥", operateType = OperateType.QUERY)
    @Operation(summary = "显示密钥")
    @GetMapping("/secret/{id}")
    @RequiresPermissions("sys:client:query")
    public Result<String> showSecret(@PathVariable String id) {
        SsoClientDetails ssoClientDetails = ssoClientDetailsService.getById(id);
        if (ssoClientDetails != null) {
            return Result.ok(ssoClientDetails.getClientSecret(), "显示密钥成功!");
        }
        return Result.fail("", "错误:获取密钥失败!");
    }

    @Log(title = "重置密钥", operateType = OperateType.UPDATE)
    @Operation(summary = "重置密钥")
    @PutMapping("/secret/{id}")
    @RequiresPermissions("sys:client:update")
    public Result<String> resetSecret(@PathVariable String id) {
        if ("1".equals(id)) {
            throw new MyRuntimeException("错误:系统密钥不允许重置");
        }
        SsoClientDetails ssoClientDetails = new SsoClientDetails();
        String secret = Utils.uuid32();
        ssoClientDetails.setId(id).setClientSecret(secret);
        Result<SsoClientDetails> result = ssoClientDetailsService.updateClient(ssoClientDetails);
        if (result.isSuccess()) {
            return Result.ok(secret, "客户端信息-重置密钥成功!");
        }
        return Result.fail("", "错误:客户端信息-重置密钥失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回客户端信息-删除结果
     */
    @Log(title = "客户端信息-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "客户端信息-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:client:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        SsoClientDetails ssoClientDetails = new SsoClientDetails();
        ssoClientDetails.setDelFlag(1).setId(id);
        Result<SsoClientDetails> result = ssoClientDetailsService.updateClient(ssoClientDetails);
        if (result.isSuccess()) {
            return Result.ok(true, "客户端信息-删除成功!");
        }
        return Result.fail(false, "错误:客户端信息-删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回客户端信息对象
     */
    @Operation(summary = "客户端信息-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:client:query")
    public Result<SsoClientDetails> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        SsoClientDetails ssoClientDetails = ssoClientDetailsService.getById(id);
        return Result.ok(ssoClientDetails, "客户端信息-查询成功!");
    }
}

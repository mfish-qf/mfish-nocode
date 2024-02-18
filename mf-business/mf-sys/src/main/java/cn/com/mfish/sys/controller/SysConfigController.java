package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.sys.entity.SysConfig;
import cn.com.mfish.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @description: 界面配置
 * @author: mfish
 * @date: 2023-03-07
 * @version: V1.2.0
 */
@Slf4j
@Api(tags = "界面配置")
@RestController
@RequestMapping("/sysConfig")
public class SysConfigController {
    @Resource
    private SysConfigService sysConfigService;

    /**
     * 查询系统界面配置
     *
     * @return 返回界面配置对象
     */
    @ApiOperation("界面配置-通过token查询")
    @GetMapping
    public Result<SysConfig> queryConfig() {
        SysConfig sysConfig = sysConfigService.querySysConfig(AuthInfoUtils.getCurrentUserId());
        return Result.ok(sysConfig, "界面配置-查询成功!");
    }

    /**
     * 保存
     *
     * @param sysConfig 界面配置对象
     * @return 返回界面配置-添加结果
     */
    @Log(title = "界面配置-保存", operateType = OperateType.INSERT)
    @ApiOperation("界面配置-保存")
    @PostMapping
    public Result<SysConfig> save(@RequestBody SysConfig sysConfig) {
        return sysConfigService.saveSysConfig(sysConfig);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回界面配置-删除结果
     */
    @Log(title = "界面配置-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation("界面配置-通过id删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (sysConfigService.removeById(id)) {
            return Result.ok(true, "界面配置-删除成功!");
        }
        return Result.fail(false, "错误:界面配置-删除失败!");
    }
}

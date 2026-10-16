package cn.com.mfish.project.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.project.req.ReqUserColumnConfig;
import cn.com.mfish.project.service.PmUserColumnConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户表格列配置(按登录人员保存)
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.2
 */
@Slf4j
@Tag(name = "用户列配置")
@RestController
@RequestMapping("/user-column-config")
public class UserColumnConfigController {

    @Resource
    private PmUserColumnConfigService userColumnConfigService;

    /**
     * 获取当前登录用户的列配置
     */
    @Operation(summary = "用户列配置-查询当前用户配置", description = "按当前登录人员返回其保存的表格列配置")
    @GetMapping
    public Result<String> getConfig() {
        return userColumnConfigService.getCurrentUserConfig();
    }

    /**
     * 保存当前登录用户的列配置(无记录则新增, 有记录则更新)
     */
    @Log(title = "用户列配置-保存", operateType = OperateType.UPDATE)
    @Operation(summary = "用户列配置-保存当前用户配置")
    @PutMapping
    public Result<String> updateConfig(@RequestBody ReqUserColumnConfig config) {
        return userColumnConfigService.updateCurrentUserConfig(config.getConfigJson());
    }
}

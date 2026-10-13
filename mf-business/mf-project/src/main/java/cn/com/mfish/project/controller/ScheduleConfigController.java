package cn.com.mfish.project.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.project.dto.ScheduleConfigDTO;
import cn.com.mfish.project.service.PmScheduleConfigService;
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
 * @description: 排期日历跳过规则配置
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Tag(name = "排期日历配置")
@RestController
@RequestMapping("/project/schedule-config")
public class ScheduleConfigController {

    @Resource
    private PmScheduleConfigService scheduleConfigService;

    /**
     * 获取当前生效的排期跳过规则(包含节假日、封版冻结日列表和调休补班工作日)
     */
    @Operation(summary = "排期配置-查询当前生效规则", description = "包含节假日、封版冻结日列表和调休补班工作日")
    @GetMapping
    @RequiresPermissions("project:scheduleConfig:query")
    public Result<ScheduleConfigDTO> getConfig() {
        return scheduleConfigService.getConfig();
    }

    /**
     * 更新排期规则及明细
     */
    @Log(title = "排期配置-更新规则", operateType = OperateType.UPDATE)
    @Operation(summary = "排期配置-更新规则及明细")
    @PutMapping
    @RequiresPermissions("project:scheduleConfig:update")
    public Result<ScheduleConfigDTO> updateConfig(@RequestBody ScheduleConfigDTO config) {
        return scheduleConfigService.updateConfig(config);
    }
}

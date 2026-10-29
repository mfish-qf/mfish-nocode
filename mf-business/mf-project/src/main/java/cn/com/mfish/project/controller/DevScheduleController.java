package cn.com.mfish.project.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.annotation.DataScopes;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.oauth.common.DataScopeType;
import cn.com.mfish.project.entity.PmDevSchedule;
import cn.com.mfish.project.req.ReqDevScheduleAdjust;
import cn.com.mfish.project.req.ReqDevSchedulePin;
import cn.com.mfish.project.req.ReqDevScheduleQuery;
import cn.com.mfish.project.service.PmDevScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 研发排期明细(需求×人员×岗位) 拖拽调期与钉住
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.1
 */
@Slf4j
@Tag(name = "研发排期明细")
@RestController
@RequestMapping("/devSchedule")
public class DevScheduleController {

    @Resource
    private PmDevScheduleService devScheduleService;

    /**
     * 按版本筛选条件查询排期明细(甘特图/日历按人铺期数据源)
     * POST body 传参: 仅传版本筛选条件(iterationIds/includeUnassigned), 由后端复用需求查询解析需求ID后聚合排期,
     * 前端无需回传大量需求UUID; 版本过滤口径与需求列表完全一致
     */
    @Operation(summary = "排期明细-按版本筛选查询", description = "一行=需求×人员×岗位, 含起止日/工时/顺序/钉住状态")
    @DataScopes({
            @DataScope(table = "pm_requirement", type = DataScopeType.Tenant),
            @DataScope(table = "pm_iteration", type = DataScopeType.Tenant),
            @DataScope(table = "pm_dev_schedule", type = DataScopeType.Tenant)
    })
    @PostMapping
    @RequiresPermissions("project:requirement:query")
    public Result<List<PmDevSchedule>> listBySelection(@Valid @RequestBody ReqDevScheduleQuery req) {
        return devScheduleService.listBySelection(req.getIterationIds(), req.getIncludeUnassigned());
    }

    /**
     * 拖拽调期: 更新任务开始日并按工时重算完成日, 自动钉住
     */
    @Log(title = "排期明细-拖拽调期", operateType = OperateType.UPDATE)
    @Operation(summary = "排期明细-拖拽调期", description = "与本人其他任务重叠时拒绝; 调期后自动钉住")
    @PutMapping("/adjust")
    @RequiresPermissions("project:requirement:update")
    public Result<PmDevSchedule> adjust(@Valid @RequestBody ReqDevScheduleAdjust req) {
        return devScheduleService.adjust(req);
    }

    /**
     * 钉住/取消钉住某条排期
     */
    @Log(title = "排期明细-钉住切换", operateType = OperateType.UPDATE)
    @Operation(summary = "排期明细-钉住/取消钉住", description = "钉住后一键重算保持不动")
    @PutMapping("/pin")
    @RequiresPermissions("project:requirement:update")
    public Result<PmDevSchedule> togglePin(@Valid @RequestBody ReqDevSchedulePin req) {
        return devScheduleService.togglePin(req);
    }
}

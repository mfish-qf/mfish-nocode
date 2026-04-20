package cn.com.mfish.demo.controller;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.demo.entity.DemoLeaveApply;
import cn.com.mfish.common.demo.req.ReqDemoLeaveApply;
import cn.com.mfish.common.demo.service.DemoLeaveApplyService;
import cn.com.mfish.common.log.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @description: 请假申请审批Demo
 * @author: mfish
 * @date: 2026-04-18
 * @version: V2.3.1
 */
@Slf4j
@Tag(name = "请假申请审批Demo")
@RestController
@RequestMapping("/demoLeaveApply")
public class DemoLeaveApplyController {
    @Resource
    private DemoLeaveApplyService demoLeaveApplyService;

    @Operation(summary = "请假申请审批Demo-分页列表查询", description = "请假申请审批Demo-分页列表查询")
    @GetMapping
    public Result<PageResult<DemoLeaveApply>> queryPageList(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage) {
        return demoLeaveApplyService.queryPageList(reqDemoLeaveApply, reqPage);
    }

    @Log(title = "请假申请审批Demo-添加", operateType = OperateType.INSERT)
    @Operation(summary = "请假申请审批Demo-添加")
    @PostMapping
    public Result<DemoLeaveApply> add(@RequestBody DemoLeaveApply demoLeaveApply) {
        return demoLeaveApplyService.add(demoLeaveApply);
    }

    @Log(title = "请假申请审批Demo-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "请假申请审批Demo-编辑")
    @PutMapping
    public Result<DemoLeaveApply> edit(@RequestBody DemoLeaveApply demoLeaveApply) {
        return demoLeaveApplyService.edit(demoLeaveApply);
    }

    @Log(title = "请假申请审批Demo-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "请假申请审批Demo-通过id删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一ID") @PathVariable String id) {
        return demoLeaveApplyService.delete(id);
    }

    @Log(title = "请假申请审批Demo-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "请假申请审批Demo-批量删除")
    @DeleteMapping("/batch/{ids}")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一ID") @PathVariable String ids) {
        return demoLeaveApplyService.deleteBatch(ids);
    }

    @Operation(summary = "请假申请审批Demo-通过id查询")
    @GetMapping("/{id}")
    public Result<DemoLeaveApply> queryById(@Parameter(name = "id", description = "唯一ID") @PathVariable String id) {
        return demoLeaveApplyService.queryById(id);
    }

    @Operation(summary = "导出请假申请审批Demo", description = "导出请假申请审批Demo")
    @GetMapping("/export")
    public void export(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage) throws IOException {
        demoLeaveApplyService.export(reqDemoLeaveApply, reqPage);
    }

    @Log(title = "请假申请审批Demo-提交审批", operateType = OperateType.UPDATE)
    @Operation(summary = "请假申请审批Demo-提交审批")
    @PostMapping("/submit/{id}")
    public Result<DemoLeaveApply> submit(@PathVariable String id) {
        return demoLeaveApplyService.submit(id);
    }

    @Log(title = "请假申请审批Demo-撤回审批", operateType = OperateType.UPDATE)
    @Operation(summary = "请假申请审批Demo-撤回审批")
    @PostMapping("/revoke/{id}")
    public Result<DemoLeaveApply> revoke(@PathVariable String id) {
        return demoLeaveApplyService.revoke(id);
    }

    @PostMapping("/approved/{id}")
    public Result<String> approved(@PathVariable String id, @RequestBody WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, 1, result);
    }

    @PostMapping("/rejected/{id}")
    public Result<String> rejected(@PathVariable String id, @RequestBody WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, 2, result);
    }

    @PostMapping("/canceled/{id}")
    public Result<String> canceled(@PathVariable String id, @RequestBody WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, null, result);
    }
}

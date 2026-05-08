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

    /**
     * 分页列表查询
     *
     * @param reqDemoLeaveApply 请假申请请求参数
     * @param reqPage           分页参数
     * @return 返回请假申请分页列表
     */
    @Operation(summary = "请假申请审批Demo-分页列表查询", description = "请假申请审批Demo-分页列表查询")
    @GetMapping
    public Result<PageResult<DemoLeaveApply>> queryPageList(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage) {
        return demoLeaveApplyService.queryPageList(reqDemoLeaveApply, reqPage);
    }

    /**
     * 添加请假申请
     *
     * @param demoLeaveApply 请假申请对象
     * @return 返回添加结果
     */
    @Log(title = "请假申请审批Demo-添加", operateType = OperateType.INSERT)
    @Operation(summary = "请假申请审批Demo-添加")
    @PostMapping
    public Result<DemoLeaveApply> add(@RequestBody DemoLeaveApply demoLeaveApply) {
        return demoLeaveApplyService.add(demoLeaveApply);
    }

    /**
     * 编辑请假申请
     *
     * @param demoLeaveApply 请假申请对象
     * @return 返回编辑结果
     */
    @Log(title = "请假申请审批Demo-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "请假申请审批Demo-编辑")
    @PutMapping
    public Result<DemoLeaveApply> edit(@RequestBody DemoLeaveApply demoLeaveApply) {
        return demoLeaveApplyService.edit(demoLeaveApply);
    }

    /**
     * 通过id删除请假申请
     *
     * @param id 唯一ID
     * @return 返回删除结果
     */
    @Log(title = "请假申请审批Demo-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "请假申请审批Demo-通过id删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一ID") @PathVariable String id) {
        return demoLeaveApplyService.delete(id);
    }

    /**
     * 批量删除请假申请
     *
     * @param ids 批量ID，多个ID以逗号分隔
     * @return 返回删除结果
     */
    @Log(title = "请假申请审批Demo-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "请假申请审批Demo-批量删除")
    @DeleteMapping("/batch/{ids}")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一ID") @PathVariable String ids) {
        return demoLeaveApplyService.deleteBatch(ids);
    }

    /**
     * 通过id查询请假申请
     *
     * @param id 唯一ID
     * @return 返回请假申请对象
     */
    @Operation(summary = "请假申请审批Demo-通过id查询")
    @GetMapping("/{id}")
    public Result<DemoLeaveApply> queryById(@Parameter(name = "id", description = "唯一ID") @PathVariable String id) {
        return demoLeaveApplyService.queryById(id);
    }

    /**
     * 导出请假申请数据
     *
     * @param reqDemoLeaveApply 请假申请请求参数
     * @param reqPage           分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "导出请假申请审批Demo", description = "导出请假申请审批Demo")
    @GetMapping("/export")
    public void export(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage) throws IOException {
        demoLeaveApplyService.export(reqDemoLeaveApply, reqPage);
    }

    /**
     * 提交请假申请审批，启动工作流流程
     *
     * @param id 请假申请ID
     * @return 返回提交结果
     */
    @Log(title = "请假申请审批Demo-提交审批", operateType = OperateType.UPDATE)
    @Operation(summary = "请假申请审批Demo-提交审批")
    @PostMapping("/submit/{id}")
    public Result<DemoLeaveApply> submit(@PathVariable String id) {
        return demoLeaveApplyService.submit(id);
    }

    /**
     * 撤回请假申请审批，终止工作流流程
     *
     * @param id 请假申请ID
     * @return 返回撤回结果
     */
    @Log(title = "请假申请审批Demo-撤回审批", operateType = OperateType.UPDATE)
    @Operation(summary = "请假申请审批Demo-撤回审批")
    @PostMapping("/revoke/{id}")
    public Result<DemoLeaveApply> revoke(@PathVariable String id) {
        return demoLeaveApplyService.revoke(id);
    }

    /**
     * 审批通过回调接口
     *
     * @param id     请假申请ID
     * @param result 工作流完成结果
     * @return 返回审批结果
     */
    @PostMapping("/approved/{id}")
    public Result<String> approved(@PathVariable String id, @RequestBody WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, 1, result);
    }

    /**
     * 审批驳回回调接口
     *
     * @param id     请假申请ID
     * @param result 工作流完成结果
     * @return 返回审批结果
     */
    @PostMapping("/rejected/{id}")
    public Result<String> rejected(@PathVariable String id, @RequestBody WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, 2, result);
    }

    /**
     * 审批取消回调接口
     *
     * @param id     请假申请ID
     * @param result 工作流完成结果
     * @return 返回审批结果
     */
    @PostMapping("/canceled/{id}")
    public Result<String> canceled(@PathVariable String id, @RequestBody WorkflowCompleteResult result) {
        return demoLeaveApplyService.audit(id, null, result);
    }
}

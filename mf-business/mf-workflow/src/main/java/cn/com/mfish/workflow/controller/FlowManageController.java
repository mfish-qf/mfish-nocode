package cn.com.mfish.workflow.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.workflow.entity.FlowManage;
import cn.com.mfish.workflow.req.ReqFlowManage;
import cn.com.mfish.workflow.service.FlowManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @description: 流程管理
 * @author: mfish
 * @date: 2026-03-30
 * @version: V2.3.1
 */
@Slf4j
@Tag(name = "流程管理")
@RestController
@RequestMapping("/flowManage")
public class FlowManageController {
    @Resource
    private FlowManageService flowManageService;

    /**
     * 分页列表查询
     *
     * @param reqFlowManage 流程管理请求参数
     * @param reqPage       分页参数
     * @return 返回流程管理-分页列表
     */
    @Operation(summary = "流程管理-分页列表查询", description = "流程管理-分页列表查询")
    @GetMapping
    @RequiresPermissions("workflow:flowManage:query")
    public Result<PageResult<FlowManage>> queryPageList(ReqFlowManage reqFlowManage, ReqPage reqPage) {
        return flowManageService.queryPageList(reqFlowManage, reqPage);
    }

    /**
     * 添加
     *
     * @param flowManage 流程管理对象
     * @return 返回流程管理-添加结果
     */
    @Log(title = "流程管理-添加", operateType = OperateType.INSERT)
    @Operation(summary = "流程管理-添加")
    @PostMapping
    @RequiresPermissions("workflow:flowManage:insert")
    public Result<FlowManage> add(@RequestBody FlowManage flowManage) {
        return flowManageService.add(flowManage);
    }

    /**
     * 编辑
     *
     * @param flowManage 流程管理对象
     * @return 返回流程管理-编辑结果
     */
    @Log(title = "流程管理-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "流程管理-编辑")
    @PutMapping
    @RequiresPermissions("workflow:flowManage:update")
    public Result<FlowManage> edit(@RequestBody FlowManage flowManage) {
        return flowManageService.edit(flowManage);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回流程管理-删除结果
     */
    @Log(title = "流程管理-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "流程管理-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("workflow:flowManage:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return flowManageService.delete(id);
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回流程管理对象
     */
    @Operation(summary = "流程管理-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("workflow:flowManage:query")
    public Result<FlowManage> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return flowManageService.queryById(id);
    }

    /**
     * 导出
     *
     * @param reqFlowManage 流程管理请求参数
     * @param reqPage       分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "导出流程管理", description = "导出流程管理")
    @GetMapping("/export")
    @RequiresPermissions("workflow:flowManage:export")
    public void export(ReqFlowManage reqFlowManage, ReqPage reqPage) throws IOException {
        flowManageService.export(reqFlowManage, reqPage);
    }

    /**
     * 发布流程
     *
     * @param id 唯一ID
     * @return 返回发布结果
     */
    @Log(title = "流程管理-发布", operateType = OperateType.UPDATE)
    @Operation(summary = "流程管理-发布")
    @PutMapping("/publish/{id}")
    @RequiresPermissions("workflow:flowManage:update")
    public Result<Boolean> publish(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return flowManageService.publish(id);
    }

    /**
     * 撤回发布
     *
     * @param id 唯一ID
     * @return 返回撤回结果
     */
    @Log(title = "流程管理-撤回发布", operateType = OperateType.UPDATE)
    @Operation(summary = "流程管理-撤回发布")
    @PutMapping("/unpublish/{id}")
    @RequiresPermissions("workflow:flowManage:update")
    public Result<Boolean> unpublish(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return flowManageService.unpublish(id);
    }
}

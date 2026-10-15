package cn.com.mfish.project.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.project.entity.PmIteration;
import cn.com.mfish.project.entity.PmIterationTestNode;
import cn.com.mfish.project.service.PmIterationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 迭代版本管理
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Tag(name = "迭代版本管理")
@RestController
@RequestMapping("/iteration")
public class IterationController {

    @Resource
    private PmIterationService iterationService;

    /**
     * 查询迭代列表(包含各版本关联的多节点转测列表及自动解析计算的跨月标签)
     */
    @Operation(summary = "迭代版本-列表查询", description = "包含各版本关联的多节点转测列表testNodes及自动解析计算的spannedMonths")
    @GetMapping("/list")
    @RequiresPermissions("project:requirement:query")
    public Result<List<PmIteration>> queryList() {
        return iterationService.queryList();
    }

    /**
     * 通过id或版本号查询迭代详情
     */
    @Operation(summary = "迭代版本-通过id或版本号查询")
    @GetMapping("/{id}")
    @RequiresPermissions("project:requirement:query")
    public Result<PmIteration> queryById(@Parameter(name = "id", description = "迭代ID或版本号") @PathVariable String id) {
        return iterationService.queryByIdOrCode(id);
    }

    /**
     * 新增迭代版本(包含子表转测节点)
     */
    @Log(title = "迭代版本-添加", operateType = OperateType.INSERT)
    @Operation(summary = "迭代版本-添加")
    @PostMapping
    @RequiresPermissions("project:iteration:insert")
    public Result<PmIteration> add(@RequestBody PmIteration iteration) {
        return iterationService.addIteration(iteration);
    }

    /**
     * 编辑迭代版本(包含子表转测节点的新增与删除同步)
     */
    @Log(title = "迭代版本-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "迭代版本-编辑")
    @PutMapping
    @RequiresPermissions("project:iteration:update")
    public Result<PmIteration> edit(@RequestBody PmIteration iteration) {
        return iterationService.editIteration(iteration);
    }

    /**
     * 删除迭代版本
     */
    @Log(title = "迭代版本-删除", operateType = OperateType.DELETE)
    @Operation(summary = "迭代版本-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("project:iteration:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "迭代ID") @PathVariable String id) {
        return iterationService.deleteIteration(id);
    }

    /**
     * 为指定迭代添加批次转测节点
     */
    @Log(title = "迭代版本-添加转测节点", operateType = OperateType.INSERT)
    @Operation(summary = "迭代版本-添加转测节点")
    @PostMapping("/test-node")
    @RequiresPermissions("project:iteration:insert")
    public Result<PmIterationTestNode> addTestNode(@RequestParam String iterationId,
                                                   @RequestBody PmIterationTestNode node) {
        return iterationService.addTestNode(iterationId, node);
    }

    /**
     * 移除批次转测节点
     */
    @Log(title = "迭代版本-移除转测节点", operateType = OperateType.DELETE)
    @Operation(summary = "迭代版本-移除转测节点")
    @DeleteMapping("/test-node/{nodeId}")
    @RequiresPermissions("project:iteration:delete")
    public Result<Boolean> deleteTestNode(@Parameter(name = "nodeId", description = "转测节点ID") @PathVariable String nodeId) {
        return iterationService.deleteTestNode(nodeId);
    }

    /**
     * 归档迭代版本(以状态"已发布"作为归档标记，仅更新状态不影响转测节点)
     */
    @Log(title = "迭代版本-归档", operateType = OperateType.UPDATE)
    @Operation(summary = "迭代版本-归档", description = "归档后版本在版本筛选挂到归档版本下，全部版本汇总查询不再包含该版本需求")
    @PutMapping("/archive/{id}")
    @RequiresPermissions("project:iteration:update")
    public Result<Boolean> archive(@Parameter(name = "id", description = "迭代ID") @PathVariable String id) {
        return iterationService.archiveIteration(id);
    }

    /**
     * 取消归档迭代版本(状态回退为"进行中")
     */
    @Log(title = "迭代版本-取消归档", operateType = OperateType.UPDATE)
    @Operation(summary = "迭代版本-取消归档")
    @PutMapping("/unarchive/{id}")
    @RequiresPermissions("project:iteration:update")
    public Result<Boolean> unarchive(@Parameter(name = "id", description = "迭代ID") @PathVariable String id) {
        return iterationService.unarchiveIteration(id);
    }
}

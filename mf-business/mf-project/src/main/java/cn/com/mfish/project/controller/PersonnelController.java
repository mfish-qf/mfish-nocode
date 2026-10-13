package cn.com.mfish.project.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.project.dto.WorkloadStatsDTO;
import cn.com.mfish.project.entity.PmPersonnel;
import cn.com.mfish.project.service.PmPersonnelService;
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
 * @description: 团队人员与工时负载统计
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Tag(name = "人员管理")
@RestController
@RequestMapping("/project/personnel")
public class PersonnelController {

    @Resource
    private PmPersonnelService personnelService;

    /**
     * 查询项目人员库(支持按岗位角色过滤；传入 pageNum/pageSize 时分页查询，否则返回全量列表)
     */
    @Operation(summary = "人员管理-列表查询", description = "支持按岗位角色(role)与姓名/部门关键词(keyword)过滤")
    @GetMapping("/list")
    @RequiresPermissions("project:personnel:query")
    public Result<?> queryList(@Parameter(name = "role", description = "岗位角色编码(frontend/backend/qa/product/owner)")
                               @RequestParam(required = false) String role,
                               @Parameter(name = "keyword", description = "姓名/部门关键词")
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer pageNum,
                               @RequestParam(required = false) Integer pageSize) {
        if (pageNum != null && pageSize != null) {
            return personnelService.queryPageList(role, keyword, pageNum, pageSize);
        }
        return personnelService.queryList(role, keyword);
    }

    /**
     * 工时负载统计：总承接需求数、总工时数(人天)、交付百分比、团队平均工时偏差差值、健康负荷诊断状态
     */
    @Operation(summary = "人员管理-工时负载统计", description = "计算各开发人员的总承接需求数、总工时数(人天)、交付百分比、团队平均工时偏差差值及健康负荷诊断状态(轻载/正常/饱和/过载)")
    @GetMapping("/workload-stats")
    @RequiresPermissions("project:personnel:query")
    public Result<List<WorkloadStatsDTO>> workloadStats() {
        return personnelService.workloadStats();
    }

    /**
     * 添加人员
     */
    @Log(title = "人员管理-添加", operateType = OperateType.INSERT)
    @Operation(summary = "人员管理-添加")
    @PostMapping
    @RequiresPermissions("project:personnel:insert")
    public Result<PmPersonnel> add(@RequestBody PmPersonnel personnel) {
        return personnelService.addPersonnel(personnel);
    }

    /**
     * 编辑人员
     */
    @Log(title = "人员管理-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "人员管理-编辑")
    @PutMapping
    @RequiresPermissions("project:personnel:update")
    public Result<PmPersonnel> edit(@RequestBody PmPersonnel personnel) {
        return personnelService.editPersonnel(personnel);
    }

    /**
     * 通过id删除人员
     */
    @Log(title = "人员管理-删除", operateType = OperateType.DELETE)
    @Operation(summary = "人员管理-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("project:personnel:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "人员ID") @PathVariable String id) {
        return personnelService.deletePersonnel(id);
    }
}

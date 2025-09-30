package cn.com.mfish.sys.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.log.service.SysLogService;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.core.annotation.InnerUser;
import cn.com.mfish.sys.api.entity.SysLog;
import cn.com.mfish.sys.req.ReqSysLog;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * @description: 系统日志
 * @author: mfish
 * @date: 2023-01-08
 * @version: V2.2.0
 */
@Slf4j
@Tag(name = "系统日志")
@RestController
@RequestMapping("/sysLog")
public class SysLogController {
    @Resource
    private SysLogService sysLogService;

    /**
     * 分页列表查询
     *
     * @param reqSysLog 系统日志请求参数
     * @return 返回系统日志
     */
    @Operation(summary = "系统日志-分页列表查询", description = "系统日志-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:log:query")
    public Result<PageResult<SysLog>> queryPageList(ReqSysLog reqSysLog, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<SysLog> queryWrapper = new LambdaQueryWrapper<SysLog>()
                .like(reqSysLog.getTitle() != null, SysLog::getTitle, reqSysLog.getTitle())
                .like(reqSysLog.getMethod() != null, SysLog::getMethod, reqSysLog.getMethod())
                .eq(reqSysLog.getReqType() != null, SysLog::getReqType, reqSysLog.getReqType())
                .like(reqSysLog.getReqUri() != null, SysLog::getReqUri, reqSysLog.getReqUri())
                .eq(reqSysLog.getReqSource() != null, SysLog::getReqSource, reqSysLog.getReqSource())
                .eq(reqSysLog.getOperType() != null, SysLog::getOperType, reqSysLog.getOperType())
                .like(reqSysLog.getOperIp() != null, SysLog::getOperIp, reqSysLog.getOperIp())
                .eq(reqSysLog.getOperStatus() != null, SysLog::getOperStatus, reqSysLog.getOperStatus())
                .like(reqSysLog.getCreateBy() != null, SysLog::getCreateBy, reqSysLog.getCreateBy())
                .ge(reqSysLog.getStartTime() != null, SysLog::getCreateTime, reqSysLog.getStartTime())
                .le(reqSysLog.getEndTime() != null, SysLog::getCreateTime, reqSysLog.getEndTime())
                .orderByDesc(SysLog::getCreateTime);
        return Result.ok(new PageResult<>(sysLogService.list(queryWrapper)), "系统日志-查询成功!");
    }

    /**
     * 添加
     *
     * @param sysLog 日志
     * @return 返回添加结果
     */
    @Operation(summary = "系统日志-添加", description = "系统日志-添加")
    @PostMapping
    @InnerUser
    public Result<SysLog> add(@RequestBody SysLog sysLog) {
        return sysLogService.insertLog(sysLog);
    }

    /**
     * 编辑
     *
     * @param sysLog 日志
     * @return 返回编辑结果
     */
    @Log(title = "系统日志-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "系统日志-编辑", description = "系统日志-编辑")
    @PutMapping
    public Result<SysLog> edit(@RequestBody SysLog sysLog) {
        if (sysLogService.updateById(sysLog)) {
            return Result.ok(sysLog, "系统日志-编辑成功!");
        }
        return Result.fail(sysLog, "错误:系统日志-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id id
     * @return 返回删除结果
     */
    @Log(title = "系统日志-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "系统日志-通过id删除", description = "系统日志-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:log:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable Long id) {
        if (sysLogService.removeById(id)) {
            return Result.ok(true, "系统日志-删除成功!");
        }
        return Result.fail(false, "错误:系统日志-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids id集合
     * @return 返回删除结果
     */
    @Log(title = "系统日志-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "系统日志-批量删除", description = "系统日志-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("sys:log:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        if (this.sysLogService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "系统日志-批量删除成功!");
        }
        return Result.fail(false, "错误:系统日志-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id id
     * @return 返回查询结果
     */
    @Operation(summary = "系统日志-通过id查询", description = "系统日志-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:log:query")
    public Result<SysLog> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        SysLog sysLog = sysLogService.getById(id);
        return Result.ok(sysLog, "系统日志-查询成功!");
    }
}

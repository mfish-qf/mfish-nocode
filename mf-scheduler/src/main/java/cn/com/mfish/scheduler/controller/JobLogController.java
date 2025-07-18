package cn.com.mfish.scheduler.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.scheduler.api.entity.JobLog;
import cn.com.mfish.common.scheduler.config.enums.JobStatus;
import cn.com.mfish.scheduler.req.ReqJobLog;
import cn.com.mfish.scheduler.service.JobLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @description: 任务日志
 * @author: mfish
 * @date: 2023-02-14
 * @version: V2.0.1
 */
@Slf4j
@Tag(name = "任务日志")
@RestController
@RequestMapping("/jobLog")
public class JobLogController {
    @Resource
    private JobLogService jobLogService;

    /**
     * 分页列表查询
     *
     * @param reqJobLog 任务日志请求参数
     * @return 返回任务日志-分页列表
     */
    @Operation(summary = "任务日志-分页列表查询", description = "任务日志-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:jobLog:query")
    public Result<PageResult<JobLog>> queryPageList(ReqJobLog reqJobLog, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<JobLog> queryWrapper = new LambdaQueryWrapper<JobLog>()
                .like(reqJobLog.getJobName() != null, JobLog::getJobName, reqJobLog.getJobName())
                .like(reqJobLog.getJobGroup() != null, JobLog::getJobGroup, reqJobLog.getJobGroup())
                .eq(reqJobLog.getJobType() != null, JobLog::getJobType, reqJobLog.getJobType())
                .like(reqJobLog.getClassName() != null, JobLog::getMethodName, reqJobLog.getClassName())
                .like(reqJobLog.getCron() != null, JobLog::getCron, reqJobLog.getCron())
                .orderByDesc(JobLog::getCreateTime);
        return Result.ok(new PageResult<>(jobLogService.list(queryWrapper)), "任务日志-查询成功!");
    }

    /**
     * 添加
     *
     * @param jobLog 任务日志对象
     * @return 返回任务日志-添加结果
     */
    @Log(title = "任务日志-添加", operateType = OperateType.INSERT)
    @Operation(summary = "任务日志-添加")
    @PostMapping
    public Result<JobLog> add(@RequestBody JobLog jobLog) {
        if (jobLogService.save(jobLog)) {
            return Result.ok(jobLog, "任务日志-添加成功!");
        }
        return Result.fail(jobLog, "错误:任务日志-添加失败!");
    }

    /**
     * 编辑
     *
     * @param jobLog 任务日志对象
     * @return 返回任务日志-编辑结果
     */
    @Log(title = "任务日志-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "任务日志-编辑")
    @PutMapping
    public Result<JobLog> edit(@RequestBody JobLog jobLog) {
        if (jobLogService.updateById(jobLog)) {
            return Result.ok(jobLog, "任务日志-编辑成功!");
        }
        return Result.fail(jobLog, "错误:任务日志-编辑失败!");
    }

    @Log(title = "回调设置执行状态", operateType = OperateType.UPDATE)
    @Operation(summary = "回调设置执行状态", description = "回调设置执行状态")
    @PutMapping("/callBackStatus")
    public Result<Boolean> callBackStatus(@RequestBody JobLog jobLog) {
        if (!jobLog.getStatus().equals(JobStatus.执行成功.getValue()) && !jobLog.getStatus().equals(JobStatus.执行失败.getValue())) {
            return Result.fail(false, "错误:传入状态不正确");
        }
        log.info(MessageFormat.format("任务:{0}-执行状态:{1}-任务ID:{2}-订阅ID:{3}", jobLog.getJobName(), jobLog.getStatus(), jobLog.getId(), jobLog.getSubscribeId()));
        if (1 == jobLog.getLogType()) {
            return Result.ok(true, "任务回调设置状态成功");
        }
        JobLog newLog = new JobLog().setId(jobLog.getId()).setStatus(jobLog.getStatus());
        Date createTime = jobLog.getCreateTime();
        if (createTime != null) {
            newLog.setCostTime(new Date().getTime() - jobLog.getCreateTime().getTime());
        }
        String remark = jobLog.getRemark();
        if (!StringUtils.isEmpty(remark)) {
            //数据库设置长度1000，截取999位
            if (remark.length() >= 1000) {
                remark = remark.substring(0, 999);
            }
            newLog.setRemark(remark);
        }
        if (jobLogService.updateById(newLog)) {
            return Result.ok(true, "任务回调设置状态成功!");
        }
        return Result.fail(false, "错误:任务回调设置状态失败!");
    }


    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回任务日志-删除结果
     */
    @Log(title = "任务日志-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "任务日志-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:jobLog:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (jobLogService.removeById(id)) {
            return Result.ok(true, "任务日志-删除成功!");
        }
        return Result.fail(false, "错误:任务日志-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回任务日志-删除结果
     */
    @Log(title = "任务日志-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "任务日志-批量删除")
    @DeleteMapping("/batch")
    @RequiresPermissions("sys:jobLog:delete")
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.jobLogService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "任务日志-批量删除成功!");
        }
        return Result.fail(false, "错误:任务日志-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回任务日志对象
     */
    @Operation(summary = "任务日志-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:jobLog:query")
    public Result<JobLog> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        JobLog jobLog = jobLogService.getById(id);
        return Result.ok(jobLog, "任务日志-查询成功!");
    }
}

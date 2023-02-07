package cn.com.mfish.scheduler.controller;

import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.req.ReqJob;
import cn.com.mfish.scheduler.service.JobService;
import cn.com.mfish.common.web.page.PageResult;
import cn.com.mfish.common.web.page.ReqPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @description: 定时调度任务
 * @author: mfish
 * @date: 2023-02-03
 * @version: V1.0.0
 */
@Slf4j
@Api(tags = "定时调度任务")
@RestController
@RequestMapping("/job")
public class JobController {
    @Resource
    private JobService jobService;

    /**
     * 分页列表查询
     *
     * @param reqJob 定时调度任务请求参数
     * @return 返回定时调度任务-分页列表
     */
    @ApiOperation(value = "定时调度任务-分页列表查询", notes = "定时调度任务-分页列表查询")
    @GetMapping
    public Result<PageResult<Job>> queryPageList(ReqJob reqJob, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<Job>()
                .like(reqJob.getJobName() != null, Job::getJobName, reqJob.getJobName())
                .like(reqJob.getJobGroup() != null, Job::getJobGroup, reqJob.getJobGroup())
                .like(reqJob.getCron() != null, Job::getCron, reqJob.getCron())
                .like(reqJob.getInvokeMethod() != null, Job::getInvokeMethod, reqJob.getInvokeMethod())
                .orderByDesc(true, Job::getCreateTime);
        return Result.ok(new PageResult<>(jobService.list(queryWrapper)), "定时调度任务-查询成功!");
    }

    /**
     * 添加
     *
     * @param job 定时调度任务对象
     * @return 返回定时调度任务-添加结果
     */
    @Log(title = "定时调度任务-添加", operateType = OperateType.INSERT)
    @ApiOperation("定时调度任务-添加")
    @PostMapping
    public Result<Job> add(@RequestBody Job job) throws SchedulerException, ClassNotFoundException {
        return jobService.insertJob(job);
    }

    /**
     * 编辑
     *
     * @param job 定时调度任务对象
     * @return 返回定时调度任务-编辑结果
     */
    @Log(title = "定时调度任务-编辑", operateType = OperateType.UPDATE)
    @ApiOperation("定时调度任务-编辑")
    @PutMapping
    public Result<Job> edit(@RequestBody Job job) {
        if (jobService.updateById(job)) {
            return Result.ok(job, "定时调度任务-编辑成功!");
        }
        return Result.fail(job, "错误:定时调度任务-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回定时调度任务-删除结果
     */
    @Log(title = "定时调度任务-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation("定时调度任务-通过id删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (jobService.removeById(id)) {
            return Result.ok(true, "定时调度任务-删除成功!");
        }
        return Result.fail(false, "错误:定时调度任务-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回定时调度任务-删除结果
     */
    @Log(title = "定时调度任务-批量删除", operateType = OperateType.DELETE)
    @ApiOperation("定时调度任务-批量删除")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.jobService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "定时调度任务-批量删除成功!");
        }
        return Result.fail(false, "错误:定时调度任务-批量删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回定时调度任务对象
     */
    @ApiOperation("定时调度任务-通过id查询")
    @GetMapping("/{id}")
    public Result<Job> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        Job job = jobService.getById(id);
        return Result.ok(job, "定时调度任务-查询成功!");
    }
}

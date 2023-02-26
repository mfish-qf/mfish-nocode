package cn.com.mfish.scheduler.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.web.page.PageResult;
import cn.com.mfish.common.web.page.ReqPage;
import cn.com.mfish.scheduler.entity.Job;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import cn.com.mfish.scheduler.req.ReqJob;
import cn.com.mfish.scheduler.service.JobService;
import cn.com.mfish.scheduler.service.JobSubscribeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
    @Resource
    JobSubscribeService jobSubscribeService;

    /**
     * 分页列表查询
     *
     * @param reqJob 定时调度任务请求参数
     * @return 返回定时调度任务-分页列表
     */
    @ApiOperation(value = "定时调度任务-分页列表查询", notes = "定时调度任务-分页列表查询")
    @GetMapping
    @RequiresPermissions("sys:job:query")
    public Result<PageResult<Job>> queryPageList(ReqJob reqJob, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<Job>()
                .like(reqJob.getJobName() != null, Job::getJobName, reqJob.getJobName())
                .like(reqJob.getJobGroup() != null, Job::getJobGroup, reqJob.getJobGroup())
                .eq(reqJob.getJobType() != null, Job::getJobType, reqJob.getJobType())
                .like(reqJob.getMethodName() != null, Job::getMethodName, reqJob.getMethodName())
                .orderByDesc(true, Job::getCreateTime);
        List<Job> list = jobService.list(queryWrapper);
        List<JobSubscribe> subscribes = jobSubscribeService.getSubscribesByJobIds(list.stream().map(Job::getId).collect(Collectors.toList()));
        for (Job job : list) {
            job.setSubscribes(subscribes.stream().filter(jobSubscribe -> jobSubscribe.getJobId().equals(job.getId())).collect(Collectors.toList()));
        }
        return Result.ok(new PageResult<>(list), "定时调度任务-查询成功!");
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
    @RequiresPermissions("sys:job:insert")
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
    @RequiresPermissions("sys:job:update")
    public Result<Job> edit(@RequestBody Job job) throws SchedulerException, ClassNotFoundException {
        return jobService.updateJob(job);
    }

    @Log(title = "定时调度任务-设置状态", operateType = OperateType.UPDATE)
    @ApiOperation(value = "定时调度任务-设置状态", notes = "定时调度任务-设置状态")
    @PutMapping("/status")
    @RequiresPermissions("sys:job:update")
    public Result<Boolean> setStatus(@RequestBody Job job) throws SchedulerException, ClassNotFoundException {
        return jobService.setStatus(job);
    }

    @Log(title = "立即执行", operateType = OperateType.UPDATE)
    @ApiOperation(value = "立即执行", notes = "定时调度任务-设置状态")
    @PutMapping("/execute")
    @RequiresPermissions("sys:job:execute")
    public Result<Boolean> execute(@RequestBody Job job) {
        return jobService.executeJob(job);
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
    @RequiresPermissions("sys:job:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) throws SchedulerException {
        return jobService.deleteJob(id);
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回定时调度任务对象
     */
    @ApiOperation("定时调度任务-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("sys:job:query")
    public Result<Job> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        Job job = jobService.getById(id);
        return Result.ok(job, "定时调度任务-查询成功!");
    }
}

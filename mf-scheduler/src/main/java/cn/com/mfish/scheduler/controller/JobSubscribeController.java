package cn.com.mfish.scheduler.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.scheduler.entity.JobSubscribe;
import cn.com.mfish.scheduler.req.ReqJobSubscribe;
import cn.com.mfish.scheduler.service.JobSubscribeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 任务订阅表
 * @author: mfish
 * @date: 2023-02-20
 * @version: V2.0.1
 */
@Slf4j
@Tag(name = "任务订阅表")
@RestController
@RequestMapping("/jobSubscribe")
public class JobSubscribeController {
    @Resource
    private JobSubscribeService jobSubscribeService;

    /**
     * 分页列表查询
     *
     * @param reqJobSubscribe 任务订阅表请求参数
     * @return 返回任务订阅表-分页列表
     */
    @Operation(summary = "任务订阅表-分页列表查询", description = "任务订阅表-分页列表查询")
    @GetMapping
    public Result<PageResult<JobSubscribe>> queryPageList(ReqJobSubscribe reqJobSubscribe, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return Result.ok(new PageResult<>(jobSubscribeService.list(new LambdaQueryWrapper<JobSubscribe>()
                        .eq(reqJobSubscribe.getJobId() != null, JobSubscribe::getJobId, reqJobSubscribe.getJobId())))
                , "任务订阅表-查询成功!");
    }

    @Operation(summary = "根据任务ID获取策略列表")
    @GetMapping("/{jobId}")
    @RequiresPermissions("sys:job:query")
    public Result<List<JobSubscribe>> queryList(@Parameter(name = "jobId", description = "任务ID") @PathVariable("jobId") String jobId) {
        return Result.ok(jobSubscribeService.getSubscribesByJobId(jobId), "任务策略-查询成功!");
    }

    /**
     * 添加
     *
     * @param jobSubscribe 任务订阅表对象
     * @return 返回任务订阅表-添加结果
     */
    @Log(title = "任务订阅表-添加", operateType = OperateType.INSERT)
    @Operation(summary = "任务订阅表-添加")
    @PostMapping
    @RequiresPermissions("sys:job:insert")
    public Result<JobSubscribe> add(@RequestBody JobSubscribe jobSubscribe) {
        if (jobSubscribeService.save(jobSubscribe)) {
            return Result.ok(jobSubscribe, "任务订阅表-添加成功!");
        }
        return Result.fail(jobSubscribe, "错误:任务订阅表-添加失败!");
    }

    /**
     * 编辑
     *
     * @param jobSubscribe 任务订阅表对象
     * @return 返回任务订阅表-编辑结果
     */
    @Log(title = "任务订阅表-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "任务订阅表-编辑")
    @PutMapping
    @RequiresPermissions("sys:job:update")
    public Result<JobSubscribe> edit(@RequestBody JobSubscribe jobSubscribe) {
        if (jobSubscribeService.updateById(jobSubscribe)) {
            return Result.ok(jobSubscribe, "任务订阅表-编辑成功!");
        }
        return Result.fail(jobSubscribe, "错误:任务订阅表-编辑失败!");
    }

    @Log(title = "设置订阅状态", operateType = OperateType.UPDATE)
    @Operation(summary = "设置订阅状态", description = "设置订阅状态")
    @PutMapping("/status")
    @RequiresPermissions("sys:job:update")
    public Result<Boolean> setStatus(@RequestBody JobSubscribe jobSubscribe) throws SchedulerException {
        return jobSubscribeService.setStatus(jobSubscribe);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回任务订阅表-删除结果
     */
    @Log(title = "任务订阅表-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "任务订阅表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:job:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        if (jobSubscribeService.removeById(id)) {
            return Result.ok(true, "任务订阅表-删除成功!");
        }
        return Result.fail(false, "错误:任务订阅表-删除失败!");
    }

}

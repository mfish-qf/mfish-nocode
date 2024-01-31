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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 任务订阅表
 * @author: mfish
 * @date: 2023-02-20
 * @version: V1.2.0
 */
@Slf4j
@Api(tags = "任务订阅表")
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
    @ApiOperation(value = "任务订阅表-分页列表查询", notes = "任务订阅表-分页列表查询")
    @GetMapping
    public Result<PageResult<JobSubscribe>> queryPageList(ReqJobSubscribe reqJobSubscribe, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        return Result.ok(new PageResult<>(jobSubscribeService.list(new LambdaQueryWrapper<JobSubscribe>()
                        .eq(reqJobSubscribe.getJobId() != null, JobSubscribe::getJobId, reqJobSubscribe.getJobId())))
                , "任务订阅表-查询成功!");
    }

    @ApiOperation("根据任务ID获取策略列表")
    @GetMapping("/{jobId}")
    @RequiresPermissions("sys:job:query")
    public Result<List<JobSubscribe>> queryList(@ApiParam(name = "jobId", value = "任务ID") @PathVariable("jobId") String jobId) {
        return Result.ok(jobSubscribeService.getSubscribesByJobId(jobId), "任务策略-查询成功!");
    }

    /**
     * 添加
     *
     * @param jobSubscribe 任务订阅表对象
     * @return 返回任务订阅表-添加结果
     */
    @Log(title = "任务订阅表-添加", operateType = OperateType.INSERT)
    @ApiOperation("任务订阅表-添加")
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
    @ApiOperation("任务订阅表-编辑")
    @PutMapping
    @RequiresPermissions("sys:job:update")
    public Result<JobSubscribe> edit(@RequestBody JobSubscribe jobSubscribe) {
        if (jobSubscribeService.updateById(jobSubscribe)) {
            return Result.ok(jobSubscribe, "任务订阅表-编辑成功!");
        }
        return Result.fail(jobSubscribe, "错误:任务订阅表-编辑失败!");
    }

    @Log(title = "设置订阅状态", operateType = OperateType.UPDATE)
    @ApiOperation(value = "设置订阅状态", notes = "设置订阅状态")
    @PutMapping("/status")
    @RequiresPermissions("sys:job:update")
    public Result<Boolean> setStatus(@RequestBody JobSubscribe jobSubscribe) throws SchedulerException, ClassNotFoundException {
        return jobSubscribeService.setStatus(jobSubscribe);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回任务订阅表-删除结果
     */
    @Log(title = "任务订阅表-通过id删除", operateType = OperateType.DELETE)
    @ApiOperation("任务订阅表-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:job:delete")
    public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
        if (jobSubscribeService.removeById(id)) {
            return Result.ok(true, "任务订阅表-删除成功!");
        }
        return Result.fail(false, "错误:任务订阅表-删除失败!");
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回任务订阅表-删除结果
     */
    @Log(title = "任务订阅表-批量删除", operateType = OperateType.DELETE)
    @ApiOperation("任务订阅表-批量删除")
    @DeleteMapping("/batch")
    @RequiresPermissions("sys:job:delete")
    public Result<Boolean> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (this.jobSubscribeService.removeByIds(Arrays.asList(ids.split(",")))) {
            return Result.ok(true, "任务订阅表-批量删除成功!");
        }
        return Result.fail(false, "错误:任务订阅表-批量删除失败!");
    }
}

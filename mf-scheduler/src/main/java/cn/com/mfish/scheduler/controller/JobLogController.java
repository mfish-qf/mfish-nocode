package cn.com.mfish.scheduler.controller;

import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.scheduler.entity.JobLog;
import cn.com.mfish.scheduler.req.ReqJobLog;
import cn.com.mfish.scheduler.service.JobLogService;
import cn.com.mfish.common.web.page.PageResult;
import cn.com.mfish.common.web.page.ReqPage;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @description: 任务日志
 * @author: mfish
 * @date: 2023-02-14
 * @version: V1.0.0
 */
@Slf4j
@Api(tags = "任务日志")
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
	@ApiOperation(value = "任务日志-分页列表查询", notes = "任务日志-分页列表查询")
	@GetMapping
	public Result<PageResult<JobLog>> queryPageList(ReqJobLog reqJobLog, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
	    return Result.ok(new PageResult<>(jobLogService.list()), "任务日志-查询成功!");
	}

	/**
	 * 添加
	 *
	 * @param jobLog 任务日志对象
	 * @return 返回任务日志-添加结果
	 */
	@Log(title = "任务日志-添加", operateType = OperateType.INSERT)
	@ApiOperation("任务日志-添加")
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
	@ApiOperation("任务日志-编辑")
	@PutMapping
	public Result<JobLog> edit(@RequestBody JobLog jobLog) {
		if (jobLogService.updateById(jobLog)) {
		    return Result.ok(jobLog, "任务日志-编辑成功!");
		}
		return Result.fail(jobLog, "错误:任务日志-编辑失败!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id 唯一ID
	 * @return 返回任务日志-删除结果
	 */
	@Log(title = "任务日志-通过id删除", operateType = OperateType.DELETE)
	@ApiOperation("任务日志-通过id删除")
	@DeleteMapping("/{id}")
	public Result<Boolean> delete(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
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
	@ApiOperation("任务日志-批量删除")
	@DeleteMapping("/batch")
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
	@ApiOperation("任务日志-通过id查询")
	@GetMapping("/{id}")
	public Result<JobLog> queryById(@ApiParam(name = "id", value = "唯一性ID") @PathVariable String id) {
		JobLog jobLog = jobLogService.getById(id);
		return Result.ok(jobLog, "任务日志-查询成功!");
	}
}

package cn.com.mfish.demo.controller;

import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.demo.entity.DemoOrderDetail;
import cn.com.mfish.demo.req.ReqDemoOrderDetail;
import cn.com.mfish.demo.service.DemoOrderDetailService;
import cn.com.mfish.common.core.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.io.IOException;
import java.util.List;

/**
 * @description: 销售订单明细
 * @author: mfish
 * @date: 2024-09-02
 * @version: V2.1.0
 */
@Slf4j
@Tag(name = "销售订单明细")
@RestController
@RequestMapping("/demoOrderDetail")
public class DemoOrderDetailController {
	@Resource
	private DemoOrderDetailService demoOrderDetailService;

	/**
	 * 分页列表查询
	 *
	 * @param reqDemoOrderDetail 销售订单明细请求参数
	 * @param reqPage 分页参数
	 * @return 返回销售订单明细-分页列表
	 */
	@Operation(summary = "销售订单明细-分页列表查询", description = "销售订单明细-分页列表查询")
	@GetMapping
    @RequiresPermissions("demo:demoOrder:query")
	public Result<PageResult<DemoOrderDetail>> queryPageList(ReqDemoOrderDetail reqDemoOrderDetail, ReqPage reqPage) {
		return Result.ok(new PageResult<>(queryList(reqDemoOrderDetail, reqPage)), "销售订单明细-查询成功!");
	}

	/**
	* 获取列表
	*
	* @param reqDemoOrderDetail 销售订单明细请求参数
	* @param reqPage 分页参数
	* @return 返回销售订单明细-分页列表
	*/
	private List<DemoOrderDetail> queryList(ReqDemoOrderDetail reqDemoOrderDetail, ReqPage reqPage) {
		PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
		LambdaQueryWrapper<DemoOrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<DemoOrderDetail>()
			.eq(!StringUtils.isEmpty(reqDemoOrderDetail.getId()), DemoOrderDetail::getId, reqDemoOrderDetail.getId())
					.eq(!StringUtils.isEmpty(reqDemoOrderDetail.getOrderId()), DemoOrderDetail::getOrderId, reqDemoOrderDetail.getOrderId())
		;
		return demoOrderDetailService.list(lambdaQueryWrapper);
	}

	/**
	 * 添加
	 *
	 * @param demoOrderDetail 销售订单明细对象
	 * @return 返回销售订单明细-添加结果
	 */
	@Log(title = "销售订单明细-添加", operateType = OperateType.INSERT)
	@Operation(summary = "销售订单明细-添加")
	@PostMapping
	@RequiresPermissions("demo:demoOrder:insert")
	public Result<DemoOrderDetail> add(@RequestBody DemoOrderDetail demoOrderDetail) {
		if (demoOrderDetailService.save(demoOrderDetail)) {
			return Result.ok(demoOrderDetail, "销售订单明细-添加成功!");
		}
        return Result.fail(demoOrderDetail, "错误:销售订单明细-添加失败!");
	}

	/**
	 * 编辑
	 *
	 * @param demoOrderDetail 销售订单明细对象
	 * @return 返回销售订单明细-编辑结果
	 */
	@Log(title = "销售订单明细-编辑", operateType = OperateType.UPDATE)
	@Operation(summary = "销售订单明细-编辑")
	@PutMapping
	@RequiresPermissions("demo:demoOrder:update")
	public Result<DemoOrderDetail> edit(@RequestBody DemoOrderDetail demoOrderDetail) {
		if (demoOrderDetailService.updateById(demoOrderDetail)) {
		    return Result.ok(demoOrderDetail, "销售订单明细-编辑成功!");
		}
		return Result.fail(demoOrderDetail, "错误:销售订单明细-编辑失败!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id 唯一ID
	 * @return 返回销售订单明细-删除结果
	 */
	@Log(title = "销售订单明细-通过id删除", operateType = OperateType.DELETE)
	@Operation(summary = "销售订单明细-通过id删除")
	@DeleteMapping("/{id}")
	@RequiresPermissions("demo:demoOrder:delete")
	public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
		if (demoOrderDetailService.removeById(id)) {
			return Result.ok(true, "销售订单明细-删除成功!");
		}
		return Result.fail(false, "错误:销售订单明细-删除失败!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids 批量ID
	 * @return 返回销售订单明细-删除结果
	 */
	@Log(title = "销售订单明细-批量删除", operateType = OperateType.DELETE)
	@Operation(summary = "销售订单明细-批量删除")
	@DeleteMapping("/batch/{ids}")
	@RequiresPermissions("demo:demoOrder:delete")
	public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
		if (this.demoOrderDetailService.removeByIds(Arrays.asList(ids.split(",")))) {
		    return Result.ok(true, "销售订单明细-批量删除成功!");
		}
		return Result.fail(false, "错误:销售订单明细-批量删除失败!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id 唯一ID
	 * @return 返回销售订单明细对象
	 */
	@Operation(summary = "销售订单明细-通过id查询")
	@GetMapping("/{id}")
	@RequiresPermissions("demo:demoOrder:query")
	public Result<DemoOrderDetail> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
		DemoOrderDetail demoOrderDetail = demoOrderDetailService.getById(id);
		return Result.ok(demoOrderDetail, "销售订单明细-查询成功!");
	}

	/**
	* 导出
	* @param reqDemoOrderDetail 销售订单明细请求参数
	* @param reqPage 分页参数
	* @throws IOException IO异常
	*/
	@Operation(summary = "导出销售订单明细", description = "导出销售订单明细")
	@GetMapping("/export")
	@RequiresPermissions("demo:demoOrder:export")
	public void export(ReqDemoOrderDetail reqDemoOrderDetail, ReqPage reqPage) throws IOException {
		//swagger调用会用问题，使用postman测试
		ExcelUtils.write("DemoOrderDetail", queryList(reqDemoOrderDetail, reqPage));
	}
}

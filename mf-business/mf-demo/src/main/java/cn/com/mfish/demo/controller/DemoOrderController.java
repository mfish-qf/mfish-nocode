package cn.com.mfish.demo.controller;

import cn.com.mfish.common.core.enums.OperateType;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.log.annotation.Log;
import cn.com.mfish.common.oauth.annotation.RequiresPermissions;
import cn.com.mfish.demo.entity.DemoOrder;
import cn.com.mfish.demo.req.ReqDemoOrder;
import cn.com.mfish.demo.service.DemoOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @description: 销售订单
 * @author: mfish
 * @date: 2024-09-13
 * @version: V2.1.0
 */
@Slf4j
@Tag(name = "销售订单")
@RestController
@RequestMapping("/demoOrder")
public class DemoOrderController {
    @Resource
    private DemoOrderService demoOrderService;

    /**
     * 分页列表查询
     *
     * @param reqDemoOrder 销售订单请求参数
     * @param reqPage      分页参数
     * @return 返回销售订单-分页列表
     */
    @Operation(summary = "销售订单-分页列表查询", description = "销售订单-分页列表查询")
    @GetMapping
    @RequiresPermissions("demo:demoOrder:query")
    public Result<PageResult<DemoOrder>> queryPageList(ReqDemoOrder reqDemoOrder, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoOrder, reqPage)), "销售订单-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqDemoOrder 销售订单请求参数
     * @param reqPage      分页参数
     * @return 返回销售订单-分页列表
     */
    private List<DemoOrder> queryList(ReqDemoOrder reqDemoOrder, ReqPage reqPage) {
        Object[] payType = null;
        if (!StringUtils.isEmpty(reqDemoOrder.getPayType())) {
            payType = reqDemoOrder.getPayType().split(",");
        }
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<DemoOrder> lambdaQueryWrapper = new LambdaQueryWrapper<DemoOrder>()
                .like(!StringUtils.isEmpty(reqDemoOrder.getUserName()), DemoOrder::getUserName, reqDemoOrder.getUserName())
                .eq(null != reqDemoOrder.getOrderStatus(), DemoOrder::getOrderStatus, reqDemoOrder.getOrderStatus())
                .in(null != payType, DemoOrder::getPayType, payType)
                .eq(null != reqDemoOrder.getDeliveryType(), DemoOrder::getDeliveryType, reqDemoOrder.getDeliveryType());
        return demoOrderService.list(lambdaQueryWrapper);
    }

    /**
     * 添加
     *
     * @param demoOrder 销售订单对象
     * @return 返回销售订单-添加结果
     */
    @Log(title = "销售订单-添加", operateType = OperateType.INSERT)
    @Operation(summary = "销售订单-添加")
    @PostMapping
    @RequiresPermissions("demo:demoOrder:insert")
    public Result<DemoOrder> add(@RequestBody DemoOrder demoOrder) {
        return demoOrderService.addOrder(demoOrder);
    }

    /**
     * 编辑
     *
     * @param demoOrder 销售订单对象
     * @return 返回销售订单-编辑结果
     */
    @Log(title = "销售订单-编辑", operateType = OperateType.UPDATE)
    @Operation(summary = "销售订单-编辑")
    @PutMapping
    @RequiresPermissions("demo:demoOrder:update")
    public Result<DemoOrder> edit(@RequestBody DemoOrder demoOrder) {
        return demoOrderService.editOrder(demoOrder);
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回销售订单-删除结果
     */
    @Log(title = "销售订单-通过id删除", operateType = OperateType.DELETE)
    @Operation(summary = "销售订单-通过id删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("demo:demoOrder:delete")
    public Result<Boolean> delete(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        return demoOrderService.deleteOrder(id);
    }

    /**
     * 批量删除
     *
     * @param ids 批量ID
     * @return 返回销售订单-删除结果
     */
    @Log(title = "销售订单-批量删除", operateType = OperateType.DELETE)
    @Operation(summary = "销售订单-批量删除")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("demo:demoOrder:delete")
    public Result<Boolean> deleteBatch(@Parameter(name = "ids", description = "唯一性ID") @PathVariable String ids) {
        return demoOrderService.deleteBatchOrder(ids);
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回销售订单对象
     */
    @Operation(summary = "销售订单-通过id查询")
    @GetMapping("/{id}")
    @RequiresPermissions("demo:demoOrder:query")
    public Result<DemoOrder> queryById(@Parameter(name = "id", description = "唯一性ID") @PathVariable String id) {
        DemoOrder demoOrder = demoOrderService.getById(id);
        return Result.ok(demoOrder, "销售订单-查询成功!");
    }

    /**
     * 导出
     *
     * @param reqDemoOrder 销售订单请求参数
     * @param reqPage      分页参数
     * @throws IOException IO异常
     */
    @Operation(summary = "导出销售订单", description = "导出销售订单")
    @GetMapping("/export")
    @RequiresPermissions("demo:demoOrder:export")
    public void export(ReqDemoOrder reqDemoOrder, ReqPage reqPage) throws IOException {
        //swagger调用会用问题，使用postman测试
        ExcelUtils.write("DemoOrder", queryList(reqDemoOrder, reqPage));
    }
}

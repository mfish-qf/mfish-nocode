package cn.com.mfish.demo.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.demo.entity.DemoOrder;
import cn.com.mfish.demo.entity.DemoOrderDetail;
import cn.com.mfish.demo.mapper.DemoOrderDetailMapper;
import cn.com.mfish.demo.mapper.DemoOrderMapper;
import cn.com.mfish.demo.service.DemoOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @description: 销售订单
 * @author: mfish
 * @date: 2024-09-13
 * @version: V1.3.1
 */
@Service
public class DemoOrderServiceImpl extends ServiceImpl<DemoOrderMapper, DemoOrder> implements DemoOrderService {
    @Resource
    DemoOrderDetailMapper demoOrderDetailMapper;

    @Override
    @Transactional
    public Result<DemoOrder> addOrder(DemoOrder demoOrder) {
        if (save(demoOrder)) {
            if (!insertDetails(demoOrder.getDetails(), demoOrder.getId())) {
                throw new MyRuntimeException("错误：销售订单明细-添加失败!");
            }
            return Result.ok(demoOrder, "销售订单-添加成功!");
        }
        return Result.fail(demoOrder, "错误:销售订单-添加失败!");
    }

    @Override
    @Transactional
    public Result<DemoOrder> editOrder(DemoOrder demoOrder) {
        if (updateById(demoOrder)) {
            if (!deleteDetails(Collections.singletonList(demoOrder.getId()))) {
                throw new MyRuntimeException("错误：销售订单明细-删除失败!");
            }
            if (!insertDetails(demoOrder.getDetails(), demoOrder.getId())) {
                throw new MyRuntimeException("错误：销售订单明细-添加失败!");
            }
            return Result.ok(demoOrder, "销售订单-编辑成功!");
        }
        return Result.fail(demoOrder, "错误:销售订单-编辑失败!");
    }

    @Override
    @Transactional
    public Result<Boolean> deleteOrder(String id) {
        if (removeById(id)) {
            if (deleteDetails(Collections.singletonList(id))) {
                return Result.ok(true, "销售订单-删除成功!");
            }
            throw new MyRuntimeException("错误：销售订单明细-删除失败!");
        }
        return Result.fail(false, "错误:销售订单-删除失败!");
    }

    @Override
    public Result<Boolean> deleteBatchOrder(String ids) {
        List<String> idArray = Arrays.asList(ids.split(","));
        if (removeByIds(idArray)) {
            if (deleteDetails(idArray)) {
                return Result.ok(true, "销售订单明细-批量删除成功!");
            }
            throw new MyRuntimeException("错误：销售订单明细-删除失败!");
        }
        return Result.fail(false, "错误:销售订单-批量删除失败!");
    }

    /**
     * 根据订单ID删除订单详情
     *
     * @param orderIds 订单ID
     * @return 如果删除成功，返回true，否则返回false
     */
    private boolean deleteDetails(List<String> orderIds) {
        return demoOrderDetailMapper.delete(new LambdaQueryWrapper<DemoOrderDetail>().in(DemoOrderDetail::getOrderId, orderIds)) >= 0;
    }


    /**
     * 插入订单详情列表中的部分列到数据库中
     * 如果列表为空或null，则直接返回true
     *
     * @param details 订单详情列表
     * @return 如果插入成功，返回true，否则返回false
     */
    private boolean insertDetails(List<DemoOrderDetail> details, String orderId) {
        if (details == null || details.isEmpty()) {
            return true;
        }
        for (DemoOrderDetail detail : details) {
            detail.setOrderId(orderId);
        }
        return demoOrderDetailMapper.insertBatchSomeColumn(details) == details.size();
    }
}

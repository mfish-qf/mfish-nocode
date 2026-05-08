package cn.com.mfish.demo.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.demo.entity.DemoOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 销售订单
 * @author: mfish
 * @date: 2024-09-13
 * @version: V2.3.1
 */
public interface DemoOrderService extends IService<DemoOrder> {
    /**
     * 添加销售订单及其明细
     *
     * @param demoOrder 销售订单对象（包含订单明细）
     * @return 返回添加结果
     */
    Result<DemoOrder> addOrder(DemoOrder demoOrder);

    /**
     * 编辑销售订单及其明细（先删除原明细再重新插入）
     *
     * @param demoOrder 销售订单对象（包含订单明细）
     * @return 返回编辑结果
     */
    Result<DemoOrder> editOrder(DemoOrder demoOrder);

    /**
     * 通过id删除销售订单及其明细
     *
     * @param id 订单ID
     * @return 返回删除结果
     */
    Result<Boolean> deleteOrder(String id);

    /**
     * 批量删除销售订单及其明细
     *
     * @param ids 批量订单ID，多个ID以逗号分隔
     * @return 返回删除结果
     */
    Result<Boolean> deleteBatchOrder(String ids);
}

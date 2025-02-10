package cn.com.mfish.demo.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.demo.entity.DemoOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 销售订单
 * @author: mfish
 * @date: 2024-09-13
 * @version: V1.3.2
 */
public interface DemoOrderService extends IService<DemoOrder> {
    Result<DemoOrder> addOrder(DemoOrder demoOrder);
    Result<DemoOrder> editOrder(DemoOrder demoOrder);
    Result<Boolean> deleteOrder(String id);
    Result<Boolean> deleteBatchOrder(String ids);
}

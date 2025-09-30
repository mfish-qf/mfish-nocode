package cn.com.mfish.demo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 销售订单明细
 * @author: mfish
 * @date: 2024-09-02
 * @version: V2.2.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "销售订单明细请求参数")
public class ReqDemoOrderDetail {
    @Schema(description = "明细主键")
    private String id;
    @Schema(description = "订单号")
    private String orderId;
}

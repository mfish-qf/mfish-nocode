package cn.com.mfish.demo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 导入导出Demo
 * @author: mfish
 * @date: 2024-09-02
 * @version: V1.3.2
 */
@Data
@Accessors(chain = true)
@Schema(description = "导入导出Demo请求参数")
public class ReqDemoImportExport {
    @Schema(description = "姓名")
    private String userName;
    @Schema(description = "订单状态")
    private Short orderStatus;
    @Schema(description = "支付类型")
    private Short payType;
    @Schema(description = "配送方式")
    private Short deliveryType;
}

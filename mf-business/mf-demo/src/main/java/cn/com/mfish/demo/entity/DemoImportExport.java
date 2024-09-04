package cn.com.mfish.demo.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 导入导出Demo
 * @author: mfish
 * @date: 2024-09-02
 * @version: V1.3.1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_import_export")
@Schema(description = "demo_import_export对象 导入导出Demo")
public class DemoImportExport extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;
    @ExcelProperty("姓名")
    @Schema(description = "姓名")
    private String userName;
    @ExcelProperty("联系电话")
    @Schema(description = "联系电话")
    private String userPhone;
    @ExcelProperty("收货地址")
    @Schema(description = "收货地址")
    private String userAddress;
    @ExcelProperty("订单状态")
    @Schema(description = "订单状态")
    private Short orderStatus;
    @ExcelProperty("订单总价")
    @Schema(description = "订单总价")
    private BigDecimal totalAmount;
    @ExcelProperty("支付金额")
    @Schema(description = "支付金额")
    private BigDecimal payAmount;
    @ExcelProperty("快递费用")
    @Schema(description = "快递费用")
    private BigDecimal expressAmount;
    @ExcelProperty("订单描述")
    @Schema(description = "订单描述")
    private String orderDesc;
    @ExcelProperty("支付流水")
    @Schema(description = "支付流水")
    private String tradeNo;
    @ExcelProperty("支付类型")
    @Schema(description = "支付类型")
    private Short payType;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("支付时间")
    @Schema(description = "支付时间")
    private Date payTime;
    @ExcelProperty("配送方式")
    @Schema(description = "配送方式")
    private Short deliveryType;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("收货时间")
    @Schema(description = "收货时间")
    private Date confirmTime;
}

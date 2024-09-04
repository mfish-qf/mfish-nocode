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
 * @description: 销售订单明细
 * @author: mfish
 * @date: 2024-09-02
 * @version: V1.3.1
 */
@Data
@TableName("demo_order_detail")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "demo_order_detail对象 销售订单明细")
public class DemoOrderDetail extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("订单号")
    @Schema(description = "订单号")
	private String orderId;
    @ExcelProperty("商品名称")
    @Schema(description = "商品名称")
	private String goodsName;
    @ExcelProperty("商品货品图片或者商品图片")
    @Schema(description = "商品货品图片或者商品图片")
	private String picUrl;
    @ExcelProperty("商品单价")
    @Schema(description = "商品单价")
	private BigDecimal goodsPrice;
    @ExcelProperty("商品原价")
    @Schema(description = "商品原价")
	private BigDecimal goodsPricePro;
    @ExcelProperty("购买数量")
    @Schema(description = "购买数量")
	private Short goodsCount;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("发货时间")
    @Schema(description = "发货时间")
	private Date sendTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelProperty("收货时间")
    @Schema(description = "收货时间")
	private Date receiveTime;
    @ExcelProperty("实际支付金额")
    @Schema(description = "实际支付金额")
	private BigDecimal payAmount;
    @ExcelProperty("商品优惠总金额")
    @Schema(description = "商品优惠总金额")
	private BigDecimal discount;
    @ExcelProperty("优惠券扣减金额")
    @Schema(description = "优惠券扣减金额")
	private BigDecimal couponDiscount;
}

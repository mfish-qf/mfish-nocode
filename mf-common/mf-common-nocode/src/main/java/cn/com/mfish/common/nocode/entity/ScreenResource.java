package cn.com.mfish.common.nocode.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.math.BigDecimal;

/**
 * @description: 大屏资源信息
 * @author: mfish
 * @date: 2025-03-19
 * @version: V2.2.0
 */
@Data
@TableName("mf_screen_resource")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "mf_screen_resource对象 大屏资源信息")
public class ScreenResource extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;
    @ExcelProperty("来源id")
    @Schema(description = "来源id")
	private String sourceId;
    @ExcelProperty("资源名称")
    @Schema(description = "资源名称")
	private String name;
    @ExcelProperty("资源描述")
    @Schema(description = "资源描述")
	private String remark;
    @ExcelProperty("资源分类")
    @Schema(description = "资源分类")
	private String category;
    @ExcelProperty("画布配置")
    @Schema(description = "画布配置")
	private String canvasConfig;
    @ExcelProperty("图片KEY")
    @Schema(description = "图片KEY")
	private String picKey;
    @ExcelProperty("大屏组件容器配置-位置信息(json方式存储)")
    @Schema(description = "大屏组件容器配置-位置信息(json方式存储)")
	private String contains;
    @ExcelProperty("资源价格")
    @Schema(description = "资源价格")
	private BigDecimal price;
    @ExcelProperty("使用次数")
    @Schema(description = "使用次数")
	private Integer useCount;
    @ExcelProperty("收藏次数")
    @Schema(description = "收藏次数")
	private Integer favoritesCount;
    @ExcelProperty("点击次数")
    @Schema(description = "点击次数")
	private Integer clickCount;
    @ExcelProperty("租户ID")
    @Schema(description = "租户ID")
	private String tenantId;
    @ExcelProperty("审核状态")
    @Schema(description = "审核状态")
    private Integer auditState;
    @ExcelProperty("创建人头像")
    @Schema(description = "创建人头像")
    @TableField(exist = false)
    private String headImgUrl;
}

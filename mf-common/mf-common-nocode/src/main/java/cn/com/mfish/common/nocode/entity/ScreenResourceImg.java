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

/**
 * @description: 大屏图片资源信息
 * @author: mfish
 * @date: 2026-09-04
 * @version: V2.4.1
 */
@Data
@TableName("mf_screen_resource_img")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "mf_screen_resource_img对象 大屏图片资源信息")
@Accessors(chain = true)
public class ScreenResourceImg extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    @Accessors(chain = true)
    private String id;
    @ExcelProperty("图片名称")
    @Schema(description = "图片名称")
    private String name;
    @ExcelProperty("图片描述")
    @Schema(description = "图片描述")
    private String remark;
    @ExcelProperty("图片分类")
    @Schema(description = "图片分类（sys_dict_category分类id）")
    private String category;
    @ExcelProperty("图片文件KEY")
    @Schema(description = "图片文件KEY")
    private String picKey;
    @ExcelProperty("原始文件名")
    @Schema(description = "原始文件名")
    private String fileName;
    @ExcelProperty("文件类型")
    @Schema(description = "文件类型")
    private String fileType;
    @ExcelProperty("文件大小")
    @Schema(description = "文件大小(字节)")
    private Long fileSize;
    @ExcelProperty("使用次数")
    @Schema(description = "使用次数")
    private Integer useCount;
    @ExcelProperty("租户ID")
    @Schema(description = "租户ID")
    private String tenantId;
    @ExcelProperty("审核状态")
    @Schema(description = "审核状态（null未发布 0待审核 1审核通过 2审核不通过）")
    private Integer auditState;
    @ExcelProperty("删除标记")
    @Schema(description = "删除标记（0正常 1删除）")
    private Integer delFlag;
    @ExcelProperty("创建人头像")
    @Schema(description = "创建人头像")
    @TableField(exist = false)
    private String headImgUrl;
    @ExcelProperty("分类名称")
    @Schema(description = "分类名称")
    @TableField(exist = false)
    private String categoryName;
}

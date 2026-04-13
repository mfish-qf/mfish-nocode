package cn.com.mfish.workflow.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.idev.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 流程管理
 * @author: mfish
 * @date: 2026-03-30
 * @version: V2.3.1
 */
@Data
@TableName("flw_mf_manage")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "flw_mf_manage对象 流程管理")
@Accessors(chain = true)
public class FlowManage extends BaseEntity<String> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ExcelProperty("流程key")
    @Schema(description = "流程key")
	private String flowKey;
    @ExcelProperty("流程名称")
    @Schema(description = "流程名称")
	private String name;
    @ExcelProperty("流程描述")
    @Schema(description = "流程描述")
	private String remark;
    @ExcelProperty("版本号")
    @Schema(description = "版本号")
	private Integer version;
    @ExcelProperty("是否发布（0未发布 1已发布）")
    @Schema(description = "是否发布（0未发布 1已发布）")
	private Short released;
    @ExcelProperty("流程配置")
    @Schema(description = "流程配置")
	private String flowConfig;
    @ExcelProperty("hex")
    @Schema(description = "hex")
    private String hex;
    @ExcelProperty("删除标记(0未删除1删除)")
    @Schema(description = "删除标记(0未删除1删除)")
	private Integer delFlag;
}

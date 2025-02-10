package cn.com.mfish.demo.entity;

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
 * @description: demo_data_scope
 * @author: mfish
 * @date: 2024-09-04
 * @version: V1.3.2
 */
@Data
@TableName("demo_data_scope")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "demo_data_scope对象 demo_data_scope")
public class DemoDataScope extends BaseEntity<Integer> {
    @ExcelProperty("唯一ID")
    @Schema(description = "唯一ID")
    @TableId(type = IdType.AUTO)
    @Accessors(chain = true)
    private Integer id;
    @ExcelProperty("角色id")
    @Schema(description = "角色id")
	private String roleId;
    @ExcelProperty("租户id")
    @Schema(description = "租户id")
	private String tenantId;
    @ExcelProperty("组织id")
    @Schema(description = "组织id")
	private String orgId;
    @ExcelProperty("名称")
    @Schema(description = "名称")
	private String name;
}

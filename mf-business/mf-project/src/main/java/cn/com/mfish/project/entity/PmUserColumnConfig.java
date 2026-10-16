package cn.com.mfish.project.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 用户表格列配置表(按登录人员保存, 每人一行)
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.2
 */
@Data
@TableName("pm_user_column_config")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "pm_user_column_config对象 用户表格列配置")
public class PmUserColumnConfig extends BaseEntity<String> {
    @Schema(description = "唯一ID(雪花)")
    @TableId(type = IdType.ASSIGN_ID)
    @Accessors(chain = true)
    private String id;
    @Schema(description = "所属用户ID(当前登录人员)")
    private String userId;
    @Schema(description = "列配置JSON(含key/title/category/width/visible)")
    private String configJson;
    @Schema(description = "删除标志(0正常 1已删除)")
    @TableLogic
    private Integer delFlag;
    @Schema(description = "备注")
    private String remark;
}

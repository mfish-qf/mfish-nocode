package cn.com.mfish.sys.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 界面配置
 * @author: mfish
 * @date: 2023-03-07
 * @version: V2.0.0
 */
@Data
@TableName("sys_config")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sys_config对象 界面配置")
public class SysConfig extends BaseEntity<Integer> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.AUTO)
    private Integer id;
    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "配置信息")
    private String config;
    @Schema(description = "配置类型 0 样式风格 1表格配置")
    private Integer type;
}

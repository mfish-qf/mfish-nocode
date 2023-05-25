package cn.com.mfish.sys.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 界面配置
 * @author: mfish
 * @date: 2023-03-07
 * @version: V1.0.1
 */
@Data
@TableName("sys_config")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sys_config对象", description = "界面配置")
public class SysConfig extends BaseEntity<Integer> {
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "用户ID")
	private String userId;
    @ApiModelProperty(value = "配置信息")
	private String config;
}

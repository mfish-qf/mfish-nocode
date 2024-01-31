package cn.com.mfish.sys.api.entity;

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
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V1.2.0
 */
@Data
@TableName("sys_db_connect")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sys_db_connect对象", description = "数据库连接")
public class DbConnect extends BaseEntity<String> {
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty(value = "连接名")
	private String dbTitle;
    @ApiModelProperty(value = "数据库类型（0 mysql 1 oracle 2 pgsql）")
	private Integer dbType;
    @ApiModelProperty(value = "连接池类型(Druid,Hikari)")
	private String poolType;
    @ApiModelProperty(value = "主机")
	private String host;
    @ApiModelProperty(value = "端口号")
	private String port;
    @ApiModelProperty(value = "数据库名")
	private String dbName;
    @ApiModelProperty(value = "数据库登录用户名")
	private String username;
    @ApiModelProperty(value = "数据库登录密码")
	private String password;
    @ApiModelProperty(value = "数据源配置项(JSON格式）")
	private String options;
    @ApiModelProperty(value = "备注")
	private String remark;
}

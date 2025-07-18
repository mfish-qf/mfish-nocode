package cn.com.mfish.sys.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V2.0.1
 */
@Data
@TableName("sys_db_connect")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sys_db_connect对象 数据库连接")
public class DbConnect extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @Schema(description = "租户ID")
    private String tenantId;
    @Schema(description = "连接名")
    private String dbTitle;
    @Schema(description = "数据库类型（0 mysql 1 oracle 2 pgsql）")
    private Integer dbType;
    @Schema(description = "连接池类型(Druid,Hikari)")
    private String poolType;
    @Schema(description = "主机")
    private String host;
    @Schema(description = "端口号")
    private String port;
    @Schema(description = "数据库名")
    private String dbName;
    @Schema(description = "数据库登录用户名")
    private String username;
    @Schema(description = "数据库登录密码")
    private String password;
    @Schema(description = "数据源配置项(JSON格式）")
    private String options;
    @Schema(description = "是否公共（0 否 1 是）")
    private Integer isPublic;
    @Schema(description = "备注")
    private String remark;
}

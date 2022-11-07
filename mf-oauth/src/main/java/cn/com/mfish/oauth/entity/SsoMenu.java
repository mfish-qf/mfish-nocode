package cn.com.mfish.oauth.entity;

import cn.com.mfish.oauth.req.ReqSsoMenu;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @Date: 2022-09-21
 * @Version: V1.0
 */
@Data
@TableName("sso_menu")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sso_menu对象", description = "菜单权限表")
public class SsoMenu extends ReqSsoMenu {
    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "菜单ID")
	private String id;
    @ApiModelProperty(value = "父菜单ID")
	private String parentId;
    @ApiModelProperty(value = "菜单图标")
	private String menuIcon;
    @ApiModelProperty(value = "菜单编码")
    private String menuCode;
    @ApiModelProperty(value = "菜单级别")
    private Integer menuLevel;
    @ApiModelProperty(value = "菜单顺序")
	private Integer menuSort;
    @ApiModelProperty(value = "路由地址")
	private String routePath;
    @ApiModelProperty(value = "组件路径")
	private String component;
    @ApiModelProperty(value = "是否缓存(1是 0否)")
    private Integer isKeepalive;
    @ApiModelProperty(value = "是否为外部链接（1是 0否）")
	private Integer isExternal;
    @ApiModelProperty(value = "描述")
	private String remark;
    @ApiModelProperty(value = "创建者")
	private String createBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
    @ApiModelProperty(value = "更新者")
	private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
}

package cn.com.mfish.oauth.entity;

import cn.com.mfish.common.core.entity.BaseTreeEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @date: 2022-09-21
 * @Version: V1.2.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sso_menu")
@Accessors(chain = true)
@ApiModel(value = "sso_menu对象", description = "菜单权限表")
public class SsoMenu extends BaseTreeEntity<String> {
    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "菜单ID")
    private String id;
    @ApiModelProperty(value = "父菜单ID")
    private String parentId;
    @ApiModelProperty(value = "菜单名称")
    private String menuName;
    @ApiModelProperty(value = "菜单类型(0目录 1菜单 2按钮)")
    private Integer menuType;
    @ApiModelProperty(value = "菜单状态(1显示 0隐藏)")
    private Integer isVisible;
    @ApiModelProperty(value = "当前激活菜单(当菜单隐藏时，设置当前激活的菜单项)")
    private String activeMenu;
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
    @ApiModelProperty(value = "是否为外部链接(1是 0否)")
    private Integer isExternal;
    @ApiModelProperty(value = "描述")
    private String remark;
    @ApiModelProperty(value = "权限标识(多个标识逗号隔开)")
    private String permissions;
}

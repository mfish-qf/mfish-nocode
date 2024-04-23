package cn.com.mfish.common.oauth.api.entity;

import cn.com.mfish.common.core.entity.BaseTreeEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @date: 2022-09-21
 * @Version: V1.3.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sso_menu")
@Accessors(chain = true)
@Schema(description = "sso_menu对象 菜单权限表")
public class SsoMenu extends BaseTreeEntity<String> {
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "菜单ID")
    private String id;
    @Schema(description = "父菜单ID")
    private String parentId;
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "菜单类型(0目录 1菜单 2按钮)")
    private Integer menuType;
    @Schema(description = "菜单状态(1显示 0隐藏)")
    private Integer isVisible;
    @Schema(description = "当前激活菜单(当菜单隐藏时，设置当前激活的菜单项)")
    private String activeMenu;
    @Schema(description = "菜单图标")
    private String menuIcon;
    @Schema(description = "菜单编码")
    private String menuCode;
    @Schema(description = "菜单级别")
    private Integer menuLevel;
    @Schema(description = "菜单顺序")
    private Integer menuSort;
    @Schema(description = "路由地址")
    private String routePath;
    @Schema(description = "组件路径")
    private String component;
    @Schema(description = "是否缓存(1是 0否)")
    private Integer isKeepalive;
    @Schema(description = "是否为外部链接(1是 0否)")
    private Integer isExternal;
    @Schema(description = "描述")
    private String remark;
    @Schema(description = "权限标识(多个标识逗号隔开)")
    private String permissions;
}

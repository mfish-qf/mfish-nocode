package cn.com.mfish.common.oauth.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 菜单权限表
 * @Author: mfish
 * @date: 2022-09-21
 * @Version: V1.3.2
 */
@Data
@Accessors(chain = true)
@Schema(description = "菜单权限表请求参数")
public class ReqSsoMenu {
    @Schema(description = "菜单名称")
    private String menuName;
    @Schema(description = "菜单类型（0目录 1菜单 2按钮）")
    private Integer menuType;
    @Schema(description = "菜单状态（1显示 0隐藏）")
    private Integer isVisible;
    @Schema(description = "权限标识")
    private String permission;
    @Schema(description = "是否返回按钮 true 不返回按钮 false 返回按钮")
    private Boolean noButton = false;
}

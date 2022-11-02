package cn.com.mfish.oauth.vo;

import cn.com.mfish.oauth.entity.SsoMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author ：qiufeng
 * @description：菜单树
 * @date ：2022/11/2 16:31
 */
@Data
@Accessors(chain = true)
@ApiModel("菜单树")
public class MenuTree extends SsoMenu {
    @ApiModelProperty("子菜单")
    private List<MenuTree> children;
}

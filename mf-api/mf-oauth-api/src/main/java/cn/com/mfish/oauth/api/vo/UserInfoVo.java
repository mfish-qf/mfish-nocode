package cn.com.mfish.oauth.api.vo;

import cn.com.mfish.oauth.api.entity.UserInfo;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ：qiufeng
 * @description：用户信息Vo
 * @date ：2022/12/1 21:07
 */
@Data
public class UserInfoVo extends UserInfo {
    @TableField(exist = false)
    @ApiModelProperty("用户角色信息")
    private List<UserRoleVo> userRoles;
    @TableField(exist = false)
    @ApiModelProperty("用户权限")
    private List<String> permissions;
    @TableField(exist = false)
    @ApiModelProperty("用户菜单")
    private List<String> menus;
}

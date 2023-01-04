package cn.com.mfish.common.oauth.api.vo;

import cn.com.mfish.common.oauth.api.entity.UserInfo;
import cn.com.mfish.common.oauth.api.entity.UserRole;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @author: mfish
 * @description: 用户信息Vo
 * @date: 2022/12/1 21:07
 */
@ApiModel("用户基础信息带权限")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserInfoVo extends UserInfo {
    @TableField(exist = false)
    @ApiModelProperty("用户角色信息")
    private List<UserRole> userRoles;
    @TableField(exist = false)
    @ApiModelProperty("用户权限")
    private Set<String> permissions;
}

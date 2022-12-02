package cn.com.mfish.oauth.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author qiufeng
 * @date 2020/2/18 17:48
 */
@ApiModel("用户基础信息")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserInfo extends BaseEntity<String> {
    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("昵称--用于显示")
    private String nickname;
    @ApiModelProperty("头像")
    private String headImgUrl;
    @ApiModelProperty("电话")
    private String telephone;
    @ApiModelProperty("生日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    @ApiModelProperty("性别(1男 0女)")
    private Integer sex;
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private Integer status;
    @ApiModelProperty(value = "删除标志（0正常 1删除）")
    private Integer delFlag;
    @TableField(exist = false)
    @ApiModelProperty("组织名称")
    private String orgName;
    @TableField(exist = false)
    @ApiModelProperty("组织ID")
    private String orgId;
    @TableField(exist = false)
    @ApiModelProperty("角色ID列表")
    private List<String> roleIds;
}

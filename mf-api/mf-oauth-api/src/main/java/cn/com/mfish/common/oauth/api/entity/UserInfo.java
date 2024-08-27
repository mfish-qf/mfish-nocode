package cn.com.mfish.common.oauth.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author: mfish
 * @date: 2020/2/18 17:48
 */
@Schema(description = "用户基础信息")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserInfo extends BaseEntity<String> {
    @Schema(description = "账号")
    private String account;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "昵称--用于显示")
    private String nickname;
    @Schema(description = "头像")
    private String headImgUrl;
    @Schema(description = "电话")
    private String telephone;
    @Schema(description = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    @Schema(description = "性别(1男 0女)")
    private Integer sex;
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
    @Schema(description = "删除标志（0正常 1删除）")
    private Integer delFlag;
    @Schema(description = "备注")
    private String remark;
    @TableField(exist = false)
    @Schema(description = "组织ID列表")
    private List<String> orgIds;
    @TableField(exist = false)
    @Schema(description="组织名称列表")
    private List<String> orgNames;
    @TableField(exist = false)
    @Schema(description = "角色ID列表")
    private List<String> roleIds;
    @TableField(exist = false)
    @Schema(description = "角色名称列表")
    private List<String> roleNames;
}

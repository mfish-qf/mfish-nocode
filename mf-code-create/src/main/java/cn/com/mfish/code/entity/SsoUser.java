package cn.com.mfish.code.entity;

import cn.com.mfish.code.req.ReqSsoUser;
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
 * @Description: 用户信息
 * @Author: mfish
 * @Date: 2022-09-01
 * @Version: V1.0
 */
@Data
@TableName("sso_user")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sso_user对象", description = "用户信息")
public class SsoUser extends ReqSsoUser {

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "")
	private String id;
    @ApiModelProperty(value = "账号")
	private String account;
    @ApiModelProperty(value = "手机号")
	private String phone;
    @ApiModelProperty(value = "邮箱")
	private String email;
    @ApiModelProperty(value = "密码")
	private String password;
    @ApiModelProperty(value = "旧密码")
	private String oldPassword;
    @ApiModelProperty(value = "昵称")
	private String nickname;
    @ApiModelProperty(value = "图片")
	private String headImgUrl;
    @ApiModelProperty(value = "电话")
	private String telephone;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "生日")
	private Date birthday;
    @ApiModelProperty(value = "性别")
	private Integer sex;
    @ApiModelProperty(value = "状态")
	private Integer status;
    @ApiModelProperty(value = "盐")
	private String salt;
    @ApiModelProperty(value = "微信唯一id")
	private String openid;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
}

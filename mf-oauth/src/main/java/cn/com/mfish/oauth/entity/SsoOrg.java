package cn.com.mfish.oauth.entity;

import cn.com.mfish.oauth.req.ReqSsoOrg;
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
 * @Description: 组织结构表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Data
@TableName("sso_org")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sso_org对象", description = "组织结构表")
public class SsoOrg extends ReqSsoOrg {

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "组织ID")
	private String id;
    @ApiModelProperty(value = "父组织ID")
	private String parentId;
    @ApiModelProperty(value = "组织编码")
	private String orgCode;
    @ApiModelProperty(value = "组织级别")
    private Integer orgLevel;
    @ApiModelProperty(value = "组织名称")
	private String orgName;
    @ApiModelProperty(value = "排序")
	private Integer orgSort;
    @ApiModelProperty(value = "负责人")
	private String leader;
    @ApiModelProperty(value = "联系电话")
	private String phone;
    @ApiModelProperty(value = "邮箱")
	private String email;
    @ApiModelProperty(value = "状态（0正常 1停用）")
	private String status;
    @ApiModelProperty(value = "删除标志（0正常 1删除）")
	private String delFlag;
    @ApiModelProperty(value = "创建用户")
	private String createBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
    @ApiModelProperty(value = "更新用户")
	private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
}

package cn.com.mfish.oauth.entity;

import cn.com.mfish.common.ds.entity.BaseTreeEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @Date: 2022-09-20
 * @Version: V1.0
 */
@Data
@TableName("sso_org")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "sso_org对象", description = "组织结构表")
public class SsoOrg extends BaseTreeEntity<String> {
    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty("组织ID")
    private String id;
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
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;
    @ApiModelProperty(value = "删除标志（0正常 1删除）")
    private String delFlag;
}

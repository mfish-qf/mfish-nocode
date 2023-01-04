package cn.com.mfish.oauth.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel("组织结构表请求参数")
public class ReqSsoOrg {
    @ApiModelProperty(value = "客户端ID")
    private String clientId;
    @ApiModelProperty(value = "组织名称")
    private String orgName;
    @ApiModelProperty(value = "负责人")
    private String leader;
    @ApiModelProperty(value = "联系电话")
    private String phone;
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;
}

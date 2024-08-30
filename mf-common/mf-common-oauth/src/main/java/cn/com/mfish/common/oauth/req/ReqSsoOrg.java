package cn.com.mfish.common.oauth.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 组织结构表
 * @Author: mfish
 * @date: 2022-09-20
 * @Version: V1.3.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "组织结构表请求参数")
public class ReqSsoOrg {
    @Schema(description = "租户ID")
    private String tenantId;
    @Schema(description = "组织名称")
    private String orgName;
    @Schema(description = "负责人")
    private String leader;
    @Schema(description = "联系电话")
    private String phone;
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;
}

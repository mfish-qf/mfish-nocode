package cn.com.mfish.oauth.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 租户信息表
 * @author: mfish
 * @date: 2023-05-31
 * @version: V2.1.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "租户信息表请求参数")
public class ReqSsoTenant {
    @Schema(description = "租户类型 0 个人 1 企业")
    private Integer tenantType;
    @Schema(description = "域名")
    private String domain;
    @Schema(description = "租户名称")
    private String name;
}

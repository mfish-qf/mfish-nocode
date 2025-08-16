package cn.com.mfish.oauth.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 客户端信息
 * @author: mfish
 * @date: 2023-05-12
 * @version: V2.1.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "客户端信息请求参数")
public class ReqSsoClientDetails {
    @Schema(description = "客户端名称")
    private String clientName;
    @Schema(description = "客户端ID")
    private String clientId;
}

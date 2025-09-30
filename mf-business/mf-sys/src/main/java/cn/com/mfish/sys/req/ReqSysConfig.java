package cn.com.mfish.sys.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 界面配置
 * @author: mfish
 * @date: 2023-03-07
 * @version: V2.2.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "界面配置请求参数")
public class ReqSysConfig {
    @Schema(description = "用户ID")
    private String userId;
}

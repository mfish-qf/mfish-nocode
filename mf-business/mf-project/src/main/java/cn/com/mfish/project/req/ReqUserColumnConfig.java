package cn.com.mfish.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description: 用户列配置保存请求
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.2
 */
@Data
@Schema(description = "用户列配置保存请求对象")
public class ReqUserColumnConfig {
    @Schema(description = "列配置JSON(含key/title/category/width/visible)")
    private String configJson;
}

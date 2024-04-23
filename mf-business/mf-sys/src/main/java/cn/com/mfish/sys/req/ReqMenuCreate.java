package cn.com.mfish.sys.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @description: 菜单创建请求
 * @author: mfish
 * @date: 2024/4/22
 */
@Data
public class ReqMenuCreate {
    @Schema(description = "代码生成id")
    private Long id;
    @Schema(description = "目录ID")
    private String parentId;
}

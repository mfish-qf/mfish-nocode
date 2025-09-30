package cn.com.mfish.common.sys.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 树形分类
 * @author: mfish
 * @date: 2024-03-12
 * @version: V2.2.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "树形分类请求参数")
public class ReqDictCategory {
    @Schema(description = "分类名称")
    private String categoryName;
    @Schema(description = "分类编码")
    private String categoryCode;
}

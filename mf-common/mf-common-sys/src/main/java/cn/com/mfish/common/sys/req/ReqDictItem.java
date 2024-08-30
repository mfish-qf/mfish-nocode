package cn.com.mfish.common.sys.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description: 字典项
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.3.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "字典项请求参数")
public class ReqDictItem {
    @Schema(description = "字典ID")
    private String dictId;
    @Schema(description = "字典编码")
    private String dictCode;
    @Schema(description = "字典标签")
    private String dictLabel;
    @Schema(description = "字典键值")
    private String dictValue;
    @Schema(description = "状态(0正常 1停用)")
    private Integer status;
}

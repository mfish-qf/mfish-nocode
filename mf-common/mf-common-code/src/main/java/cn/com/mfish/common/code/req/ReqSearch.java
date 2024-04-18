package cn.com.mfish.common.code.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 搜索请求
 * @author: mfish
 * @date: 2023/5/9 19:31
 */
@Schema(description = "查询条件")
@Data
@Accessors(chain = true)
public class ReqSearch {
    @Schema(description = "字段")
    private String field;
    @Schema(description = "查询条件")
    private String condition;
}

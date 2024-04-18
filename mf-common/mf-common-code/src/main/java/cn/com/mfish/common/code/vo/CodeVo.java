package cn.com.mfish.common.code.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: mfish
 * @description: 代码相关信息
 * @date: 2022/9/1 15:46
 */
@Data
@Accessors(chain = true)
@Schema(description = "代码相关信息")
public class CodeVo {
    @Schema(description = "代码存储路径")
    private String path;
    @Schema(description = "文件名称")
    private String name;
    @Schema(description = "生成代码")
    private String code;
}

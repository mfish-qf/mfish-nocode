package cn.com.mfish.code.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: mfish
 * @description: 代码相关信息
 * @date: 2022/9/1 15:46
 */
@Data
@Accessors(chain = true)
@ApiModel("代码相关信息")
public class CodeVo {
    @ApiModelProperty("代码存储路径")
    private String path;
    @ApiModelProperty("文件名称")
    private String name;
    @ApiModelProperty("生成代码")
    private String code;
}

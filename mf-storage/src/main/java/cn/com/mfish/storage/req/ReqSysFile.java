package cn.com.mfish.storage.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 文件存储
 * @author: mfish
 * @date: 2023-03-02
 * @version: V1.0.0
 */
@Data
@Accessors(chain = true)
@ApiModel("文件存储请求参数")
public class ReqSysFile {
    @ApiModelProperty(value = "文件名")
    private String fileName;
    @ApiModelProperty(value = "文件类型")
    private String fileType;
}

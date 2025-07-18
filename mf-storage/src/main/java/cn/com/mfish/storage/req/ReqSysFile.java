package cn.com.mfish.storage.req;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: 文件存储
 * @author: mfish
 * @date: 2023-03-02
 * @version: V2.0.1
 */
@Data
@Accessors(chain = true)
@Schema(description = "文件存储请求参数")
public class ReqSysFile {
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "文件类型")
    private String fileType;
    @Schema(description = "删除标签 1删除 0未删除")
    private String delFlag;
}

package cn.com.mfish.common.storage.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 文件存储
 * @author: mfish
 * @date: 2023-03-02
 * @version: V2.0.1
 */
@Data
@TableName("sys_storage")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "sys_storage对象 文件存储")
public class StorageInfo extends BaseEntity<String> {
    @Schema(description = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @Schema(description = "文件的唯一索引")
    private String fileKey;
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "文件类型")
    private String fileType;
    @Schema(description = "文件大小")
    private Integer fileSize;
    @Schema(description = "文件访问链接")
    private String fileUrl;
    @Schema(description = "存储路径")
    private String filePath;
    @Schema(description = "是否私密文件 0为公开的  1为私密文件")
    private Integer isPrivate;
    @Schema(description = "删除标记(0未删除1删除)")
    private Integer delFlag;
}

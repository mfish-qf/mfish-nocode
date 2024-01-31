package cn.com.mfish.common.storage.api.entity;

import cn.com.mfish.common.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @description: 文件存储
 * @author: mfish
 * @date: 2023-03-02
 * @version: V1.2.0
 */
@Data
@TableName("sys_storage")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "sys_storage对象", description = "文件存储")
public class StorageInfo extends BaseEntity<String> {
    @ApiModelProperty(value = "唯一ID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    @ApiModelProperty(value = "文件的唯一索引")
	private String fileKey;
    @ApiModelProperty(value = "文件名")
	private String fileName;
    @ApiModelProperty(value = "文件类型")
	private String fileType;
    @ApiModelProperty(value = "文件大小")
	private Integer fileSize;
    @ApiModelProperty(value = "文件访问链接")
	private String fileUrl;
    @ApiModelProperty(value = "存储路径")
	private String filePath;
    @ApiModelProperty(value = "是否私密文件 0为公开的  1为私密文件")
	private Integer isPrivate;
    @ApiModelProperty(value = "删除标记(0未删除1删除)")
	private Integer delFlag;
}

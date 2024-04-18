package cn.com.mfish.sys.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: 数据库树节点
 * @author: mfish
 * @date: 2023/4/12 20:10
 */
@Data
@Accessors(chain = true)
@Schema(description = "数据库树节点")
public class DBTreeNode implements Serializable {
    @Schema(description = "编码")
    private String code;
    @Schema(description = "父编码")
    private String parentCode;
    @Schema(description = "标签")
    private String label;
    @Schema(description = "节点类型 0 数据库 1表")
    private int type;
}

package cn.com.mfish.sys.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("数据库树节点")
public class DBTreeNode implements Serializable {
    @ApiModelProperty("编码")
    private String code;
    @ApiModelProperty("父编码")
    private String parentCode;
    @ApiModelProperty("标签")
    private String label;
    @ApiModelProperty("节点类型 0 数据库 1表")
    private int type;
}

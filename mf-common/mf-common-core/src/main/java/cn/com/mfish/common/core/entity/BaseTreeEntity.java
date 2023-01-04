package cn.com.mfish.common.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: mfish
 * @description: 基础树
 * @date: 2022/11/11 17:07
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel("基础树对象")
public class BaseTreeEntity<T> extends BaseEntity<T> {
    @ApiModelProperty("父节点ID")
    private T parentId;

    @ApiModelProperty("子节点")
    @TableField(exist = false)
    private List<BaseTreeEntity<T>> children;
}

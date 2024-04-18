package cn.com.mfish.common.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "基础树对象")
public class BaseTreeEntity<T> extends BaseEntity<T> {
    @Schema(description = "父节点ID")
    private T parentId;

    @Schema(description = "子节点")
    @TableField(exist = false)
    private List<BaseTreeEntity<T>> children;
}

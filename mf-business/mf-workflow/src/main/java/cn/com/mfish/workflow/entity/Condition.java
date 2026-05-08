package cn.com.mfish.workflow.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @description: 条件
 * @author: mfish
 * @date: 2026/4/30
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // 使用特定的名称来区分类型
        include = JsonTypeInfo.As.PROPERTY, // 包含在属性字段中
        property = "type" // 指定类型字段为 "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleCondition.class, name = "simple"),
        @JsonSubTypes.Type(value = ConditionGroup.class, name = "group")
})
public abstract class Condition {
    /** 条件类型（simple 或 group） */
    private String type;
}

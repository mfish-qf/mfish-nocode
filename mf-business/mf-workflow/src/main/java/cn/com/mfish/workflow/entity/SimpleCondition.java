package cn.com.mfish.workflow.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 简单条件
 * @author: mfish
 * @date: 2026/4/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleCondition extends Condition{
    private String variable;
    private String operator;
    private Object value;
    private String valueType;
}

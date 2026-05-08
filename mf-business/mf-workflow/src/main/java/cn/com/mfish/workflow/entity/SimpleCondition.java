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
    /** 条件变量名 */
    private String variable;
    /** 比较操作符（eq, ne, gt, lt, ge, le） */
    private String operator;
    /** 比较值 */
    private Object value;
    /** 值类型（variable 表示值为变量名，其他表示常量） */
    private String valueType;
}

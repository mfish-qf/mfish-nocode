package cn.com.mfish.workflow.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @description: 条件组合
 * @author: mfish
 * @date: 2026/4/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConditionGroup extends Condition{
    // 'AND' or 'OR'
    private String logic;
    private List<Condition> children;
}

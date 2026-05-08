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
    /** 条件组合逻辑关系（AND 或 OR） */
    private String logic;
    /** 子条件列表 */
    private List<Condition> children;
}

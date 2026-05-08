package cn.com.mfish.workflow.nodes;

import cn.com.mfish.workflow.enums.ApproveType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;

import java.util.List;

/**
 * @description: 审批节点
 * 支持单签、或签（OR）、会签（AND）以及按比例完成的会签模式
 * @author: mfish
 * @date: 2026/4/2
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "审批节点")
public class ApprovalNode extends BaseNode {
    @Schema(description = "候选用户列表")
    private List<String> candidateUsers;
    @Schema(description = "候选组列表")
    private List<String> candidateGroups;
    @Schema(description = "审批类型（OR 或签，AND 会签）")
    private ApproveType approvalType;
    @Schema(description = "会签完成比例（0-100 之间的整数，仅在 approvalType 为 PERCENT 时有效）")
    private Integer completionPercent;

    /**
     * 审批节点构造方法
     *
     * @param id            节点ID
     * @param name          节点名称
     * @param documentation 节点说明
     */
    public ApprovalNode(String id, String name, String documentation) {
        super(id, name, documentation);
    }

    /**
     * 创建用户任务节点，并根据审批类型初始化多实例配置
     *
     * @param sequenceFlows 所有连线列表
     * @return 用户任务节点
     */
    @Override
    protected FlowNode create(List<SequenceFlow> sequenceFlows) {
        UserTask node = new UserTask();
        initUserTask(node);
        return node;
    }

    /**
     * 初始化用户任务节点
     * 根据审批类型（会签/或签）配置不同的候选人策略和多实例特征
     *
     * @param userTask 用户任务对象
     */
    private void initUserTask(UserTask userTask) {
        // 1. 根据审批类型决定如何处理
        if (this.getApprovalType() == ApproveType.AND) {
            if (this.getCompletionPercent() == null || this.getCompletionPercent() == 100) {
                // 全部完成模式（100% 会签）
                initMultiInstance(userTask, null);
                // 会签模式下只需设置候选人列表（用于任务查询），assignee 会在多实例中动态设置
                setCandidateInfo(userTask);
            } else {
                // 按比例完成模式
                initMultiInstance(userTask, this.getCompletionPercent());
                // 会签模式下只需设置候选人列表（用于任务查询），assignee 会在多实例中动态设置
                setCandidateInfo(userTask);
            }

        } else {
            // 或签 (OR) 或无指定类型：普通单签模式
            // Flowable 中，当 UserTask 设置了多个候选人（candidateUsers 或 candidateGroups），
            // 默认就是或签模式：任意一个候选人审批通过后，流程即进入下一个节点
            setCandidateInfo(userTask);
        }
    }


    /**
     * 仅设置候选人信息（用于会签模式，assignee 由多实例动态设置）
     *
     * @param userTask 用户任务对象
     */
    private void setCandidateInfo(UserTask userTask) {
        // 会签模式下，assignee 会被设置为 ${elementVariable}，从集合变量中取值
        // 这里只设置候选人列表，用于任务查询和权限验证
        if (this.getCandidateUsers() != null && !this.getCandidateUsers().isEmpty()) {
            userTask.setCandidateUsers(this.getCandidateUsers());
        }
        if (this.getCandidateGroups() != null && !this.getCandidateGroups().isEmpty()) {
            userTask.setCandidateGroups(this.getCandidateGroups());
        }
    }

    /**
     * 初始化多实例配置
     *
     * @param userTask          用户任务对象
     * @param completionPercent 完成比例（null 表示全部完成）
     */
    private void initMultiInstance(UserTask userTask, Integer completionPercent) {
        MultiInstanceLoopCharacteristics mi = new MultiInstanceLoopCharacteristics();
        mi.setSequential(false); // 并行会签（多人同时审批）
        // 集合变量名和元素变量名基于节点 ID 生成，避免多个会签节点之间的变量冲突
        // 例如：节点 ID 为 "node1"，则变量名为 "node1_assigneeList" 和 "node1_assigneeItem"
        mi.setInputDataItem(this.getId() + "_assigneeList");
        String elementVariable = this.getId() + "_assigneeItem";
        mi.setElementVariable(elementVariable);
        // 会签模式下，UserTask 的办理人应指向多实例的变量
        userTask.setAssignee("${" + elementVariable + "}");
        // 设置完成条件
        if (completionPercent == null || completionPercent == 100) {
            // 全部完成：当已完成的数量等于总数量时完成
            mi.setCompletionCondition("${nrOfCompletedInstances >= nrOfInstances}");
        } else {
            // 按比例完成：当已完成的数量达到总数的指定比例时完成
            // 例如：completionPercent = 80，表示完成 80% 即可
            double percent = completionPercent / 100.0;
            // 使用表达式：已完成数 >= 总数 * 比例
            mi.setCompletionCondition("${nrOfCompletedInstances >= nrOfInstances * " + percent + "}");
        }

        userTask.setLoopCharacteristics(mi);
    }
}

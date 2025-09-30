package cn.com.mfish.common.workflow.service;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.*;
import cn.com.mfish.common.workflow.api.req.ReqProcess;
import cn.com.mfish.common.workflow.api.req.ReqTask;
import cn.com.mfish.common.workflow.enums.AuditOperator;

import java.util.List;

/**
 * @description: 工作流服务
 * @author: mfish
 * @date: 2025/9/16
 */
public interface FlowableService {

    /**
     * 部署流程定义
     *
     * @param name 流程定义文件名称 例如：test.bpmn20.xml文件名称传test
     */
    void deployProcess(String name);

    /**
     * 启动流程
     *
     * @param param 工作流参数
     * @param <T>   id类型
     * @return 流程实例id
     */
    <T> String startProcess(FlowableParam<T> param);

    /**
     * 查询流程实例列表
     *
     * @param reqProcess 查询参数
     * @param reqPage    分页参数
     * @return 流程实例列表
     */
    PageResult<MfProcess> getProcessList(ReqProcess reqProcess, ReqPage reqPage);

    /**
     * 查询历史流程实例列表
     *
     * @param reqProcess 查询参数
     * @param reqPage    分页参数
     * @return 流程实例列表
     */
    PageResult<MfProcess> getHistoryProcessList(ReqProcess reqProcess, ReqPage reqPage);

    /**
     * 查询流程实例是否存在
     *
     * @param reqProcess 查询参数
     * @return 是否存在
     */
    boolean existProcess(ReqProcess reqProcess);

    /**
     * 删除流程实例
     *
     * @param processInstanceId 流程实例id
     * @param businessKey       业务id
     * @param reason            删除原因
     */
    Result<String> delProcess(String processInstanceId, String businessKey, String reason);

    /**
     * 判断是否是流程启动人
     *
     * @param businessKey 业务id
     * @param userId      用户id
     * @return 是否是启动人
     */
    boolean isStarter(String businessKey, String userId);

    /**
     * 查询流程实例图片
     *
     * @param processInstanceId 流程实例id
     * @return 图片base64编码
     */
    String queryImage(String processInstanceId);

    /**
     * 查询任务列表
     *
     * @param processInstanceId 流程实例id
     * @return 任务列表
     */
    List<MfTask> getProcessTasks(String processInstanceId);

    /**
     * 查询历史任务列表
     *
     * @param processInstanceId 流程实例id
     * @param isHistory         是否查询历史任务
     * @return 任务列表
     */
    List<MfTask> getProcessTasks(String processInstanceId, boolean isHistory);


    /**
     * 查询当前用户待处理任务列表
     *
     * @param reqTask 查询参数
     * @param reqPage 分页参数
     * @return 待处理任务列表
     */
    PageResult<MfTask> getPendingTasks(ReqTask reqTask, ReqPage reqPage);

    /**
     * 完成任务
     *
     * @param flowOperator 操作人
     * @param approveInfo  审批信息
     */
    void completeTask(AuditOperator flowOperator, ApproveInfo approveInfo);

    /**
     * 查询审核评论列表
     *
     * @param processInstanceId 流程实例id
     * @return 审核评论列表
     */
    List<AuditComment> getAuditComments(String processInstanceId);

}

package cn.com.mfish.workflow.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.*;
import cn.com.mfish.common.workflow.api.enums.FlowKey;
import cn.com.mfish.common.workflow.api.req.ReqAllTask;
import cn.com.mfish.common.workflow.api.req.ReqProcess;
import cn.com.mfish.common.workflow.api.req.ReqTask;
import cn.com.mfish.common.workflow.common.Constants;
import cn.com.mfish.common.workflow.enums.AuditOperator;
import cn.com.mfish.common.workflow.service.FlowableService;
import cn.com.mfish.workflow.common.BpmnConverter;
import cn.com.mfish.workflow.common.FlowAuthority;
import cn.com.mfish.workflow.entity.FlowManage;
import cn.com.mfish.workflow.mapper.FlowManageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.engine.task.Comment;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 工作流服务实现类
 * @author: mfish
 * @date: 2025/9/16
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FlowableServiceImpl implements FlowableService {
    private final RuntimeService runtimeService;
    private final IdentityService identityService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;
    private final ProcessEngineConfiguration processEngineConfiguration;
    @Resource
    private FlowManageMapper flowManageMapper;

    /**
     * 项目启动后自动执行初始化流程任务
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void initFlow() {
        List<FlowManage> flowManageList = flowManageMapper.selectList(new LambdaQueryWrapper<FlowManage>()
                .eq(FlowManage::getReleased, 1)
                .eq(FlowManage::getDelFlag, 0));
        log.info("项目启动后自动执行初始化流程任务！");
        flowManageList.forEach(flowManage -> {
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(flowManage.getFlowKey())
                    .latestVersion()
                    .singleResult();
            // 如果流程定义不存在则部署
            if (pd == null) {
                try {
                    deployProcess(flowManage.getFlowKey(), flowManage.getName(), flowManage.getRemark(), flowManage.getFlowConfig());
                } catch (Exception ex) {
                    log.error("部署流程{}失败", flowManage.getFlowKey(), ex);
                }
            } else {
                log.info("流程{}已存在，无需部署", flowManage.getFlowKey());
            }
        });
        log.info("项目启动后自动执行初始化流程任务完成！");
    }

    @Override
    public Integer deployProcess(String id) {
        FlowManage flowManage = flowManageMapper.selectById(id);
        if (flowManage == null) {
            throw new MyRuntimeException("错误:流程配置不存在");
        }
        return deployProcess(flowManage.getFlowKey(), flowManage.getName(), flowManage.getRemark(), flowManage.getFlowConfig());
    }

    @Override
    public Integer deployProcess(String flowKey, String name, String desc, String flowConfig) {
        if (StringUtils.isEmpty(flowKey) || StringUtils.isEmpty(flowConfig)) {
            throw new MyRuntimeException("错误:流程key或流程配置为空");
        }
        BpmnModel bpmnModel = BpmnConverter.convertToBpmn(flowKey, name, desc, flowConfig);
        Deployment deployment = repositoryService.createDeployment()
                .name(flowKey)
                .key(flowKey)
                .addBpmnModel(flowKey + ".bpmn20.xml", bpmnModel)
                .enableDuplicateFiltering()
                .deploy();
        log.info("流程{}部署成功", flowKey);
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        return processDefinition.getVersion();
    }

    @Override
    public void deleteDeploy(String flowKey, Integer version) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(flowKey);
        if (version != null) {
            query.processDefinitionVersion(version);
        }
        long count = query.count();
        if (count > 0) {
            throw new MyRuntimeException("错误:还有未完成的流程，请完成审批后再撤回");
        }
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(flowKey);
        if (version != null) {
            processDefinitionQuery.processDefinitionVersion(version);
        }
        List<ProcessDefinition> list = processDefinitionQuery.list();
        if (list == null || list.isEmpty()) {
            return;
        }
        try {
            list.forEach(pd -> repositoryService.deleteDeployment(pd.getDeploymentId()));
        } catch (Exception ex) {
            log.error("删除流程部署失败", ex);
            throw new MyRuntimeException("删除流程部署失败");
        }
    }

    @Override
    public <T> String startProcess(FlowableParam<T> param) {
        if (param.getKey() == null) {
            log.error("错误：流程定义key不能为空");
            throw new MyRuntimeException("错误：流程定义key不能为空");
        }
        if (StringUtils.isEmpty(param.getId().toString())) {
            log.error("错误：业务id不能为空");
            throw new MyRuntimeException("错误：业务id不能为空");
        }
        if (existProcess(new ReqProcess().setFlowKey(param.getKey())
                .setBusinessKey(param.getId().toString()))) {
            log.error("错误：流程实例已存在");
            throw new MyRuntimeException("错误：流程实例已存在");
        }
        // 设置启动人 如果未指定启动人，则默认使用当前登录用户 否则使用指定的启动人
        if (StringUtils.isEmpty(param.getStartAccount())) {
            String account = AuthInfoUtils.getCurrentAccount();
            identityService.setAuthenticatedUserId(account);
            param.setStartAccount(account);
        } else {
            identityService.setAuthenticatedUserId(param.getStartAccount());
        }
        // 设置流程变量
        try {
            ProcessInstance pi = runtimeService.startProcessInstanceByKey(param.getKey(), param.getId().toString()
                    , Map.of(Constants.WORKFLOW_PARAM, param, Constants.PROCESS_START_ACCOUNT, param.getStartAccount()));
            // 清理上下文，避免线程污染
            identityService.setAuthenticatedUserId(null);
            return pi.getId();
        } catch (FlowableObjectNotFoundException ex) {
            log.error("错误：当前审批流程不存在{}", param);
            throw new MyRuntimeException("错误：当前审批流程不存在");
        } catch (Exception ex) {
            log.error("错误：启动流程失败{}", param, ex);
            throw new MyRuntimeException("错误：启动流程失败");
        }
    }

    @Override
    public PageResult<MfProcess> getProcessList(ReqProcess reqProcess, ReqPage reqPage) {
        if (FlowKey.UNKNOWN.toString().equals(reqProcess.getFlowKey())) {
            log.error("错误：流程定义key不能为空");
            throw new MyRuntimeException("错误：流程定义key不能为空");
        }
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().includeProcessVariables();
        if (StringUtils.isNotEmpty(reqProcess.getFlowKey())) {
            query.processDefinitionKey(reqProcess.getFlowKey());
        }
        if (reqProcess.getProcessInstanceIds() != null && !reqProcess.getProcessInstanceIds().isEmpty()) {
            query.processInstanceIds(Set.copyOf(reqProcess.getProcessInstanceIds()));
        }
        if (StringUtils.isNotEmpty(reqProcess.getBusinessKey())) {
            query.processInstanceBusinessKey(reqProcess.getBusinessKey());
        }
        int first = (reqPage.getPageNum() - 1) * reqPage.getPageSize();
        List<ProcessInstance> processList = query.orderByStartTime().desc().listPage(first, reqPage.getPageSize());
        if (processList == null || processList.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), reqPage.getPageNum(), reqPage.getPageSize(), 0);
        }
        return new PageResult<>(processList.stream().map(process -> {
                    MfProcess mfProcess = new MfProcess()
                            .setProcessDefinitionId(process.getProcessDefinitionId())
                            .setProcessDefinitionName(process.getProcessDefinitionName())
                            .setProcessDefinitionKey(process.getProcessDefinitionKey())
                            .setProcessInstanceId(process.getId())
                            .setDeploymentId(process.getDeploymentId())
                            .setStartUserId(process.getStartUserId())
                            .setStartTime(process.getStartTime());
                    FlowableParam<?> flowableParam = (FlowableParam<?>) process.getProcessVariables().get(Constants.WORKFLOW_PARAM);
                    if (flowableParam != null && flowableParam.getId() != null) {
                        mfProcess.setBusinessKey(flowableParam.getId().toString());
                    }
                    return mfProcess;
                }
        ).collect(Collectors.toList()), reqPage.getPageNum(), reqPage.getPageSize(), query.count());
    }

    @Override
    public PageResult<MfProcess> getHistoryProcessList(ReqProcess reqProcess, ReqPage reqPage) {
        if (FlowKey.UNKNOWN.toString().equals(reqProcess.getFlowKey())) {
            log.error("错误：流程定义key不能为空");
            throw new MyRuntimeException("错误：流程定义key不能为空");
        }
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery().includeProcessVariables();
        if (StringUtils.isNotEmpty(reqProcess.getFlowKey())) {
            query.processDefinitionKey(reqProcess.getFlowKey());
        }
        if (reqProcess.getProcessInstanceIds() != null && !reqProcess.getProcessInstanceIds().isEmpty()) {
            query.processInstanceIds(Set.copyOf(reqProcess.getProcessInstanceIds()));
        }
        if (StringUtils.isNotEmpty(reqProcess.getBusinessKey())) {
            query.processInstanceBusinessKey(reqProcess.getBusinessKey());
        }
        int first = (reqPage.getPageNum() - 1) * reqPage.getPageSize();
        List<HistoricProcessInstance> processList = query.orderByProcessInstanceStartTime().desc().listPage(first, reqPage.getPageSize());
        if (processList == null || processList.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), reqPage.getPageNum(), reqPage.getPageSize(), 0);
        }
        return new PageResult<>(processList.stream().map(process -> {
                    MfProcess mfProcess = new MfProcess()
                            .setProcessDefinitionId(process.getProcessDefinitionId())
                            .setProcessDefinitionName(process.getProcessDefinitionName())
                            .setProcessDefinitionKey(process.getProcessDefinitionKey())
                            .setProcessInstanceId(process.getId())
                            .setDeploymentId(process.getDeploymentId())
                            .setStartUserId(process.getStartUserId())
                            .setStartTime(process.getStartTime())
                            .setEndTime(process.getEndTime())
                            .setDeleteReason(process.getDeleteReason());
                    FlowableParam<?> flowableParam = (FlowableParam<?>) process.getProcessVariables().get(Constants.WORKFLOW_PARAM);
                    if (flowableParam != null && flowableParam.getId() != null) {
                        mfProcess.setBusinessKey(flowableParam.getId().toString());
                    }
                    return mfProcess;
                }
        ).collect(Collectors.toList()), reqPage.getPageNum(), reqPage.getPageSize(), query.count());
    }

    @Override
    public boolean existProcess(ReqProcess reqProcess) {
        return getProcessList(reqProcess, new ReqPage()).getTotal() > 0;
    }


    @Override
    @Transactional
    public Result<String> delProcess(String processInstanceId, String businessKey, String reason) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        if (StringUtils.isNotEmpty(processInstanceId)) {
            query.processInstanceId(processInstanceId);
        }
        if (StringUtils.isNotEmpty(businessKey)) {
            query.processInstanceBusinessKey(businessKey);
        }
        ProcessInstance pi = query.singleResult();
        // 流程实例不存在，直接返回
        if (pi == null) {
            return Result.ok(businessKey, "错误：流程不存在");
        }
        String curAccount = AuthInfoUtils.getCurrentAccount();
        // 非超管，且流程实例不存在，或流程实例启动人不是当前用户，抛出异常
        if (!AuthInfoUtils.isSuper() && (curAccount == null || !curAccount.equals(pi.getStartUserId()))) {
            log.error("错误：该用户无权限终止流程");
            throw new MyRuntimeException("错误：该用户无权限删除流程");
        }
        try {
            runtimeService.deleteProcessInstance(pi.getProcessInstanceId(), AuditOperator.终止.getValue() + ":" + curAccount + "终止了流程，原因：" + reason);
        } catch (Exception e) {
            throw new MyRuntimeException("错误：删除流程失败");
        }
        return Result.ok(businessKey, "删除流程成功");
    }

    /**
     * 判断用户是否为流程启动人
     *
     * @param businessKey 业务id
     * @param userId      用户id
     * @return 是否为启动人
     */
    @Override
    public boolean isStarter(String businessKey, String userId) {
        List<HistoricProcessInstance> hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .orderByProcessInstanceStartTime().desc()
                .orderByProcessInstanceEndTime().desc()
                .list();
        return hpi != null && !hpi.isEmpty() && userId != null && userId.equals(hpi.getFirst().getStartUserId());
    }

    /**
     * 获取流程实例任务列表
     *
     * @param processInstanceId 流程实例id
     * @return 返回任务列表
     */
    @Override
    public List<MfTask> getProcessTasks(String processInstanceId) {
        return getProcessTasks(processInstanceId, false);
    }

    /**
     * 查询流程实例图片
     *
     * @param processInstanceId 流程实例id
     * @return 图片base64编码
     */
    @Override
    public String queryImage(String processInstanceId) {
        HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (pi == null) {
            log.error("错误：流程实例不存在，实例id:{}", processInstanceId);
            throw new MyRuntimeException("错误：流程实例不存在");
        }
        String processDefinitionId = pi.getProcessDefinitionId();
        if (StringUtils.isEmpty(processDefinitionId)) {
            log.error("错误：流程定义ID不能为空");
            throw new MyRuntimeException("错误：流程定义ID不能为空");
        }

        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        List<String> activeActivityIds = new ArrayList<>();
        for (HistoricTaskInstance hti : htiList) {
            if (hti.getEndTime() == null) {
                activeActivityIds.add(hti.getTaskDefinitionKey());
            }
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        if (bpmnModel == null) {
            log.error("错误：流程定义不存在，流程定义ID：{}", processDefinitionId);
            throw new MyRuntimeException("错误：流程定义不存在");
        }
        return buildFlowImage(bpmnModel, activeActivityIds);
    }

    /**
     * 构建流程实例图片
     *
     * @param bpmnModel         流程定义模型
     * @param activeActivityIds 活动id列表
     * @return 返回图片base64编码
     */
    public String buildFlowImage(BpmnModel bpmnModel, List<String> activeActivityIds) {
        if (bpmnModel == null) {
            throw new MyRuntimeException("错误：流程定义不存在");
        }
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        ProcessDiagramGenerator diagramGenerator = processEngine.getProcessEngineConfiguration()
                .getProcessDiagramGenerator();
        // 获取流程图资源流
        try (InputStream imageStream = diagramGenerator.generateDiagram(
                bpmnModel,
                "jpg",
                activeActivityIds,
                new ArrayList<>(),  // 高亮连线ID
                "宋体",
                "宋体",
                "宋体",
                processEngine.getProcessEngineConfiguration().getClassLoader(),
                2.0,
                true
        )) {
            if (imageStream == null) {
                log.error("错误：资源流为空");
                throw new MyRuntimeException("错误：资源流为空");
            }
            // 将资源流内容读取为字节数组并进行Base64编码
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = imageStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (Exception e) {
            // 捕获其他未知异常并记录日志
            log.error("错误：未知异常", e);
            throw new MyRuntimeException("错误：未知异常", e);
        }
    }

    /**
     * 获取任务列表
     *
     * @param processInstanceId 流程实例id
     * @param isHistory         是否只查询历史任务
     * @return 任务列表
     */
    @Override
    public List<MfTask> getProcessTasks(String processInstanceId, boolean isHistory) {
        Result<Map<String, MfTask>> hisTaskMap = getHistoryTaskMap(processInstanceId);
        if (!hisTaskMap.isSuccess()) {
            throw new MyRuntimeException(hisTaskMap.getMsg());
        }
        Map<String, String> activeTasks = taskService.createTaskQuery()
                .taskIds(hisTaskMap.getData().values().stream().map(MfTask::getId).collect(Collectors.toList()))
                .list().stream().collect(Collectors.toMap(Task::getTaskDefinitionKey, Task::getState));
        BpmnModel bpmnModel = repositoryService.getBpmnModel(hisTaskMap.getMsg());
        List<MfTask> mfTasks = new ArrayList<>();
        for (org.flowable.bpmn.model.Process process : bpmnModel.getProcesses()) {
            Collection<FlowElement> flowElements = process.getFlowElements();
            if (flowElements == null || flowElements.isEmpty()) {
                continue;
            }
            for (FlowElement fe : flowElements) {
                if (!(fe instanceof UserTask userTask)) {
                    continue;
                }
                MfTask mfTask;
                //历史任务设置状态
                if (hisTaskMap.getData().containsKey(userTask.getId())) {
                    mfTask = hisTaskMap.getData().get(userTask.getId());
                    // 历史任务存在，更新状态
                    if (activeTasks.containsKey(userTask.getId())) {
                        mfTask.setStatus(activeTasks.get(userTask.getId()));
                    }
                } else if (isHistory) {
                    continue;
                } else {
                    mfTask = new MfTask();
                }
                mfTask.setProcessInstanceId(processInstanceId)
                        .setTaskName(userTask.getName())
                        .setDescription(userTask.getDocumentation())
                        .setCandidateUsers(userTask.getCandidateUsers())
                        .setCandidateGroups(userTask.getCandidateGroups())
                        .setFormKey(userTask.getFormKey());
                mfTasks.add(mfTask);
            }
        }
        return mfTasks;
    }

    @Override
    public PageResult<MfTask> getPendingTasks(ReqTask reqTask, ReqPage reqPage) {
        TaskQuery query = FlowAuthority.getAuthTaskQuery(taskService);
        if (StringUtils.isNotBlank(reqTask.getTaskName())) {
            query.taskNameLike("%" + reqTask.getTaskName() + "%");
        }
        if (reqTask.getApplyStartTime() != null) {
            query.taskCreatedAfter(reqTask.getApplyStartTime());
        }
        if (reqTask.getApplyEndTime() != null) {
            query.taskCreatedBefore(reqTask.getApplyEndTime());
        }
        if (StringUtils.isNotBlank(reqTask.getProcessDefinitionKey())) {
            query.processDefinitionKey(reqTask.getProcessDefinitionKey());
        }
        if (StringUtils.isNotBlank(reqTask.getProcessInstanceId())) {
            query.processInstanceId(reqTask.getProcessInstanceId());
        }
        if (StringUtils.isNotBlank(reqTask.getStartAccount())) {
            query.processVariableValueLikeIgnoreCase(Constants.PROCESS_START_ACCOUNT, "%" + reqTask.getStartAccount() + "%");
        }
        if (StringUtils.isNotBlank(reqTask.getAssignee())) {
            query.taskAssignee(reqTask.getAssignee());
        }
        query.orderByTaskCreateTime().desc();
        int first = (reqPage.getPageNum() - 1) * reqPage.getPageSize();
        List<Task> list = query.listPage(first, reqPage.getPageSize());
        List<MfTask> mfTasks = new ArrayList<>();
        for (Task task : list) {
            MfTask mfTask = new MfTask()
                    .setId(task.getId())
                    .setTaskName(task.getName())
                    .setProcessInstanceId(task.getProcessInstanceId())
                    .setProcessDefinitionId(task.getProcessDefinitionId())
                    .setAssignee(task.getAssignee())
                    .setStartTime(task.getCreateTime())
                    .setStatus(task.getState())
                    .setStartTime(task.getCreateTime())
                    .setDescription(task.getDescription())
                    .setFormKey(task.getFormKey());
            setProcessKey(task.getProcessVariables(), mfTask);
            mfTasks.add(mfTask);
        }
        return new PageResult<>(mfTasks, reqPage.getPageNum(), reqPage.getPageSize(), query.count());

    }

    @Override
    public PageResult<MfTask> getAllTasks(ReqAllTask reqAllTask, ReqPage reqPage) {
        FlowAuthority auth = FlowAuthority.getFlowAuthority();
        HistoricTaskInstanceQuery query = supplyHistoricTaskInstanceQuery(auth, reqAllTask);
        return buildPageResult(query, reqPage);
    }

    @Override
    public PageResult<MfTask> getApplyTasks(ReqAllTask reqAllTask, ReqPage reqPage) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .processVariableValueEquals(Constants.PROCESS_START_ACCOUNT, AuthInfoUtils.getCurrentAccount());
        query = supplyHistoricTaskInstanceQuery(query, reqAllTask);
        return buildPageResult(query, reqPage);
    }

    private PageResult<MfTask> buildPageResult(HistoricTaskInstanceQuery query, ReqPage reqPage) {
        int first = (reqPage.getPageNum() - 1) * reqPage.getPageSize();
        List<HistoricTaskInstance> list = query.listPage(first, reqPage.getPageSize());
        List<MfTask> mfTasks = new ArrayList<>();
        for (HistoricTaskInstance his : list) {
            MfTask mfTask = buildMfTask(his);
            mfTasks.add(mfTask);
        }
        return new PageResult<>(mfTasks, reqPage.getPageNum(), reqPage.getPageSize(), query.count());
    }

    /**
     * 构建MfTask对象，从历史任务实例中提取任务信息
     *
     * @param his 历史任务实例
     * @return 构建的MfTask对象
     */
    private MfTask buildMfTask(HistoricTaskInstance his) {
        MfTask mfTask = new MfTask()
                .setId(his.getId())
                .setTaskName(his.getName())
                .setProcessInstanceId(his.getProcessInstanceId())
                .setProcessDefinitionId(his.getProcessDefinitionId())
                .setAssignee(his.getAssignee())
                .setStartTime(his.getCreateTime())
                .setStatus(getState(his))
                .setStartTime(his.getCreateTime())
                .setDescription(his.getDescription())
                .setDeleteReason(his.getDeleteReason())
                .setFormKey(his.getFormKey())
                .setEndTime(his.getEndTime());
        setProcessKey(his.getProcessVariables(), mfTask);
        return mfTask;
    }

    /**
     * 获取任务状态，已完成任务如果有删除原因则状态为已取消
     *
     * @param task 历史任务实例
     * @return 任务状态
     */
    private String getState(HistoricTaskInstance task) {
        String state = task.getState();
        if (Task.COMPLETED.equals(task.getState()) && StringUtils.isNotEmpty(task.getDeleteReason())) {
            state = Task.TERMINATED;
        }
        return state;
    }

    @Override
    public TaskTotal getTaskTotal(ReqTask reqTask) {
        FlowAuthority auth = FlowAuthority.getFlowAuthority();
        ReqAllTask reqAllTask = new ReqAllTask();
        BeanUtils.copyProperties(reqTask, reqAllTask);
        long todoCount = supplyHistoricTaskInstanceQuery(auth, reqAllTask.setStatus(Task.CREATED)).count();
        long completedCount = supplyHistoricTaskInstanceQuery(auth, reqAllTask.setStatus(Task.COMPLETED)).count();
        long cancelledCount = supplyHistoricTaskInstanceQuery(auth, reqAllTask.setStatus(Task.TERMINATED)).count();
        return new TaskTotal(todoCount, completedCount, cancelledCount);
    }

    /**
     * 补充历史任务查询，根据流程用户权限添加查询条件
     *
     * @param auth       流程用户权限
     * @param reqAllTask 查询参数
     * @return 补充后的历史任务查询
     */
    private HistoricTaskInstanceQuery supplyHistoricTaskInstanceQuery(FlowAuthority auth, ReqAllTask reqAllTask) {
        HistoricTaskInstanceQuery query = FlowAuthority.getAuthHistoricTaskQuery(auth, historyService);
        return supplyHistoricTaskInstanceQuery(query, reqAllTask);
    }

    /**
     * 补充历史任务查询，根据查询参数添加查询条件
     *
     * @param query      历史任务查询
     * @param reqAllTask 查询参数
     * @return 补充后的历史任务查询
     */
    private HistoricTaskInstanceQuery supplyHistoricTaskInstanceQuery(HistoricTaskInstanceQuery query, ReqAllTask reqAllTask) {
        if (StringUtils.isNotBlank(reqAllTask.getTaskName())) {
            query.taskNameLike("%" + reqAllTask.getTaskName() + "%");
        }
        if (StringUtils.isNotBlank(reqAllTask.getProcessDefinitionKey())) {
            query.processDefinitionKey(reqAllTask.getProcessDefinitionKey());
        }
        if (reqAllTask.getApplyStartTime() != null) {
            query.taskCreatedAfter(reqAllTask.getApplyStartTime());
        }
        if (reqAllTask.getApplyEndTime() != null) {
            query.taskCreatedBefore(reqAllTask.getApplyEndTime());
        }
        if (reqAllTask.getAuditStartTime() != null) {
            query.taskCompletedAfter(reqAllTask.getAuditStartTime());
        }
        if (reqAllTask.getAuditEndTime() != null) {
            query.taskCompletedBefore(reqAllTask.getAuditEndTime());
        }
        if (StringUtils.isNotBlank(reqAllTask.getStartAccount())) {
            query.processVariableValueLikeIgnoreCase(Constants.PROCESS_START_ACCOUNT, "%" + reqAllTask.getStartAccount() + "%");
        }
        if (StringUtils.isNotBlank(reqAllTask.getAssignee())) {
            query.taskAssignee(reqAllTask.getAssignee());
        }
        if (reqAllTask.getStatus() != null) {
            if (Task.COMPLETED.equals(reqAllTask.getStatus())) {
                query.finished().or().taskWithoutDeleteReason().taskDeleteReason(Task.COMPLETED).endOr();
            } else if (Task.TERMINATED.equals(reqAllTask.getStatus())) {
                query.finished().or()
                        .taskDeleteReasonLike(Task.TERMINATED + ":%")
                        .endOr();
            } else {
                query.unfinished();
            }
        }
        return query.orderByTaskCreateTime().desc();
    }

    @Override
    @Transactional
    public void completeTask(AuditOperator flowOperator, ApproveInfo approveInfo) {
        FlowAuthority auth = FlowAuthority.getFlowAuthority();
        Task task = FlowAuthority.getAuthTaskQuery(auth, taskService).taskId(approveInfo.getTaskId()).active().singleResult();
        if (task == null) {
            throw new MyRuntimeException("错误：任务不存在或无权限");
        }
        identityService.setAuthenticatedUserId(auth.getAccount());
        taskService.setAssignee(approveInfo.getTaskId(), auth.getAccount());
        taskService.addComment(approveInfo.getTaskId(), task.getProcessInstanceId(), "[" + flowOperator.name() + "]" + approveInfo.getMessage());
        taskService.complete(approveInfo.getTaskId(), auth.getAccount(), Map.of(Constants.AUDIT_TYPE, flowOperator.getValue(), Constants.AUDIT_COMMENT, approveInfo.getMessage()));
        identityService.setAuthenticatedUserId(null);
    }

    @Override
    public List<AuditComment> getAuditComments(String processInstanceId) {
        List<HistoricTaskInstance> history = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime().asc()
                .list();
        if (history == null || history.isEmpty()) {
            return new ArrayList<>();
        }
        List<AuditComment> auditComments = new ArrayList<>();
        for (HistoricTaskInstance his : history) {
            if (StringUtils.isNotEmpty(his.getId())) {
                List<Comment> comments = taskService.getTaskComments(his.getId());
                if (comments != null && !comments.isEmpty()) {
                    auditComments.addAll(buildAuditComment(comments, his));
                }
            }
        }
        return auditComments;
    }

    /**
     * 获取历史任务map
     *
     * @param processInstanceId 流程实例id
     * @return 成功：历史任务map msg返回流程定义id 失败：msg返回错误信息
     */
    private Result<Map<String, MfTask>> getHistoryTaskMap(String processInstanceId) {
        if (processInstanceId == null) {
            return Result.fail("错误：实例id不能为空");
        }
        List<HistoricTaskInstance> history = historyService
                .createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime().asc()
                .list();
        if (history == null || history.isEmpty()) {
            return Result.fail("错误：历史任务不存在");
        }
        Map<String, MfTask> taskMap = new LinkedHashMap<>();
        String processDefinitionId = null;
        for (HistoricTaskInstance his : history) {
            processDefinitionId = his.getProcessDefinitionId();
            MfTask mfTask = buildMfTask(his);
            if (StringUtils.isNotEmpty(his.getId())) {
                List<Comment> comments = taskService.getTaskComments(his.getId());
                if (comments != null && !comments.isEmpty()) {
                    // 如果任务已存在，添加之前的审批意见
                    if (taskMap.containsKey(his.getTaskDefinitionKey())) {
                        List<AuditComment> list = taskMap.get(his.getTaskDefinitionKey()).getComments();
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.addAll(buildAuditComment(comments, his));
                        mfTask.setComments(list);
                    } else {
                        mfTask.setComments(buildAuditComment(comments, his));
                    }
                }
            }
            taskMap.put(his.getTaskDefinitionKey(), mfTask);
        }
        return Result.ok(taskMap, processDefinitionId);
    }

    private void setProcessKey(Map<String, Object> variables, MfTask mfTask) {
        if (variables == null) {
            return;
        }
        FlowableParam<?> flowableParam = (FlowableParam<?>) variables.get(Constants.WORKFLOW_PARAM);
        if (flowableParam != null) {
            mfTask.setProcessDefinitionKey(flowableParam.getKey());
            mfTask.setProcessName(FlowKey.getByKey(flowableParam.getKey()).name());
            mfTask.setStartAccount(flowableParam.getStartAccount());
            if (flowableParam.getId() != null) {
                mfTask.setBusinessKey(flowableParam.getId().toString());
            }
        }
    }

    /**
     * 构建审批意见
     *
     * @param comments 审批意见
     * @param his      历史任务
     * @return 审批意见列表
     */
    private List<AuditComment> buildAuditComment(List<Comment> comments, HistoricTaskInstance his) {
        return comments.stream().map(comment ->
                new AuditComment()
                        .setTaskId(his.getId())
                        .setProcessInstanceId(his.getProcessInstanceId())
                        .setProcessDefinitionId(his.getProcessDefinitionId())
                        .setTaskName(his.getName())
                        .setAssignee(comment.getUserId())
                        .setComment(comment.getFullMessage())
                        .setTime(comment.getTime())
                        .setType(comment.getType())
        ).collect(Collectors.toList());
    }

}

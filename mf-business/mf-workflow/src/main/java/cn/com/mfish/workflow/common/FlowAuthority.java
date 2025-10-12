package cn.com.mfish.workflow.common;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.common.OauthUtils;
import lombok.Data;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 流程用户权限
 * @author: mfish
 * @date: 2025/9/29
 */
@Data
public class FlowAuthority {

    private String account;
    private List<String> groupList;

    /**
     * 获取流程用户权限
     *
     * @return 流程用户权限
     */
    public static FlowAuthority getFlowAuthority() {
        FlowAuthority flowAuthority = new FlowAuthority();
        flowAuthority.setAccount(AuthInfoUtils.getCurrentAccount());
        List<SsoOrg> orgList = OauthUtils.getOrgs(TreeDirection.当前.toString());
        if (orgList != null && !orgList.isEmpty()) {
            flowAuthority.setGroupList(orgList.stream().map(SsoOrg::getOrgFixCode)
                    .filter(StringUtils::isNotEmpty).collect(Collectors.toList()));
        }
        return flowAuthority;
    }

    /**
     * 获取权限任务查询
     *
     * @param taskService 任务服务
     * @return 权限任务查询
     */
    public static TaskQuery getAuthTaskQuery(TaskService taskService) {
        FlowAuthority auth = FlowAuthority.getFlowAuthority();
        return getAuthTaskQuery(auth, taskService);
    }

    /**
     * 获取权限任务查询
     *
     * @param flowAuthority 流程用户权限
     * @param taskService   任务服务
     * @return 权限任务查询
     */
    public static TaskQuery getAuthTaskQuery(FlowAuthority flowAuthority, TaskService taskService) {
        TaskQuery query = taskService.createTaskQuery().includeProcessVariables();
        if (!AuthInfoUtils.isSuper()) {
            query.or()
                    .taskCandidateGroupIn(flowAuthority.getGroupList())
                    .taskCandidateOrAssigned(flowAuthority.getAccount())
                    .endOr();
        }
        return query;
    }

    /**
     * 获取权限历史任务查询
     *
     * @param historyService 历史服务
     * @return 权限历史任务查询
     */
    public static HistoricTaskInstanceQuery getAuthHistoricTaskQuery(HistoryService historyService) {
        FlowAuthority auth = FlowAuthority.getFlowAuthority();
        return getAuthHistoricTaskQuery(auth, historyService);
    }

    /**
     * 获取权限历史任务查询
     *
     * @param flowAuthority  流程用户权限
     * @param historyService 历史服务
     * @return 权限历史任务查询
     */
    public static HistoricTaskInstanceQuery getAuthHistoricTaskQuery(FlowAuthority flowAuthority, HistoryService historyService) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().includeProcessVariables();
        if (!AuthInfoUtils.isSuper()) {
            query = query.or();
            if (flowAuthority.getGroupList() != null && !flowAuthority.getGroupList().isEmpty()) {
                query.taskCandidateGroupIn(flowAuthority.getGroupList());
            }
            if (StringUtils.isNotEmpty(flowAuthority.getAccount())) {
                query.taskAssignee(flowAuthority.getAccount())
                        .taskCandidateUser(flowAuthority.getAccount());
            }
            query.endOr();
        }
        return query;
    }
}

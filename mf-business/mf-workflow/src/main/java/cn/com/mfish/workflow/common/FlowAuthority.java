package cn.com.mfish.workflow.common;

import cn.com.mfish.common.core.enums.TreeDirection;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.common.OauthUtils;
import lombok.Data;
import org.flowable.engine.TaskService;
import org.flowable.task.api.TaskQuery;

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
        TaskQuery query = taskService.createTaskQuery();
        if (!AuthInfoUtils.isSuper()) {
            query.or()
                    .taskCandidateGroupIn(flowAuthority.getGroupList())
                    .taskCandidateOrAssigned(flowAuthority.getAccount())
                    .includeProcessVariables()
                    .endOr();
        }
        return query;
    }
}

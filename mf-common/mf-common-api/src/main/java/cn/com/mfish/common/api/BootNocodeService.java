package cn.com.mfish.common.api;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.nocode.api.remote.RemoteNocodeService;
import cn.com.mfish.common.nocode.service.ScreenResourceImgService;
import cn.com.mfish.common.nocode.service.ScreenResourceService;
import cn.com.mfish.common.workflow.api.enums.FlowKey;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @description: 无代码接口单实例实现类
 * @author: mfish
 * @date: 2025/9/28
 */
@Service("remoteNocodeService")
public class BootNocodeService implements RemoteNocodeService {

    @Resource
    ScreenResourceService screenResourceService;
    @Resource
    ScreenResourceImgService screenResourceImgService;

    @Override
    public Result<String> approved(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return audit(prefix, id, 1, result);
    }

    @Override
    public Result<String> rejected(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return audit(prefix, id, 2, result);
    }

    @Override
    public Result<String> canceled(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return audit(prefix, id, null, result);
    }

    /**
     * 按流程业务前缀分发审批回调到对应资源服务
     *
     * @param prefix     业务前缀（来自流程变量 FlowableParam）
     * @param id         业务ID
     * @param auditState 审核状态（1：通过，2：驳回，null：撤销）
     * @param result     工作流审批结果
     * @return 审核结果
     */
    private Result<String> audit(String prefix, String id, Integer auditState, WorkflowCompleteResult result) {
        // 大屏发布流程早期实例可能未携带 prefix，为空时默认按大屏资源处理
        if (StringUtils.isEmpty(prefix) || FlowKey.大屏发布.getPrefix().equals(prefix)) {
            return screenResourceService.audit(id, auditState, result);
        }
        if (FlowKey.图片资源发布.getPrefix().equals(prefix)) {
            return screenResourceImgService.audit(id, auditState, result);
        }
        return Result.fail(null, "错误：未知的审批业务前缀：" + prefix);
    }
}

package cn.com.mfish.common.api;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.nocode.api.remote.RemoteNocodeService;
import cn.com.mfish.common.nocode.service.ScreenResourceService;
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

    @Override
    public Result<String> approved(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return screenResourceService.audit(id, 1, result);
    }

    @Override
    public Result<String> rejected(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return screenResourceService.audit(id, 2, result);
    }

    @Override
    public Result<String> canceled(String origin, String prefix, String id, WorkflowCompleteResult result) {
        return screenResourceService.audit(id, null, result);
    }
}

package cn.com.mfish.common.nocode.service;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.nocode.entity.ScreenResource;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 大屏资源信息
 * @author: mfish
 * @date: 2025-03-19
 * @version: V2.2.0
 */
public interface ScreenResourceService extends IService<ScreenResource> {
    Result<ScreenResource> insertScreenResource(ScreenResource screenResource);
    Result<ScreenResource> updateScreenResource(ScreenResource screenResource);
    Result<Boolean> deleteScreenResource(String sourceId);
    Result<String> audit(String sourceId, Integer auditState, WorkflowCompleteResult result);
}

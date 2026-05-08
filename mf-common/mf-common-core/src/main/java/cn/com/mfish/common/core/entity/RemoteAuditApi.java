package cn.com.mfish.common.core.entity;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.web.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @description: 工作流审批相关操作远程接口
 * @author: mfish
 * @date: 2025/9/16
 */
public interface RemoteAuditApi<T> {

    /**
     * 审批通过接口
     *
     * @param origin 请求来源
     * @param prefix 业务前缀
     * @param id     业务ID
     * @param result 工作流回调结果
     * @return 操作结果
     */
    @PostMapping("/{prefix}/approved/{id}")
    Result<String> approved(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("prefix") String prefix, @PathVariable("id") T id, @RequestBody WorkflowCompleteResult result);

    /**
     * 审批驳回接口
     *
     * @param origin 请求来源
     * @param prefix 业务前缀
     * @param id     业务ID
     * @param result 工作流回调结果
     * @return 操作结果
     */
    @PostMapping("/{prefix}/rejected/{id}")
    Result<String> rejected(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("prefix") String prefix, @PathVariable("id") T id, @RequestBody WorkflowCompleteResult result);

    /**
     * 审批撤销接口
     *
     * @param origin 请求来源
     * @param prefix 业务前缀
     * @param id     业务ID
     * @param result 工作流回调结果
     * @return 操作结果
     */
    @PostMapping("/{prefix}/canceled/{id}")
    Result<String> canceled(@RequestHeader(RPCConstants.REQ_ORIGIN) String origin, @PathVariable("prefix") String prefix, @PathVariable("id") T id, @RequestBody WorkflowCompleteResult result);

}

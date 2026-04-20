package cn.com.mfish.common.demo.service;

import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.demo.entity.DemoLeaveApply;
import cn.com.mfish.common.demo.req.ReqDemoLeaveApply;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
 * @description: leave apply service
 * @author: mfish
 * @date: 2026-04-19
 * @version: V2.3.1
 */
public interface DemoLeaveApplyService extends IService<DemoLeaveApply> {

    Result<PageResult<DemoLeaveApply>> queryPageList(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage);

    Result<DemoLeaveApply> add(DemoLeaveApply demoLeaveApply);

    Result<DemoLeaveApply> edit(DemoLeaveApply demoLeaveApply);

    Result<Boolean> delete(String id);

    Result<Boolean> deleteBatch(String ids);

    Result<DemoLeaveApply> queryById(String id);

    void export(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage) throws IOException;

    Result<DemoLeaveApply> submit(String id);

    Result<DemoLeaveApply> revoke(String id);

    Result<String> audit(String id, Integer auditState, WorkflowCompleteResult result);
}

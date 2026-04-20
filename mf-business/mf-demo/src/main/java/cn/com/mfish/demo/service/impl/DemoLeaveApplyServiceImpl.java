package cn.com.mfish.demo.service.impl;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.entity.WorkflowCompleteResult;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.demo.entity.DemoLeaveApply;
import cn.com.mfish.common.demo.req.ReqDemoLeaveApply;
import cn.com.mfish.common.demo.service.DemoLeaveApplyService;
import cn.com.mfish.common.workflow.api.entity.FlowableParam;
import cn.com.mfish.common.workflow.api.enums.FlowKey;
import cn.com.mfish.common.workflow.api.remote.RemoteWorkflowService;
import cn.com.mfish.demo.mapper.DemoLeaveApplyMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description: 请假申请审批Demo
 * @author: mfish
 * @date: 2026-04-18
 * @version: V2.3.1
 */
@Service
public class DemoLeaveApplyServiceImpl extends ServiceImpl<DemoLeaveApplyMapper, DemoLeaveApply> implements DemoLeaveApplyService {
    @Resource
    private RemoteWorkflowService remoteWorkflowService;

    @Override
    public Result<PageResult<DemoLeaveApply>> queryPageList(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqDemoLeaveApply, reqPage)), "请假申请审批Demo-查询成功!");
    }

    private List<DemoLeaveApply> queryList(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<DemoLeaveApply> lambdaQueryWrapper = new LambdaQueryWrapper<DemoLeaveApply>()
                .like(!StringUtils.isEmpty(reqDemoLeaveApply.getTitle()), DemoLeaveApply::getTitle, reqDemoLeaveApply.getTitle())
                .eq(null != reqDemoLeaveApply.getLeaveType(), DemoLeaveApply::getLeaveType, reqDemoLeaveApply.getLeaveType())
                .eq(null != reqDemoLeaveApply.getAuditState(), DemoLeaveApply::getAuditState, reqDemoLeaveApply.getAuditState())
                .orderByDesc(DemoLeaveApply::getCreateTime);
        return list(lambdaQueryWrapper);
    }

    @Override
    public Result<DemoLeaveApply> add(DemoLeaveApply demoLeaveApply) {
        demoLeaveApply.setAuditState(-1);
        // 根据开始时间和结束时间计算请假天数
        if (demoLeaveApply.getStartTime() != null && demoLeaveApply.getEndTime() != null) {
            long diffInMillis = demoLeaveApply.getEndTime().getTime() - demoLeaveApply.getStartTime().getTime();
            // 将毫秒转换为天数，保留一位小数
            BigDecimal days = new BigDecimal(diffInMillis)
                    .divide(new BigDecimal(TimeUnit.DAYS.toMillis(1)), 1, RoundingMode.HALF_UP);
            demoLeaveApply.setLeaveDays(days);
        }
        
        if (save(demoLeaveApply)) {
            return Result.ok(demoLeaveApply, "请假申请审批Demo-添加成功!");
        }
        return Result.fail(demoLeaveApply, "错误:请假申请审批Demo-添加失败!");
    }

    @Override
    public Result<DemoLeaveApply> edit(DemoLeaveApply demoLeaveApply) {
        DemoLeaveApply exist = getById(demoLeaveApply.getId());
        if (exist == null) {
            throw new MyRuntimeException("错误:记录不存在!");
        }
        if (!Integer.valueOf(-1).equals(exist.getAuditState())) {
            throw new MyRuntimeException("错误:当前记录不可编辑!");
        }
        demoLeaveApply.setAuditState(exist.getAuditState());
        
        // 根据开始时间和结束时间计算请假天数
        if (demoLeaveApply.getStartTime() != null && demoLeaveApply.getEndTime() != null) {
            long diffInMillis = demoLeaveApply.getEndTime().getTime() - demoLeaveApply.getStartTime().getTime();
            // 将毫秒转换为天数，保留一位小数
            BigDecimal days = new BigDecimal(diffInMillis)
                    .divide(new BigDecimal(TimeUnit.DAYS.toMillis(1)), 1, RoundingMode.HALF_UP);
            demoLeaveApply.setLeaveDays(days);
        }
        
        if (updateById(demoLeaveApply)) {
            return Result.ok(demoLeaveApply, "请假申请审批Demo-编辑成功!");
        }
        return Result.fail(demoLeaveApply, "错误:请假申请审批Demo-编辑失败!");
    }

    @Override
    @Transactional
    public Result<Boolean> delete(String id) {
        DemoLeaveApply demoLeaveApply = getById(id);
        if (demoLeaveApply == null) {
            return Result.fail(false, "错误:记录不存在!");
        }
        if (removeById(id)) {
            if (Integer.valueOf(0).equals(demoLeaveApply.getAuditState())) {
                Result<String> result = remoteWorkflowService.delProcessByBusinessKey(RPCConstants.INNER, id, "用户删除请假审批申请");
                if (!result.isSuccess()) {
                    throw new MyRuntimeException(result.getMsg());
                }
            }
            return Result.ok(true, "请假申请审批Demo-删除成功!");
        }
        return Result.fail(false, "错误:请假申请审批Demo-删除失败!");
    }

    @Override
    @Transactional
    public Result<Boolean> deleteBatch(String ids) {
        String[] idList = ids.split(",");
        for (String id : idList) {
            Result<Boolean> result = delete(id);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return Result.ok(true, "请假申请审批Demo-批量删除成功!");
    }

    @Override
    public Result<DemoLeaveApply> queryById(String id) {
        DemoLeaveApply demoLeaveApply = getById(id);
        return Result.ok(demoLeaveApply, "请假申请审批Demo-查询成功!");
    }

    @Override
    public void export(ReqDemoLeaveApply reqDemoLeaveApply, ReqPage reqPage) throws IOException {
        ExcelUtils.write("请假申请审批Demo_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), queryList(reqDemoLeaveApply, reqPage));
    }

    @Override
    @Transactional
    public Result<DemoLeaveApply> submit(String id) {
        DemoLeaveApply demoLeaveApply = getById(id);
        if (demoLeaveApply == null) {
            throw new MyRuntimeException("错误:记录不存在!");
        }
        if (Integer.valueOf(0).equals(demoLeaveApply.getAuditState())) {
            throw new MyRuntimeException("错误:当前记录已在审核中,请勿重复提交!");
        }
        demoLeaveApply.setAuditState(0);
        if (!updateById(demoLeaveApply)) {
            return Result.fail(demoLeaveApply, "错误:提交审批失败!");
        }
        startProcess(demoLeaveApply);
        return Result.ok(demoLeaveApply, "提交审批成功!");
    }

    @Override
    @Transactional
    public Result<DemoLeaveApply> revoke(String id) {
        DemoLeaveApply demoLeaveApply = getById(id);
        if (demoLeaveApply == null) {
            throw new MyRuntimeException("错误:记录不存在!");
        }
        if (!Integer.valueOf(0).equals(demoLeaveApply.getAuditState())) {
            throw new MyRuntimeException("错误:只有审核中记录支持撤回!");
        }
        Result<String> result = remoteWorkflowService.delProcessByBusinessKey(RPCConstants.INNER, id, "用户撤回请假审批申请");
        if (!result.isSuccess()) {
            throw new MyRuntimeException(result.getMsg());
        }
        demoLeaveApply.setAuditState(-1);
        if (!updateById(demoLeaveApply)) {
            throw new MyRuntimeException("错误:撤回后状态更新失败!");
        }
        return Result.ok(demoLeaveApply, "撤回审批成功!");
    }

    @Override
    public Result<String> audit(String id, Integer auditState, WorkflowCompleteResult result) {
        DemoLeaveApply demoLeaveApply = getById(id);
        if (demoLeaveApply == null) {
            throw new MyRuntimeException("错误:记录不存在!");
        }
        demoLeaveApply.setAuditState(auditState);
        if (!updateById(demoLeaveApply)) {
            throw new MyRuntimeException("错误:审批操作异常!");
        }
        return Result.ok(id, "审批操作成功!");
    }

    private void startProcess(DemoLeaveApply demoLeaveApply) {
        Result<String> result = remoteWorkflowService.startProcess(RPCConstants.INNER, new FlowableParam<String>()
                .setKey(FlowKey.请假申请发布.toString())
                .setId(demoLeaveApply.getId())
                .setPrefix("demoLeaveApply")
                .setCallback("cn.com.mfish.demo.api.remote.RemoteDemoLeaveApplyService"));
        if (!result.isSuccess()) {
            throw new MyRuntimeException(result.getMsg());
        }
    }
}

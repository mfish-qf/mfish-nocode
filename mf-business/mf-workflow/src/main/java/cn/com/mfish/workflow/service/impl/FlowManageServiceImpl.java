package cn.com.mfish.workflow.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.excel.ExcelUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.FlowManage;
import cn.com.mfish.common.workflow.service.FlowableService;
import cn.com.mfish.workflow.common.BpmnConverter;
import cn.com.mfish.workflow.entity.FlowJson;
import cn.com.mfish.workflow.mapper.FlowManageMapper;
import cn.com.mfish.workflow.req.ReqFlowManage;
import cn.com.mfish.workflow.service.FlowManageService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: 流程管理
 * @author: mfish
 * @date: 2026-03-30
 * @version: V2.3.1
 */
@Slf4j
@Service
public class FlowManageServiceImpl extends ServiceImpl<FlowManageMapper, FlowManage> implements FlowManageService {

    @Resource
    FlowableService flowableService;

    /**
     * 分页列表查询
     *
     * @param reqFlowManage 流程管理请求参数
     * @param reqPage       分页参数
     * @return 返回流程管理-分页列表
     */
    @Override
    public Result<PageResult<FlowManage>> queryPageList(ReqFlowManage reqFlowManage, ReqPage reqPage) {
        return Result.ok(new PageResult<>(queryList(reqFlowManage, reqPage)), "流程管理-查询成功!");
    }

    /**
     * 获取列表
     *
     * @param reqFlowManage 流程管理请求参数
     * @param reqPage       分页参数
     * @return 返回流程管理-分页列表
     */
    private List<FlowManage> queryList(ReqFlowManage reqFlowManage, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        LambdaQueryWrapper<FlowManage> lambdaQueryWrapper = new LambdaQueryWrapper<FlowManage>()
                .eq(!StringUtils.isEmpty(reqFlowManage.getFlowKey()), FlowManage::getFlowKey, reqFlowManage.getFlowKey())
                .like(!StringUtils.isEmpty(reqFlowManage.getName()), FlowManage::getName, reqFlowManage.getName())
                .eq(null != reqFlowManage.getReleased(), FlowManage::getReleased, reqFlowManage.getReleased())
                .eq(FlowManage::getDelFlag, 0)
                .orderByDesc(FlowManage::getCreateTime, FlowManage::getUpdateTime);
        return list(lambdaQueryWrapper);
    }

    /**
     * 添加
     *
     * @param flowManage 流程管理对象
     * @return 返回流程管理-添加结果
     */
    @Override
    public Result<FlowManage> add(FlowManage flowManage) {
        // 校验 flowKey 是否存在，如果存在则获取当前最大版本号并递增
        if (StringUtils.isEmpty(flowManage.getFlowKey())) {
            return Result.fail(null, "错误:flowKey不允许为空!");
        }
        if (exists(new LambdaQueryWrapper<FlowManage>()
                .eq(FlowManage::getFlowKey, flowManage.getFlowKey()))) {
            return Result.fail(null, "错误:流程已存在!");
        }
        FlowJson flowJson = JSON.parseObject(flowManage.getFlowConfig(), FlowJson.class);
        flowManage.setHex(DigestUtils.sha256Hex(JSON.toJSONString(flowJson)));
        if (save(flowManage)) {
            return Result.ok(flowManage, "流程管理-添加成功!");
        }
        return Result.fail(flowManage, "错误:流程管理-添加失败!");
    }

    /**
     * 编辑
     *
     * @param flowManage 流程管理对象
     * @return 返回流程管理-编辑结果
     */
    @Override
    public Result<FlowManage> edit(FlowManage flowManage) {
        // 1. 查询数据库中的流程信息
        FlowManage dbFlowManage = getById(flowManage.getId());
        if (dbFlowManage == null) {
            return Result.fail(flowManage, "错误：流程不存在");
        }
        FlowJson flowJson = JSON.parseObject(flowManage.getFlowConfig(), FlowJson.class);
        String hex = DigestUtils.sha256Hex(JSON.toJSONString(flowJson));
        // 3. 判断流程配置是否修改，如果未修改则执行更新操作
        if (hex.equals(dbFlowManage.getHex()) || dbFlowManage.getVersion() == null) {
            if (updateById(flowManage)) {
                return Result.ok(flowManage, "流程管理-编辑成功!");
            }
        } else {
            // 4. 如果流程配置修改则执行新增操作
            flowManage.setHex(hex);
            flowManage.setId(null);
            if (save(flowManage)) {
                return Result.ok(flowManage, "流程发生变化-新增流程成功!");
            }
        }
        return Result.fail(flowManage, "错误:流程管理-编辑失败!");
    }

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回流程管理-删除结果
     */
    @Override
    @Transactional
    public Result<Boolean> delete(String id) {
        FlowManage flowManage = getById(id);
        if (flowManage == null) {
            return Result.fail(false, "错误:流程不存在!");
        }
        if (updateById(flowManage.setDelFlag(1))) {
            return Result.ok(true, "流程管理-删除成功!");
        }
        return Result.fail(false, "错误:流程管理-删除失败!");
    }

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回流程管理对象
     */
    @Override
    public Result<FlowManage> queryById(String id) {
        FlowManage flowManage = getById(id);
        return Result.ok(flowManage, "流程管理-查询成功!");
    }

    /**
     * 导出流程管理数据
     *
     * @param reqFlowManage 流程管理请求参数
     * @param reqPage       分页参数
     * @throws IOException IO 异常
     */
    @Override
    public void export(ReqFlowManage reqFlowManage, ReqPage reqPage) throws IOException {
        //swagger 调用会用问题，使用 postman 测试
        ExcelUtils.write("流程管理_" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), queryList(reqFlowManage, reqPage));
    }

    /**
     * 发布流程
     *
     * @param id 流程ID
     * @return 返回发布结果
     */
    @Override
    @Transactional
    public Result<Boolean> publish(String id) {
        // 1. 查询流程信息
        FlowManage flowManage = getById(id);
        if (flowManage == null) {
            return Result.fail(false, "错误：流程不存在");
        }
        // 2. 验证流程配置是否为空，检查是否已发布
        if (StringUtils.isEmpty(flowManage.getFlowConfig())) {
            return Result.fail(false, "错误：流程配置不能为空");
        }
        if (flowManage.getReleased() == 1) {
            return Result.fail(false, "错误：流程已发布，请先撤回");
        }
        // 3. 调用流程格式化进行验证
        try {
            BpmnConverter.convertToBpmn(
                    flowManage.getFlowKey(),
                    flowManage.getName(),
                    flowManage.getRemark(),
                    flowManage.getFlowConfig()
            );
        } catch (Exception e) {
            log.error("流程格式化失败：{}", e.getMessage(), e);
            return Result.fail(false, "错误：流程配置不正确，请检查流程设计是否完整且符合规范");
        }
        // 4. 如果存在同flowKey的流程，则将同flowKey的流程发布状态设为未发布
        update(new FlowManage().setReleased(0), new LambdaQueryWrapper<FlowManage>().eq(FlowManage::getFlowKey, flowManage.getFlowKey()));
        // 5. 部署流程,获取版本号
        Integer version = flowableService.deployProcess(flowManage.getFlowKey(), flowManage.getName(), flowManage.getRemark(), flowManage.getFlowConfig());
        // 6. 如果版本号大于数据库中的版本号，新增一条流程信息，否则更新流程信息
        if (version != null && flowManage.getVersion() != null && version > flowManage.getVersion()) {
            flowManage.setDelFlag(1);
            if (updateById(flowManage)) {
                flowManage.setId(null).setVersion(version).setReleased(1).setDelFlag(0);
                if (save(flowManage)) {
                    return Result.ok(true, "流程发布成功!");
                }
            }
        } else {
            flowManage.setVersion(version);
            flowManage.setReleased(1);
            // 6. 更新流程信息
            if (updateById(flowManage)) {
                return Result.ok(true, "流程发布成功!");
            }
        }
        throw new MyRuntimeException("错误：流程发布失败!");
    }

    /**
     * 撤回发布
     *
     * @param id 流程ID
     * @return 返回撤回结果
     */
    @Override
    @Transactional
    public Result<Boolean> unpublish(String id) {
        // 1. 查询流程信息
        FlowManage flowManage = getById(id);
        if (flowManage == null) {
            return Result.fail(false, "错误：流程不存在");
        }
        // 2. 检查是否已发布
        if (flowManage.getReleased() == null || flowManage.getReleased() != 1) {
            return Result.fail(false, "错误：流程未发布，无需撤回");
        }
        // 3. 更新发布状态为未发布
        flowManage.setReleased(0);
        if (updateById(flowManage)) {
            return Result.ok(true, "流程撤回成功!");
        }
        return Result.fail(false, "错误：流程撤回失败!");
    }

}

package cn.com.mfish.workflow.service;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.workflow.api.entity.FlowManage;
import cn.com.mfish.workflow.req.ReqFlowManage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
 * @description: 流程管理
 * @author: mfish
 * @date: 2026-03-30
 * @version: V2.3.1
 */
public interface FlowManageService extends IService<FlowManage> {
    /**
     * 分页列表查询
     *
     * @param reqFlowManage 流程管理请求参数
     * @param reqPage 分页参数
     * @return 返回流程管理-分页列表
     */
    Result<PageResult<FlowManage>> queryPageList(ReqFlowManage reqFlowManage, ReqPage reqPage);

    /**
     * 添加
     *
     * @param flowManage 流程管理对象
     * @return 返回流程管理-添加结果
     */
    Result<FlowManage> add(FlowManage flowManage);

    /**
     * 编辑
     *
     * @param flowManage 流程管理对象
     * @return 返回流程管理-编辑结果
     */
    Result<FlowManage> edit(FlowManage flowManage);

    /**
     * 通过id删除
     *
     * @param id 唯一ID
     * @return 返回流程管理-删除结果
     */
    Result<Boolean> delete(String id);

    /**
     * 通过id查询
     *
     * @param id 唯一ID
     * @return 返回流程管理对象
     */
    Result<FlowManage> queryById(String id);

    /**
     * 导出
     *
     * @param reqFlowManage 流程管理请求参数
     * @param reqPage 分页参数
     * @throws IOException IO异常
     */
    void export(ReqFlowManage reqFlowManage, ReqPage reqPage) throws IOException;

    /**
     * 发布流程
     *
     * @param id 流程ID
     * @return 返回发布结果
     */
    Result<Boolean> publish(String id);

    /**
     * 撤回发布
     *
     * @param id 流程ID
     * @return 返回撤回结果
     */
    Result<Boolean> unpublish(String id);
}

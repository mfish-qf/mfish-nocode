package cn.com.mfish.project.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.entity.PmIteration;
import cn.com.mfish.project.entity.PmIterationTestNode;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

/**
 * @description: 迭代版本
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmIterationService extends IService<PmIteration> {

    /**
     * 查询迭代列表(包含各版本关联的多节点转测列表及自动解析计算的跨月标签)
     *
     * @return 迭代列表
     */
    Result<List<PmIteration>> queryList();

    /**
     * 通过id或版本号查询迭代详情
     *
     * @param idOrCode 迭代ID或版本号
     * @return 迭代详情
     */
    Result<PmIteration> queryByIdOrCode(String idOrCode);

    /**
     * 新增迭代(含子表转测节点)
     *
     * @param iteration 迭代对象
     * @return 添加结果
     */
    Result<PmIteration> addIteration(PmIteration iteration);

    /**
     * 编辑迭代(包含子表转测节点的新增与删除同步)
     *
     * @param iteration 迭代对象
     * @return 编辑结果
     */
    Result<PmIteration> editIteration(PmIteration iteration);

    /**
     * 通过id删除迭代版本(连同转测节点)
     *
     * @param id 迭代ID
     * @return 删除结果
     */
    Result<Boolean> deleteIteration(String id);

    /**
     * 为指定迭代添加转测节点
     *
     * @param iterationId 迭代ID
     * @param node        转测节点
     * @return 添加结果
     */
    Result<PmIterationTestNode> addTestNode(String iterationId, PmIterationTestNode node);

    /**
     * 移除批次转测节点
     *
     * @param nodeId 转测节点ID
     * @return 删除结果
     */
    Result<Boolean> deleteTestNode(String nodeId);
}

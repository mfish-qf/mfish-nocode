package cn.com.mfish.project.service;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.dto.WorkloadStatsDTO;
import cn.com.mfish.project.entity.PmPersonnel;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

/**
 * @description: 团队人员与岗位档案
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmPersonnelService extends IService<PmPersonnel> {

    /**
     * 查询项目人员库(支持按岗位角色过滤)
     *
     * @param role   岗位角色编码(frontend/backend/qa/product/owner)
     * @param keyword 姓名/部门关键词
     * @return 人员列表
     */
    Result<List<PmPersonnel>> queryList(String role, String keyword);

    /**
     * 分页查询人员列表
     *
     * @param role    岗位角色编码
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    Result<PageResult<PmPersonnel>> queryPageList(String role, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 添加人员
     *
     * @param personnel 人员对象
     * @return 添加结果
     */
    Result<PmPersonnel> addPersonnel(PmPersonnel personnel);

    /**
     * 编辑人员
     *
     * @param personnel 人员对象
     * @return 编辑结果
     */
    Result<PmPersonnel> editPersonnel(PmPersonnel personnel);

    /**
     * 通过id删除人员
     *
     * @param id 人员ID
     * @return 删除结果
     */
    Result<Boolean> deletePersonnel(String id);

    /**
     * 工时负载统计：计算各开发人员的总承接需求数、总工时数(人天)、交付百分比、
     * 团队平均工时偏差差值以及健康负荷诊断状态(轻载/正常/饱和/过载)
     *
     * @return 负载统计列表
     */
    Result<List<WorkloadStatsDTO>> workloadStats();
}

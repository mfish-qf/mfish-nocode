package cn.com.mfish.project.mapper;

import cn.com.mfish.project.entity.PmDevSchedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @description: 研发排期明细(需求×人员×岗位)
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.1
 */
public interface PmDevScheduleMapper extends BaseMapper<PmDevSchedule> {

    /**
     * 按需求ID物理删除排期明细(回收站彻底删除需求时的级联清理, 不留孤儿行)
     */
    @Delete("DELETE FROM pm_dev_schedule WHERE req_id = #{reqId}")
    int physicalDeleteByReqId(@Param("reqId") String reqId);

    /**
     * 跨租户读取指定人员(按 person_id)的排期明细占用。
     * <p>
     * 用途: 同一个人(person_id 相同)可能属于多个租户, 其空闲度应全局合并,
     * 否则会被两个租户在同一时段各自排任务(不可能同时出现在两处)。
     * 调用方须临时解除 @DataScope 拦截(见 PmRequirementServiceImpl.prefillPersonBusyDates),
     * 仅对"本租户已有占用"的人员向外查其所属其他租户的占用, 单体户人员无额外行、零影响。
     * 该方法刻意绕过 @DataScope: SQL 自带 {@code tenant_id <> #{currentTenant}} 过滤,
     * 且只读占用(日期/工时), 不暴露其他租户的需求内容或无关人员。
     */
    @Select("<script>SELECT * FROM pm_dev_schedule "
            + "WHERE person_id IN "
            + "<foreach collection='personIds' item='pid' open='(' separator=',' close=')'>#{pid}</foreach> "
            + "AND tenant_id &lt;&gt; #{currentTenant}</script>")
    List<PmDevSchedule> selectCrossTenantByPersonIds(@Param("personIds") Set<String> personIds,
                                                     @Param("currentTenant") String currentTenant);
}

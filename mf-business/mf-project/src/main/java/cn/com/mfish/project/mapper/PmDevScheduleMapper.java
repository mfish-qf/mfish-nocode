package cn.com.mfish.project.mapper;

import cn.com.mfish.project.entity.PmDevSchedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

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
}

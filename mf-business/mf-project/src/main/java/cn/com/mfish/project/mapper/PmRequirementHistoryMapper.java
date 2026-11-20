package cn.com.mfish.project.mapper;

import cn.com.mfish.project.entity.PmRequirementHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * @description: 需求调配与状态流转审计日志
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmRequirementHistoryMapper extends BaseMapper<PmRequirementHistory> {

    /**
     * 按需求ID物理删除审计日志(回收站彻底删除需求时的级联清理, 该表有 @TableLogic, MP 删除只会软删)
     */
    @Delete("DELETE FROM pm_requirement_history WHERE req_id = #{reqId}")
    int physicalDeleteByReqId(@Param("reqId") String reqId);
}

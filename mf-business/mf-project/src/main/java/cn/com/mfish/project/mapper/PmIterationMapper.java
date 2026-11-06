package cn.com.mfish.project.mapper;

import cn.com.mfish.project.entity.PmIteration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * @description: 迭代版本
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmIterationMapper extends BaseMapper<PmIteration> {

    /**
     * 所有版本中最大的截至日期(end_date), 用于未纳入版本需求的排期兜底起点
     * @return 最大 end_date, 无数据时为 null
     */
    @Select("SELECT MAX(end_date) FROM pm_iteration")
    Date selectMaxEndDate();
}

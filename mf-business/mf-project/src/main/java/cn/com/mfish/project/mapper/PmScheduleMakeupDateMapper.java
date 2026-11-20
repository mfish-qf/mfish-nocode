package cn.com.mfish.project.mapper;

import cn.com.mfish.project.entity.PmScheduleMakeupDate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @description: 排期日历调休补班工作日
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmScheduleMakeupDateMapper extends BaseMapper<PmScheduleMakeupDate> {

    /**
     * 物理删除指定配置下的全部调休补班工作日明细
     * 注：实体使用了 @TableLogic，BaseMapper.delete 会被改写为逻辑删除(UPDATE del_flag=1)，
     * 与后续"按原 id 重新插入"发生主键冲突，故此处用原生 DELETE 强制物理删除。
     */
    int physicalDeleteByConfigId(@Param("configId") String configId);
}

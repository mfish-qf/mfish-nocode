package cn.com.mfish.project.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.dto.ScheduleConfigDTO;
import com.baomidou.mybatisplus.spring.service.IService;
import cn.com.mfish.project.entity.PmScheduleConfig;

/**
 * @description: 排期日历全局规则配置
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
public interface PmScheduleConfigService extends IService<PmScheduleConfig> {

    /**
     * 获取当前生效的排期跳过规则(包含节假日、封版冻结日列表和调休补班工作日)
     *
     * @return 排期配置
     */
    Result<ScheduleConfigDTO> getConfig();

    /**
     * 更新排期规则及明细
     *
     * @param config 排期配置
     * @return 更新结果
     */
    Result<ScheduleConfigDTO> updateConfig(ScheduleConfigDTO config);

    /**
     * 获取排期配置数据对象(内部供其它服务调用，不返回Result包装)
     *
     * @return 排期配置
     */
    ScheduleConfigDTO getConfigData();
}

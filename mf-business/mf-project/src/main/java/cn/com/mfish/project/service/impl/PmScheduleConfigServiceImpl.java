package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.dto.ScheduleConfigDTO;
import cn.com.mfish.project.dto.SkippedDateDTO;
import cn.com.mfish.project.entity.PmScheduleConfig;
import cn.com.mfish.project.entity.PmScheduleMakeupDate;
import cn.com.mfish.project.entity.PmScheduleSkippedDate;
import cn.com.mfish.project.mapper.PmScheduleConfigMapper;
import cn.com.mfish.project.mapper.PmScheduleMakeupDateMapper;
import cn.com.mfish.project.mapper.PmScheduleSkippedDateMapper;
import cn.com.mfish.project.service.PmScheduleConfigService;
import cn.com.mfish.project.utils.ScheduleDateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 排期日历全局规则配置
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Service
public class PmScheduleConfigServiceImpl extends ServiceImpl<PmScheduleConfigMapper, PmScheduleConfig> implements PmScheduleConfigService {
    /** 单行配置固定ID */
    private static final String CONFIG_ID = "1";

    @Resource
    private PmScheduleSkippedDateMapper skippedDateMapper;
    @Resource
    private PmScheduleMakeupDateMapper makeupDateMapper;

    @Override
    public Result<ScheduleConfigDTO> getConfig() {
        return Result.ok(getConfigData(), "获取排期配置成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<ScheduleConfigDTO> updateConfig(ScheduleConfigDTO config) {
        if (config == null) {
            return Result.fail("错误:排期配置不能为空!");
        }
        // 保存主配置(单行)
        PmScheduleConfig entity = getById(CONFIG_ID);
        Date now = new Date();
        if (entity == null) {
            entity = new PmScheduleConfig();
            entity.setId(CONFIG_ID);
            entity.setSkipWeekends(config.getSkipWeekends());
            entity.setCreateBy(safeGetCurrentUser());
            entity.setCreateTime(now);
            save(entity);
        } else {
            entity.setSkipWeekends(config.getSkipWeekends());
            entity.setUpdateBy(safeGetCurrentUser());
            entity.setUpdateTime(now);
            updateById(entity);
        }
        // 同步跳过日期明细(先物理删后插)
        // 注意：实体含 @TableLogic，BaseMapper.delete 会被改写为逻辑删除(UPDATE del_flag=1)，
        // 与下面"按原 id 重新插入"发生主键冲突使整体更新失败，故使用原生物理删除。
        skippedDateMapper.physicalDeleteByConfigId(CONFIG_ID);
        if (config.getSkippedDates() != null) {
            for (SkippedDateDTO item : config.getSkippedDates()) {
                if (item == null || StringUtils.isEmpty(item.getDate())) {
                    continue;
                }
                PmScheduleSkippedDate record = new PmScheduleSkippedDate();
                record.setConfigId(CONFIG_ID);
                record.setDate(java.sql.Date.valueOf(item.getDate()));
                record.setName(StringUtils.isEmpty(item.getName()) ? "休假/跳过日" : item.getName());
                record.setType(StringUtils.isEmpty(item.getType()) ? "custom_off" : item.getType());
                record.setIsRecurringWeekend(item.getIsRecurringWeekend());
                record.setCreateBy(safeGetCurrentUser());
                record.setCreateTime(now);
                skippedDateMapper.insert(record);
            }
        }
        // 同步调休补班工作日明细(先物理删后插)
        makeupDateMapper.physicalDeleteByConfigId(CONFIG_ID);
        if (config.getMakeupWorkdays() != null) {
            for (String dateStr : config.getMakeupWorkdays()) {
                if (StringUtils.isEmpty(dateStr)) {
                    continue;
                }
                PmScheduleMakeupDate record = new PmScheduleMakeupDate();
                record.setConfigId(CONFIG_ID);
                record.setDate(java.sql.Date.valueOf(dateStr));
                record.setCreateBy(safeGetCurrentUser());
                record.setCreateTime(now);
                makeupDateMapper.insert(record);
            }
        }
        return Result.ok(getConfigData(), "排期配置更新成功!");
    }

    @Override
    public ScheduleConfigDTO getConfigData() {
        ScheduleConfigDTO dto = new ScheduleConfigDTO();
        PmScheduleConfig entity = getById(CONFIG_ID);
        if (entity != null && entity.getSkipWeekends() != null) {
            dto.setSkipWeekends(entity.getSkipWeekends());
        }
        // 加载跳过日期明细
        List<PmScheduleSkippedDate> skippedDates = skippedDateMapper.selectList(
                new LambdaQueryWrapper<PmScheduleSkippedDate>()
                        .eq(PmScheduleSkippedDate::getConfigId, CONFIG_ID)
                        .orderByAsc(PmScheduleSkippedDate::getDate));
        List<SkippedDateDTO> skippedDTOs = new ArrayList<>();
        for (PmScheduleSkippedDate item : skippedDates) {
            SkippedDateDTO skipped = new SkippedDateDTO();
            skipped.setId(item.getId());
            skipped.setDate(formatDate(item.getDate()));
            skipped.setName(item.getName());
            skipped.setType(item.getType());
            skipped.setIsRecurringWeekend(item.getIsRecurringWeekend());
            skippedDTOs.add(skipped);
        }
        dto.setSkippedDates(skippedDTOs);
        // 加载调休补班工作日明细
        List<PmScheduleMakeupDate> makeupDates = makeupDateMapper.selectList(
                new LambdaQueryWrapper<PmScheduleMakeupDate>()
                        .eq(PmScheduleMakeupDate::getConfigId, CONFIG_ID)
                        .orderByAsc(PmScheduleMakeupDate::getDate));
        List<String> makeupWorkdays = new ArrayList<>();
        for (PmScheduleMakeupDate item : makeupDates) {
            if (item.getDate() != null) {
                makeupWorkdays.add(formatDate(item.getDate()));
            }
        }
        dto.setMakeupWorkdays(makeupWorkdays);
        return dto;
    }

    /**
     * 将 java.util.Date 格式化为 yyyy-MM-dd 字符串
     */
    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return ScheduleDateUtils.FORMATTER.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate());
    }

    /**
     * 安全获取当前登录用户ID(无请求上下文时返回空)
     */
    private String safeGetCurrentUser() {
        try {
            String userId = AuthInfoUtils.getCurrentUserId();
            return StringUtils.isEmpty(userId) ? AuthInfoUtils.getCurrentAccount() : userId;
        } catch (Exception e) {
            log.warn("获取当前登录用户失败: {}", e.getMessage());
            return null;
        }
    }
}

package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.project.dto.ScheduleConfigDTO;
import cn.com.mfish.project.entity.PmDevSchedule;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.mapper.PmDevScheduleMapper;
import cn.com.mfish.project.mapper.PmRequirementMapper;
import cn.com.mfish.project.req.ReqDevScheduleAdjust;
import cn.com.mfish.project.req.ReqDevSchedulePin;
import cn.com.mfish.project.service.PmDevScheduleService;
import cn.com.mfish.project.service.PmRequirementService;
import cn.com.mfish.project.service.PmScheduleConfigService;
import cn.com.mfish.project.utils.PersonnelIdUtils;
import cn.com.mfish.project.utils.ScheduleDateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

/**
 * @description: 研发排期明细服务实现
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.1
 */
@Slf4j
@Service
public class PmDevScheduleServiceImpl extends ServiceImpl<PmDevScheduleMapper, PmDevSchedule> implements PmDevScheduleService {

    @Resource
    private PmScheduleConfigService scheduleConfigService;
    @Resource
    private PmRequirementMapper requirementMapper;
    @Resource
    private PmRequirementService requirementService;

    @Override
    public Result<List<PmDevSchedule>> listByReqIds(List<String> reqIds) {
        if (reqIds == null || reqIds.isEmpty()) {
            return Result.ok(List.of(), "排期明细-查询成功!");
        }
        List<PmDevSchedule> list = list(new LambdaQueryWrapper<PmDevSchedule>()
                .in(PmDevSchedule::getReqId, reqIds)
                .orderByAsc(PmDevSchedule::getReqNo)
                .orderByAsc(PmDevSchedule::getSeq)
                .orderByAsc(PmDevSchedule::getDevStartDate));
        return Result.ok(list, "排期明细-查询成功!");
    }

    @Override
    public Result<List<PmDevSchedule>> listBySelection(List<String> iterationIds, Boolean includeUnassigned) {
        // 复用需求查询的版本过滤语义解析需求ID, 再按ID聚合排期明细
        // 前端不再回传大量需求UUID; 版本筛选口径(按 iteration_id 精确匹配/未纳入版本/ALL排除已归档)与需求列表完全一致
        List<String> reqIds = requirementService.listReqIdsBySelection(iterationIds, includeUnassigned);
        return listByReqIds(reqIds);
    }

    @Override
    public Result<PmDevSchedule> adjust(ReqDevScheduleAdjust req) {
        PmDevSchedule row = getById(req.getId());
        if (row == null) {
            return Result.fail("错误:排期明细不存在!");
        }
        ScheduleConfigDTO config = scheduleConfigService.getConfigData();
        LocalDate newStart;
        try {
            newStart = LocalDate.parse(req.getDevStartDate().trim(), ScheduleDateUtils.FORMATTER);
        } catch (DateTimeParseException e) {
            return Result.fail("错误:开始日期格式不合法(yyyy-MM-dd)!");
        }
        // 拖到周末/节假日: 顺延到最近的有效工作日
        newStart = ScheduleDateUtils.getWorkdayStartDate(newStart, config);
        BigDecimal workload = row.getWorkload() == null ? BigDecimal.ONE : row.getWorkload();
        LocalDate newEnd = ScheduleDateUtils.calculateDevEndDate(newStart, workload, config);
        // 与本人其他任务的工期重叠校验(按有效工作日判定, 周末重叠不冲突)
        if (hasOverlap(row, newStart, newEnd, config)) {
            return Result.fail("调期被拒绝: 该时间段与【" + row.getPersonName() + "】的其他排期任务重叠!");
        }
        Date startDate = java.sql.Date.valueOf(newStart);
        Date endDate = java.sql.Date.valueOf(newEnd);
        row.setDevStartDate(startDate);
        row.setDevEndDate(endDate);
        // 拖拽调期视为人工干预: 自动钉住, 防止一键重算时被覆盖
        row.setIsPinned(true);
        row.setPinReason(StringUtils.isEmpty(req.getPinReason()) ? "拖拽调期" : req.getPinReason());
        row.setUpdateTime(new Date());
        if (!updateById(row)) {
            return Result.fail("错误:排期调期失败!");
        }
        refreshRequirementRange(row.getReqId());
        return Result.ok(row, "已调整至 " + ScheduleDateUtils.FORMATTER.format(newStart)
                + " ~ " + ScheduleDateUtils.FORMATTER.format(newEnd) + "(已自动钉住)!");
    }

    @Override
    public Result<PmDevSchedule> togglePin(ReqDevSchedulePin req) {
        PmDevSchedule row = getById(req.getId());
        if (row == null) {
            return Result.fail("错误:排期明细不存在!");
        }
        boolean pinned = !Boolean.FALSE.equals(req.getPinned());
        row.setIsPinned(pinned);
        row.setPinReason(pinned
                ? (StringUtils.isEmpty(req.getPinReason()) ? "手动钉住" : req.getPinReason())
                : null);
        row.setUpdateTime(new Date());
        if (!updateById(row)) {
            return Result.fail("错误:钉住状态更新失败!");
        }
        return Result.ok(row, pinned ? "已钉住, 一键重算将保持不动" : "已取消钉住, 重算时参与重新排期!");
    }

    @Override
    public Result<Integer> deleteByReqIds(List<String> reqIds) {
        if (reqIds == null || reqIds.isEmpty()) {
            return Result.ok(0, "无排期明细需要清理!");
        }
        // 本表不走逻辑删除(@TableLogic已移除): delete 直接物理删除, 避免重算堆积幽灵行
        // 物理删除前限定本租户, 防止跨租户排期明细被误清
        int count = baseMapper.delete(new LambdaQueryWrapper<PmDevSchedule>()
                .in(PmDevSchedule::getReqId, reqIds)
                .eq(PmDevSchedule::getTenantId, AuthInfoUtils.getCurrentTenantId()));
        return Result.ok(count, "已清理 " + count + " 条排期明细!");
    }

    /**
     * 判断新排期区间与同一人员其他排期明细是否存在有效工作日重叠
     * <p>
     * 身份感知: 行带 personId 时按账号匹配(彻底区分同租户同名两人); 行无 personId(历史姓名行)
     * 时按姓名匹配且限定 person_id 为 null, 避免误并把另一个同名但已账号化的人当成重叠。
     */
    private boolean hasOverlap(PmDevSchedule row, LocalDate newStart, LocalDate newEnd, ScheduleConfigDTO config) {
        LambdaQueryWrapper<PmDevSchedule> q = new LambdaQueryWrapper<PmDevSchedule>()
                .ne(PmDevSchedule::getId, row.getId());
        if (StringUtils.isNotEmpty(row.getPersonId())) {
            q.eq(PmDevSchedule::getPersonId, row.getPersonId());
        } else {
            q.eq(PmDevSchedule::getPersonName, row.getPersonName())
                    .isNull(PmDevSchedule::getPersonId);
        }
        List<PmDevSchedule> others = list(q);
        for (PmDevSchedule other : others) {
            if (other.getDevStartDate() == null || other.getDevEndDate() == null) {
                continue;
            }
            LocalDate otherStart = toLocalDate(other.getDevStartDate());
            LocalDate otherEnd = toLocalDate(other.getDevEndDate());
            if (otherEnd.isBefore(otherStart)) {
                continue;
            }
            // 粗筛: 区间完全不相交则跳过
            if (newEnd.isBefore(otherStart) || newStart.isAfter(otherEnd)) {
                continue;
            }
            // 精筛: 逐有效工作日判定是否真正重叠(周末/节假日重叠不算冲突)
            LocalDate cursor = newStart.isAfter(otherStart) ? newStart : otherStart;
            LocalDate limit = newEnd.isBefore(otherEnd) ? newEnd : otherEnd;
            while (!cursor.isAfter(limit)) {
                if (!ScheduleDateUtils.isDateSkipped(cursor, config)) {
                    return true;
                }
                cursor = cursor.plusDays(1);
            }
        }
        return false;
    }

    /**
     * 调期后同步需求整体区间(dev_start_date/dev_end_date = 各人员任务 min/max)
     */
    private void refreshRequirementRange(String reqId) {
        List<PmDevSchedule> rows = list(new LambdaQueryWrapper<PmDevSchedule>()
                .eq(PmDevSchedule::getReqId, reqId));
        if (rows.isEmpty()) {
            return;
        }
        LocalDate min = null;
        LocalDate max = null;
        for (PmDevSchedule r : rows) {
            if (r.getDevStartDate() == null || r.getDevEndDate() == null) {
                continue;
            }
            LocalDate s = toLocalDate(r.getDevStartDate());
            LocalDate e = toLocalDate(r.getDevEndDate());
            if (min == null || s.isBefore(min)) {
                min = s;
            }
            if (max == null || e.isAfter(max)) {
                max = e;
            }
        }
        if (min == null || max == null) {
            return;
        }
        PmRequirement update = new PmRequirement();
        update.setId(reqId);
        update.setDevStartDate(java.sql.Date.valueOf(min));
        update.setDevEndDate(java.sql.Date.valueOf(max));
        update.setUpdateTime(new Date());
        requirementMapper.updateById(update);
    }

    private LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
}

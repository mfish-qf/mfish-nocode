package cn.com.mfish.project.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.dto.WorkloadStatsDTO;
import cn.com.mfish.project.entity.PmPersonnel;
import cn.com.mfish.project.entity.PmRequirement;
import cn.com.mfish.project.mapper.PmPersonnelMapper;
import cn.com.mfish.project.mapper.PmRequirementMapper;
import cn.com.mfish.project.service.PmPersonnelService;
import cn.com.mfish.project.utils.PersonnelNameUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 团队人员与岗位档案
 * @author: mfish
 * @date: 2026-10-12
 * @version: V2.4.1
 */
@Slf4j
@Service
public class PmPersonnelServiceImpl extends ServiceImpl<PmPersonnelMapper, PmPersonnel> implements PmPersonnelService {

    @Resource
    private PmRequirementMapper requirementMapper;

    @Override
    public Result<List<PmPersonnel>> queryList(String role, String keyword) {
        LambdaQueryWrapper<PmPersonnel> wrapper = new LambdaQueryWrapper<PmPersonnel>()
                .eq(StringUtils.isNotEmpty(role), PmPersonnel::getRole, role)
                .like(StringUtils.isNotEmpty(keyword), PmPersonnel::getName, keyword)
                .orderByAsc(PmPersonnel::getRole)
                .orderByAsc(PmPersonnel::getName);
        return Result.ok(list(wrapper), "人员库-查询成功!");
    }

    @Override
    public Result<PageResult<PmPersonnel>> queryPageList(String role, String keyword, Integer pageNum, Integer pageSize) {
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        PageHelper.startPage(num, size);
        List<PmPersonnel> list = list(new LambdaQueryWrapper<PmPersonnel>()
                .eq(StringUtils.isNotEmpty(role), PmPersonnel::getRole, role)
                .like(StringUtils.isNotEmpty(keyword), PmPersonnel::getName, keyword)
                .orderByAsc(PmPersonnel::getRole)
                .orderByAsc(PmPersonnel::getName));
        return Result.ok(new PageResult<>(list), "人员库-查询成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmPersonnel> addPersonnel(PmPersonnel personnel) {
        verifyPersonnel(personnel);
        personnel.setCreateBy(safeGetCurrentUser());
        personnel.setCreateTime(new Date());
        if (save(personnel)) {
            return Result.ok(personnel, "成员【" + personnel.getName() + "】添加成功!");
        }
        return Result.fail(personnel, "错误:人员-添加失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<PmPersonnel> editPersonnel(PmPersonnel personnel) {
        if (personnel == null || StringUtils.isEmpty(personnel.getId())) {
            throw new MyRuntimeException("错误:人员ID不能为空!");
        }
        if (getById(personnel.getId()) == null) {
            throw new MyRuntimeException("错误:人员不存在!");
        }
        verifyPersonnel(personnel);
        personnel.setUpdateBy(safeGetCurrentUser());
        personnel.setUpdateTime(new Date());
        if (updateById(personnel)) {
            return Result.ok(personnel, "成员【" + personnel.getName() + "】更新成功!");
        }
        return Result.fail(personnel, "错误:人员-编辑失败!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deletePersonnel(String id) {
        if (getById(id) == null) {
            throw new MyRuntimeException("错误:人员不存在!");
        }
        if (removeById(id)) {
            return Result.ok(true, "人员已删除!");
        }
        return Result.fail(false, "错误:人员-删除失败!");
    }

    @Override
    public Result<List<WorkloadStatsDTO>> workloadStats() {
        // 仅统计开发人员(前端/后端)
        List<PmPersonnel> developers = list(new LambdaQueryWrapper<PmPersonnel>()
                .in(PmPersonnel::getRole, "frontend", "backend")
                .orderByAsc(PmPersonnel::getRole)
                .orderByAsc(PmPersonnel::getName));
        List<PmRequirement> requirements = requirementMapper.selectList(new LambdaQueryWrapper<>());
        List<WorkloadStatsDTO> statsList = new ArrayList<>();
        for (PmPersonnel dev : developers) {
            WorkloadStatsDTO stats = new WorkloadStatsDTO();
            stats.setName(dev.getName());
            stats.setDisplayName(StringUtils.isEmpty(dev.getDisplayName()) ? dev.getName() : dev.getDisplayName());
            stats.setRole(dev.getRole());
            stats.setRoleName(dev.getRoleName());
            stats.setDepartment(dev.getDepartment());
            stats.setTitle(dev.getTitle());
            int count = 0;
            int delivered = 0;
            BigDecimal totalWorkload = BigDecimal.ZERO;
            for (PmRequirement req : requirements) {
                boolean assigned = PersonnelNameUtils.parsePersonnelNames(
                        "backend".equals(dev.getRole()) ? req.getBackendDev() : req.getFrontendDev())
                        .contains(dev.getName());
                if (!assigned) {
                    continue;
                }
                count++;
                if (req.getWorkload() != null && req.getWorkload().compareTo(BigDecimal.ZERO) > 0) {
                    totalWorkload = totalWorkload.add(req.getWorkload());
                }
                if ("已上线".equals(req.getStatus())) {
                    delivered++;
                }
            }
            stats.setRequirementCount(count);
            stats.setTotalWorkload(totalWorkload.setScale(1, RoundingMode.HALF_UP));
            stats.setDeliveryPercent(count == 0 ? BigDecimal.ZERO
                    : BigDecimal.valueOf(delivered * 100.0 / count).setScale(1, RoundingMode.HALF_UP));
            statsList.add(stats);
        }
        // 计算团队平均工时与每人偏差
        BigDecimal avgWorkload = BigDecimal.ZERO;
        if (!statsList.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (WorkloadStatsDTO stats : statsList) {
                sum = sum.add(stats.getTotalWorkload());
            }
            avgWorkload = sum.divide(BigDecimal.valueOf(statsList.size()), 1, RoundingMode.HALF_UP);
        }
        for (WorkloadStatsDTO stats : statsList) {
            BigDecimal deviation = stats.getTotalWorkload().subtract(avgWorkload);
            stats.setAvgWorkloadDeviation(deviation.setScale(1, RoundingMode.HALF_UP));
            stats.setHealthStatus(resolveHealthStatus(stats.getTotalWorkload(), deviation, avgWorkload));
        }
        return Result.ok(statsList, "人员工时负载统计-查询成功!");
    }

    /**
     * 健康负荷诊断状态判定
     * 规则(与团队平均工时对比)：
     * 1. 偏差超过平均工时30%或绝对值超过5人天 → 过载
     * 2. 偏差超过平均工时10%或绝对值超过2人天 → 饱和
     * 3. 偏差低于平均工时-30%或绝对值低于-5人天 → 轻载
     * 4. 其余 → 正常
     */
    private String resolveHealthStatus(BigDecimal totalWorkload, BigDecimal deviation, BigDecimal avgWorkload) {
        BigDecimal zero = BigDecimal.ZERO;
        // 无任何任务承接直接判定为轻载
        if (totalWorkload.compareTo(zero) <= 0) {
            return "轻载";
        }
        if (avgWorkload.compareTo(zero) <= 0) {
            // 团队平均为0时的绝对阈值兜底
            if (totalWorkload.compareTo(BigDecimal.valueOf(10)) > 0) {
                return "过载";
            }
            if (totalWorkload.compareTo(BigDecimal.valueOf(4)) > 0) {
                return "饱和";
            }
            return "正常";
        }
        BigDecimal overloadLine = avgWorkload.multiply(new BigDecimal("0.3")).max(BigDecimal.valueOf(5));
        BigDecimal lightLoadLine = avgWorkload.multiply(new BigDecimal("0.3")).max(BigDecimal.valueOf(5));
        BigDecimal saturationLine = avgWorkload.multiply(new BigDecimal("0.1")).max(BigDecimal.valueOf(2));
        // 过载：高于团队平均工时超过阈值
        if (deviation.compareTo(overloadLine) > 0) {
            return "过载";
        }
        // 轻载：低于团队平均工时超过阈值
        if (deviation.compareTo(lightLoadLine.negate()) < 0) {
            return "轻载";
        }
        // 饱和：高于团队平均工时但在过载线以内
        if (deviation.compareTo(saturationLine) > 0) {
            return "饱和";
        }
        return "正常";
    }

    /**
     * 校验人员必填信息
     */
    private void verifyPersonnel(PmPersonnel personnel) {
        if (personnel == null) {
            throw new MyRuntimeException("错误:人员对象不能为空!");
        }
        if (StringUtils.isEmpty(personnel.getName())) {
            throw new MyRuntimeException("错误:人员姓名不能为空!");
        }
        if (StringUtils.isEmpty(personnel.getRole())) {
            throw new MyRuntimeException("错误:人员岗位角色不能为空!");
        }
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

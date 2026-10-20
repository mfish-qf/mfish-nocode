package cn.com.mfish.project.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.project.entity.PmDevSchedule;
import cn.com.mfish.project.req.ReqDevScheduleAdjust;
import cn.com.mfish.project.req.ReqDevSchedulePin;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

/**
 * @description: 研发排期明细(需求×人员×岗位)
 * @author: mfish
 * @date: 2026-10-16
 * @version: V2.4.1
 */
public interface PmDevScheduleService extends IService<PmDevSchedule> {

    /**
     * 按需求ID批量查询排期明细(供甘特图/日历按人铺期)
     *
     * @param reqIds 需求ID列表(为空返回空列表)
     * @return 排期明细列表(按需求序号、人员内顺序升序)
     */
    Result<List<PmDevSchedule>> listByReqIds(List<String> reqIds);

    /**
     * 按版本筛选条件查询排期明细(供甘特图/日历按人铺期)
     * 不再由前端回传需求ID列表, 改为传版本筛选条件, 后端复用需求查询解析需求ID后聚合排期
     *
     * @param iterationIds      勾选的版本ID列表(为空/null 表示纯汇总/ALL)
     * @param includeUnassigned 是否叠加"未纳入版本"
     * @return 排期明细列表(按需求序号、人员内顺序升序)
     */
    Result<List<PmDevSchedule>> listBySelection(List<String> iterationIds, Boolean includeUnassigned);

    /**
     * 拖拽调期: 更新任务开始日并按工时重算完成日
     * 自动钉住该行(避免被一键重算覆盖)；与本人其他任务工期重叠时拒绝
     *
     * @param req 调期参数(排期明细ID + 新开始日)
     * @return 更新后的排期明细
     */
    Result<PmDevSchedule> adjust(ReqDevScheduleAdjust req);

    /**
     * 钉住/取消钉住某条排期(钉住后一键重算保持不动)
     *
     * @param req 钉住参数
     * @return 更新后的排期明细
     */
    Result<PmDevSchedule> togglePin(ReqDevSchedulePin req);

    /**
     * 按需求ID级联清理排期明细(需求删除时调用)
     *
     * @param reqIds 需求ID列表
     * @return 清理条数
     */
    Result<Integer> deleteByReqIds(List<String> reqIds);
}

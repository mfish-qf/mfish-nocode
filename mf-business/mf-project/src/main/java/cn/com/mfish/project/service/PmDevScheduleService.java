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
     * 拖拽调期(钉住开始日 + 按工时容量逐任务装箱, 与 autoSchedule 同一口径):
     * workload 决定区间长度基准, 表中 dev_start_date/dev_end_date 决定锚点/影响窗口/展示顺序。
     * 每人每天 1 人天; 钉住任务(含被拖任务)按锚定日分组优先装箱, 同日锚定的按剩余工时比例分摊;
     * 窗口内未钉住任务按原开始日顺序找最早可行日再逐日吃掉剩余产能(等长即互换、满载日顺延),
     * 窗口外任务日期不动、占用按工时均摊登记。区间 = 首获产能日 ~ 工时耗尽日, 可还原/幂等。
     * 同日多任务分摊导致区间被撑长的写入 remark「多任务挤占导致周期拉长」供 tooltip 展示。
     *
     * @param req 调期参数(排期明细ID + 新开始日)
     * @return 更新后的排期明细
     */
    Result<PmDevSchedule> adjust(ReqDevScheduleAdjust req);

    /**
     * 钉住/取消钉住某条排期(钉住的是开始日: 一键重算时保持开始日不动, 结束日按工时容量重算)
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

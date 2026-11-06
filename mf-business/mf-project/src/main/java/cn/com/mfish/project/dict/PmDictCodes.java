package cn.com.mfish.project.dict;

/**
 * @Description: PM 业务字典编码(对应 sys_dict.dict_code)
 * @Description: 前后端统一通过字典管理以下业务取值, 禁止在代码中硬编码字符串做判断:
 *   - pm_review_result     需求评审结果(评审通过/补充修改/暂缓实施/未通过)
 *   - pm_requirement_status 需求任务状态(待评审/开发中/待转测/测试中/验收中/已上线/已取消)
 * 字典项 remark 语义标记约定(在框架「字典管理」中维护):
 *   - 评审结果: flow=&lt;流转目标状态&gt;(仅"通过"类结论配置, 驱动任务状态自动流转); stat=passed|pending|excluded(统计口径)
 *   - 任务状态: phase=pre|dev|post|terminal(生命周期阶段, pre 可被评审通过流转); progress=&lt;进入该状态的默认开发进度&gt;
 * @author: mfish
 * @date: 2026-10-17
 * @version: V2.4.1
 */
public interface PmDictCodes {
    /** 需求评审结果字典编码 */
    String REVIEW_RESULT = "pm_review_result";
    /** 需求任务状态字典编码 */
    String REQUIREMENT_STATUS = "pm_requirement_status";

    /** 评审结果 remark 标记: 评审通过后流转的目标状态 */
    String TAG_FLOW = "flow";
    /** 评审结果 remark 标记: 统计口径(passed=已准入 pending=待评审 excluded=不计入) */
    String TAG_STAT = "stat";

    /** 任务状态 remark 标记: 生命周期阶段 */
    String TAG_PHASE = "phase";
    /** 任务状态 remark 标记: 进入该状态的默认开发进度 */
    String TAG_PROGRESS = "progress";
    /** 任务状态 remark 标记: 最终完成状态(如已上线, 用于统计口径) */
    String TAG_FINAL = "final";
    /** 任务状态 remark 标记: 新建需求的默认状态适用角色(product=产品经理 admin=管理员) */
    String TAG_DEFAULT = "default";

    /** 阶段-开发前(可被评审通过自动流转) */
    String PHASE_PRE = "pre";
    /** 阶段-开发中 */
    String PHASE_DEV = "dev";
    /** 阶段-开发后(转测/测试/验收/上线) */
    String PHASE_POST = "post";
    /** 阶段-终止(已取消) */
    String PHASE_TERMINAL = "terminal";

    /** 统计口径-已准入 */
    String STAT_PASSED = "passed";
    /** 统计口径-待评审 */
    String STAT_PENDING = "pending";
}

package cn.com.mfish.project.dict;

import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.api.remote.RemoteDictService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: PM 业务字典服务(评审结果/任务状态等)
 * @Description: 统一通过框架远程字典服务 RemoteDictService(GET /sys/dictItem/{dictCode})读取业务取值
 *   与语义标记, 替代代码中硬编码的字符串判断——与前端 mfish-nocode-view 调用字典的方式同源。
 *   字典数据由框架「字典管理」页面维护(编码见 PmDictCodes)。
 *   字典调用失败或尚未种子化时(如新环境未执行建库脚本)使用内置兜底默认值, 并输出 WARN 提示补种。
 * @author: mfish
 * @date: 2026-10-17
 * @version: V2.4.1
 */
@Slf4j
@Component
@SuppressWarnings("rawtypes")
public class PmDictService {
    /** 字典缓存有效期(秒): 字典管理页修改后最长 30 秒生效, 避免每次保存都走 RPC */
    private static final long CACHE_TTL_SECONDS = 30;

    @Resource
    private RemoteDictService remoteDictService;

    /** 字典项缓存: dictCode → [过期时间戳, 字典项列表] */
    private final Map<String, Object[]> dictCache = new ConcurrentHashMap<>();
    /** 已输出过兜底 WARN 的字典编码(避免日志刷屏) */
    private final Set<String> warnedCodes = ConcurrentHashMap.newKeySet();

    /**
     * 查询字典项列表(按字典排序升序)
     *
     * @param dictCode 字典编码
     * @return 字典项列表; 字典不可用或未初始化时返回内置兜底默认值
     */
    public List<DictItem> items(String dictCode) {
        Object[] cached = dictCache.get(dictCode);
        long now = System.currentTimeMillis();
        if (cached != null && (Long) cached[0] > now) {
            return (List<DictItem>) cached[1];
        }
        List<DictItem> list = queryRemote(dictCode);
        if (list.isEmpty()) {
            list = fallbackItems(dictCode);
            if (!list.isEmpty() && warnedCodes.add(dictCode)) {
                log.warn("字典[{}]暂不可用或尚未初始化, 临时使用内置兜底默认值, 请检查字典管理页面配置!", dictCode);
            }
        }
        dictCache.put(dictCode, new Object[]{now + CACHE_TTL_SECONDS * 1000, list});
        return list;
    }

    /**
     * 通过框架远程字典服务查询字典项(内部来源, 该端点无权限校验)
     */
    private List<DictItem> queryRemote(String dictCode) {
        try {
            Result<List<DictItem>> result = remoteDictService.queryByCode(RPCConstants.INNER, dictCode);
            if (result != null && result.isSuccess() && result.getData() != null) {
                List<DictItem> list = new ArrayList<>(result.getData());
                // 仅保留启用状态的项, 并按字典排序升序
                list.removeIf(item -> item.getStatus() != null && item.getStatus() != 0);
                list.sort(Comparator.comparingInt(item -> item.getDictSort() == null ? 0 : item.getDictSort()));
                return list;
            }
            if (result != null && !result.isSuccess()) {
                log.warn("远程字典查询失败[{}]: {}", dictCode, result.getMsg());
            }
        } catch (Exception e) {
            log.warn("远程字典调用异常[{}]: {}", dictCode, e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * 按字典值查询单个字典项
     *
     * @param dictCode 字典编码
     * @param value    字典值(业务库中实际存储的值)
     * @return 字典项; 不存在返回 null
     */
    public DictItem item(String dictCode, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (DictItem item : items(dictCode)) {
            if (value.equals(String.valueOf(item.getDictValue()))) {
                return item;
            }
        }
        return null;
    }

    /**
     * 读取字典值对应的语义标记(remark 中 "k=v;k=v" 约定, 见 PmDictCodes)
     *
     * @param dictCode 字典编码
     * @param value    字典值
     * @param tagKey   标记名
     * @return 标记值; 不存在返回 null
     */
    public String tag(String dictCode, String value, String tagKey) {
        DictItem item = item(dictCode, value);
        return item == null ? null : parseTags(item.getRemark()).get(tagKey);
    }

    /**
     * 解析字典项备注中的语义标记: "flow=开发中;stat=passed" → {flow=开发中, stat=passed}
     */
    public static Map<String, String> parseTags(String remark) {
        Map<String, String> tags = new LinkedHashMap<>();
        if (remark == null || remark.isEmpty()) {
            return tags;
        }
        for (String pair : remark.split("[;,，；]")) {
            int idx = pair.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String key = pair.substring(0, idx).trim();
            String val = pair.substring(idx + 1).trim();
            if (!key.isEmpty() && !val.isEmpty()) {
                tags.put(key, val);
            }
        }
        return tags;
    }

    // ------------------------------------------------------------------
    // 语义化查询(评审结果 / 任务状态), 调用方无需关心字典结构
    // ------------------------------------------------------------------

    /** 任务状态的生命周期阶段(pre/dev/post/terminal); 状态不在字典中返回 null */
    public String statusPhase(String status) {
        return tag(PmDictCodes.REQUIREMENT_STATUS, status, PmDictCodes.TAG_PHASE);
    }

    /** 进入任务状态时的默认开发进度; 字典未配置返回 null */
    public Integer statusProgress(String status) {
        String val = tag(PmDictCodes.REQUIREMENT_STATUS, status, PmDictCodes.TAG_PROGRESS);
        if (val == null) {
            return null;
        }
        try {
            return Integer.valueOf(val);
        } catch (NumberFormatException e) {
            log.warn("字典[{}]状态[{}]的 progress 标记不是有效数字: {}", PmDictCodes.REQUIREMENT_STATUS, status, val);
            return null;
        }
    }

    /** 是否开发前阶段(空状态视为开发前, 可被评审通过自动流转) */
    public boolean isPreDevStatus(String status) {
        return status == null || status.isEmpty() || PmDictCodes.PHASE_PRE.equals(statusPhase(status));
    }

    /** 评审结论配置的状态流转目标(如"评审通过 → 开发中"); 未配置返回 null(不流转) */
    public String reviewFlowTarget(String reviewResult) {
        return tag(PmDictCodes.REVIEW_RESULT, reviewResult, PmDictCodes.TAG_FLOW);
    }

    /** 评审结论的统计口径(passed/pending/excluded); 未配置返回 null */
    public String reviewStat(String reviewResult) {
        return tag(PmDictCodes.REVIEW_RESULT, reviewResult, PmDictCodes.TAG_STAT);
    }

    /** 新建需求的默认状态(字典中第一个开发前阶段状态) */
    public String defaultStatus() {
        for (DictItem item : items(PmDictCodes.REQUIREMENT_STATUS)) {
            if (PmDictCodes.PHASE_PRE.equals(parseTags(item.getRemark()).get(PmDictCodes.TAG_PHASE))) {
                return String.valueOf(item.getDictValue());
            }
        }
        return null;
    }

    /** 评审结论是否合法(空值视为清空, 合法) */
    public boolean isValidReviewResult(String value) {
        return value == null || value.isEmpty() || item(PmDictCodes.REVIEW_RESULT, value) != null;
    }

    /** 任务状态是否合法(空值不合法, 状态必填) */
    public boolean isValidStatus(String value) {
        return value != null && !value.isEmpty() && item(PmDictCodes.REQUIREMENT_STATUS, value) != null;
    }

    // ------------------------------------------------------------------
    // 内置兜底默认值(仅字典不可用时生效, 与 db/mf_system.sql 种子保持一致)
    // ------------------------------------------------------------------

    private List<DictItem> fallbackItems(String dictCode) {
        List<DictItem> list = new ArrayList<>();
        if (PmDictCodes.REVIEW_RESULT.equals(dictCode)) {
            list.add(dictItem(dictCode, "评审通过", "green", 1, PmDictCodes.TAG_FLOW + "=开发中;" + PmDictCodes.TAG_STAT + "=" + PmDictCodes.STAT_PASSED));
            list.add(dictItem(dictCode, "补充修改", "orange", 2, PmDictCodes.TAG_STAT + "=pending"));
            list.add(dictItem(dictCode, "暂缓实施", "purple", 3, PmDictCodes.TAG_STAT + "=excluded"));
            list.add(dictItem(dictCode, "未通过", "red", 4, PmDictCodes.TAG_STAT + "=excluded"));
        } else if (PmDictCodes.REQUIREMENT_STATUS.equals(dictCode)) {
            list.add(dictItem(dictCode, "待评审", "default", 1, PmDictCodes.TAG_PHASE + "=" + PmDictCodes.PHASE_PRE + ";" + PmDictCodes.TAG_PROGRESS + "=0;" + PmDictCodes.TAG_DEFAULT + "=product"));
            list.add(dictItem(dictCode, "开发中", "blue", 2, PmDictCodes.TAG_PHASE + "=" + PmDictCodes.PHASE_DEV + ";" + PmDictCodes.TAG_PROGRESS + "=30;" + PmDictCodes.TAG_DEFAULT + "=admin"));
            list.add(dictItem(dictCode, "待转测", "purple", 3, PmDictCodes.TAG_PHASE + "=" + PmDictCodes.PHASE_POST + ";" + PmDictCodes.TAG_PROGRESS + "=100"));
            list.add(dictItem(dictCode, "测试中", "orange", 4, PmDictCodes.TAG_PHASE + "=" + PmDictCodes.PHASE_POST + ";" + PmDictCodes.TAG_PROGRESS + "=100"));
            list.add(dictItem(dictCode, "验收中", "cyan", 5, PmDictCodes.TAG_PHASE + "=" + PmDictCodes.PHASE_POST + ";" + PmDictCodes.TAG_PROGRESS + "=100"));
            list.add(dictItem(dictCode, "已上线", "green", 6, PmDictCodes.TAG_PHASE + "=" + PmDictCodes.PHASE_POST + ";" + PmDictCodes.TAG_PROGRESS + "=100;" + PmDictCodes.TAG_FINAL + "=true"));
            list.add(dictItem(dictCode, "已取消", "default", 7, PmDictCodes.TAG_PHASE + "=" + PmDictCodes.PHASE_TERMINAL));
        }
        return list;
    }

    private DictItem dictItem(String dictCode, String value, String color, int sort, String remark) {
        return new DictItem()
                .setDictCode(dictCode)
                .setDictLabel(value)
                .setDictValue(value)
                .setColor(color)
                .setDictSort(sort)
                .setStatus(0)
                .setRemark(remark);
    }
}

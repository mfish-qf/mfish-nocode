package cn.com.mfish.project.utils;

import cn.com.mfish.project.entity.PmRequirement;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 人员「账号化」身份工具
 * <p>
 * 核心约定: 需求表的人员配比由「姓名串」与「用户ID JSON数组」两套字段表达,
 * 二者通过 <b>PersonnelNameUtils.parsePersonnelNames 解析出的姓名列表索引</b> 对齐。
 * <p>
 * 设计要点(安全优先):
 * <ol>
 *   <li>二者长度不一致 / JSON 非法 → 一律视为不可对齐, 返回空 → 排期降级为按姓名处理</li>
 *   <li>某位人员未匹配到账号时, 数组对应位为 null, 该人同样降级为按姓名处理</li>
 *   <li>绝不因对齐失败抛异常中断排期</li>
 * </ol>
 *
 * @author: mfish
 * @date: 2026-10-27
 */
@Slf4j
public class PersonnelIdUtils {
    /**
     * 排期角色常量
     */
    public static final String ROLE_FRONTEND = "frontend";
    public static final String ROLE_BACKEND = "backend";
    public static final String ROLE_QA = "qa";
    public static final String ROLE_OWNER = "owner";
    /**
     * 全栈岗位: 同时承担前后端同一人(需求未拆分工时生成), 无独立人员字段,
     * 其人必然同时登记在前端与后端名单中 —— 由 {@link #personIdForName} 归并两侧解析结果
     */
    public static final String ROLE_FULLSTACK = "fullstack";

    private PersonnelIdUtils() {
    }

    /**
     * 解析用户ID JSON数组(允许 null 元素, 表示该位未匹配到账号)
     *
     * @param json JSON数组串, 如 ["id1",null,"id3"]
     * @return 元素列表(与入参数组长度一致, 保留 null 位); 非法或为空返回空列表
     */
    public static List<String> parseIds(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            JSONArray arr = JSON.parseArray(json);
            if (arr == null) {
                return new ArrayList<>();
            }
            List<String> list = new ArrayList<>(arr.size());
            for (int i = 0; i < arr.size(); i++) {
                Object o = arr.get(i);
                list.add(o == null ? null : o.toString());
            }
            return list;
        } catch (Exception e) {
            log.warn("解析用户ID数组失败, 按姓名兜底处理: {}", json, e);
            return new ArrayList<>();
        }
    }

    /**
     * 将用户ID列表序列化为 JSON 数组字符串
     *
     * @param ids 元素列表(支持 null 元素)
     * @return JSON串; 列表为空时返回 null(表示未设置, 便于区分"全部未匹配"与"未初始化")
     */
    public static String formatIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        try {
            return JSON.toJSONString(ids);
        } catch (Exception e) {
            log.warn("序列化用户ID数组失败: {}", ids, e);
            return null;
        }
    }

    /**
     * 取指定角色对应的姓名串
     *
     * @param req  需求
     * @param role 角色
     * @return 姓名串, 未知角色返回 null
     */
    public static String namesOfRole(PmRequirement req, String role) {
        if (req == null) {
            return null;
        }
        return switch (role) {
            case ROLE_FRONTEND -> req.getFrontendDev();
            case ROLE_BACKEND -> req.getBackendDev();
            case ROLE_QA -> req.getQaTester();
            case ROLE_OWNER -> req.getOwner();
            default -> null;
        };
    }

    /**
     * 取指定角色对应的用户ID数组串
     *
     * @param req  需求
     * @param role 角色
     * @return JSON串, 未知角色返回 null
     */
    public static String idsOfRole(PmRequirement req, String role) {
        if (req == null) {
            return null;
        }
        return switch (role) {
            case ROLE_FRONTEND -> req.getFrontendDevIds();
            case ROLE_BACKEND -> req.getBackendDevIds();
            case ROLE_QA -> req.getQaTesterIds();
            case ROLE_OWNER -> req.getOwnerIds();
            default -> null;
        };
    }

    /**
     * 取指定角色的「姓名 → 用户ID」映射(按索引对齐, 用于容量日历键与自愈回填)
     * <p>
     * 不可对齐或该位未匹配到账号时, 映射中该姓名对应值为 null(调用方须降级为按姓名)
     *
     * @param req  需求
     * @param role 角色
     * @return 有序映射(保持姓名的解析顺序)
     */
    public static Map<String, String> nameToIdMap(PmRequirement req, String role) {
        List<String> names = PersonnelNameUtils.parsePersonnelNames(namesOfRole(req, role));
        List<String> ids = parseIds(idsOfRole(req, role));
        boolean aligned = !ids.isEmpty() && ids.size() == names.size();
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < names.size(); i++) {
            String id = aligned ? ids.get(i) : null;
            map.put(names.get(i), (id == null || id.isBlank()) ? null : id);
        }
        return map;
    }

    /**
     * 写回指定角色的用户ID数组
     *
     * @param req  需求
     * @param role 角色
     * @param ids  元素列表(支持 null 元素表示未匹配到账号)
     */
    public static void setIdsOfRole(PmRequirement req, String role, List<String> ids) {
        if (req == null) {
            return;
        }
        String json = formatIds(ids);
        switch (role) {
            case ROLE_FRONTEND -> req.setFrontendDevIds(json);
            case ROLE_BACKEND -> req.setBackendDevIds(json);
            case ROLE_QA -> req.setQaTesterIds(json);
            case ROLE_OWNER -> req.setOwnerIds(json);
            default -> {
            }
        }
    }

    /**
     * 全部排期角色(需求侧有独立人员字段的角色)
     *
     * @return 角色数组
     */
    public static String[] allRoles() {
        return new String[]{ROLE_FRONTEND, ROLE_BACKEND, ROLE_QA, ROLE_OWNER};
    }

    /**
     * 按索引取该角色下某姓名对应的用户ID
     *
     * @param req  需求
     * @param role 角色
     * @param name 姓名
     * @return 用户ID; 无匹配/不可对齐/该位未匹配到账号时返回 null(调用方应降级为按姓名)
     */
    public static String personIdForName(PmRequirement req, String role, String name) {
        // 全栈岗位无独立人员字段: 该人同时登记在前端与后端名单中, 归并两侧解析结果
        if (ROLE_FULLSTACK.equals(role)) {
            return fullstackPersonId(req, name);
        }
        return personIdForSingleRole(req, role, name);
    }

    /**
     * 单角色的「姓名 → 用户ID」解析(严格按索引对齐)
     */
    private static String personIdForSingleRole(PmRequirement req, String role, String name) {
        List<String> names = PersonnelNameUtils.parsePersonnelNames(namesOfRole(req, role));
        List<String> ids = parseIds(idsOfRole(req, role));
        // 安全阀: 长度不一致说明两套字段已不同步(如只改了姓名没同步ID), 拒绝猜测
        if (ids.isEmpty() || ids.size() != names.size()) {
            return null;
        }
        int idx = names.indexOf(name);
        if (idx < 0) {
            return null;
        }
        String id = ids.get(idx);
        return (id == null || id.isBlank()) ? null : id;
    }

    /**
     * 全栈岗位的用户ID: 归并前端与后端两侧对该姓名的解析结果
     * <p>
     * 目的: 同一人若在「纯前端/纯后端」需求里已账号化(走 userId 键), 而在全栈需求里因拿不到ID退回姓名键,
     * 容量日历会把他拆成两个互不干涉的个体 —— 同一天可被重复排满 2 人天。这里补齐即可让两者收敛为同一身份。
     * <p>
     * 仍遵循上层「安全优先、绝不猜测」原则: 两侧都命中但结果不一致(两套字段不同步的脏数据)时返回 null,
     * 由调用方退回按姓名处理, 而非武断取一侧。
     */
    private static String fullstackPersonId(PmRequirement req, String name) {
        String feId = personIdForSingleRole(req, ROLE_FRONTEND, name);
        String beId = personIdForSingleRole(req, ROLE_BACKEND, name);
        boolean feOk = feId != null && !feId.isBlank();
        boolean beOk = beId != null && !beId.isBlank();
        if (feOk && beOk && !feId.equals(beId)) {
            return null;
        }
        return feOk ? feId : (beOk ? beId : null);
    }

    /**
     * 排期身份主键: personId 优先, 为空时退回姓名(兜底)
     *
     * @param personId   用户ID
     * @param personName 姓名
     * @return 身份主键
     */
    public static String identityKey(String personId, String personName) {
        return (personId != null && !personId.isBlank()) ? personId : personName;
    }

    /**
     * 按「姓名 → 用户ID」映射为新姓名序列生成对齐的ID数组
     * <p>
     * 用于 reassign 等"在旧名单基础上增删成员"的场景: 留任成员沿用其原有ID(不重新解析, 避免同名歧义),
     * 新加入成员取传入的新ID(可能为 null), 从而保证ID数组始终与姓名串同序、不会错位挂到别人名下。
     *
     * @param names     新的姓名序列(顺序即对齐基准)
     * @param nameToId  姓名→用户ID映射(可用 {@link #nameToIdMap} 从旧需求构建, 再补充新成员)
     * @return 与 names 等长的ID列表(查不到的位为 null)
     */
    public static List<String> idsForNames(List<String> names, Map<String, String> nameToId) {
        if (names == null || names.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> ids = new ArrayList<>(names.size());
        for (String n : names) {
            String id = nameToId == null ? null : nameToId.get(n);
            ids.add((id == null || id.isBlank()) ? null : id);
        }
        return ids;
    }

    /**
     * 将外部提交的用户ID列表与姓名列表按索引对齐
     * <p>
     * 用于批量指派等"一次性整体覆盖某角色人员"的场景: 姓名串是唯一真源, ID列表必须与之长度一致才可信。
     * 长度不一致(前端漏传/旧版前端/占位名参与)时整组判为不可对齐, 返回空 → 该角色退回按姓名兜底,
     * 绝不按下标猜测, 避免把排期静默挂到另一个人头上。
     *
     * @param names 已解析的姓名列表(顺序即对齐基准)
     * @param ids   外部提交的用户ID列表(可为 null; 允许 null 元素表示该位未匹配到账号)
     * @return 与 names 等长的ID列表(不可对齐时返回空列表)
     */
    public static List<String> alignIds(List<String> names, List<String> ids) {
        if (names == null || names.isEmpty() || ids == null || ids.size() != names.size()) {
            return new ArrayList<>();
        }
        List<String> aligned = new ArrayList<>(names.size());
        for (int i = 0; i < names.size(); i++) {
            String id = ids.get(i);
            aligned.add((id == null || id.isBlank()) ? null : id);
        }
        return aligned;
    }
}

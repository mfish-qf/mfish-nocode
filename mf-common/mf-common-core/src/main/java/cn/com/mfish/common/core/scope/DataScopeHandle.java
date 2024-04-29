package cn.com.mfish.common.core.scope;

/**
 * @description: 数据范围处理
 * @author: mfish
 * @date: 2024/4/26
 */
public interface DataScopeHandle {
    String buildCondition(String fieldName, String[] values);
}

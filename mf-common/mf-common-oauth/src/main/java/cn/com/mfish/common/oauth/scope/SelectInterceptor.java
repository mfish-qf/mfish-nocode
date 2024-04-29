package cn.com.mfish.common.oauth.scope;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.oauth.annotation.DataScope;
import cn.com.mfish.common.oauth.common.DataScopeUtils;
import cn.com.mfish.common.oauth.common.DataScopeValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 查询拦截器
 * @author: mfish
 * @date: 2024/4/25
 */
@Component
@Intercepts(
        {@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})}
)
@Slf4j
public class SelectInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List<DataScope> scopes = DataScopeUtils.context.get();
        if (scopes == null) {
            return invocation.proceed();
        }
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject
                .forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                        new DefaultReflectorFactory());
        // 获取mappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        SqlCommandType sqlType = mappedStatement.getSqlCommandType();
        if (!SqlCommandType.SELECT.equals(sqlType)) {
            return invocation.proceed();
        }
        BoundSql boundSql = statementHandler.getBoundSql();
        String oriSql = boundSql.getSql();
        try {
            Map<String, List<DataScopeValue>> map = buildParamsMap(scopes);
            oriSql = MixDataScopeHandle.sqlChange(oriSql, map);
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, oriSql);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MyRuntimeException("异常：数据权限过滤出错");
        }
        return invocation.proceed();
    }

    private Map<String, List<DataScopeValue>> buildParamsMap(List<DataScope> scopes) {
        Map<String, List<DataScopeValue>> map = new HashMap<>();
        for (DataScope scope : scopes) {
            String tableName = scope.table();
            List<DataScopeValue> list;
            if (map.containsKey(tableName)) {
                list = map.get(tableName);
            } else {
                list = new ArrayList<>();
            }
            list.add(new DataScopeValue().setDataScopeHandle(scope.type().getHandle()).setFieldName(scope.fieldName()).setValues(scope.values()));
            map.put(tableName, list);
        }
        return map;
    }
}

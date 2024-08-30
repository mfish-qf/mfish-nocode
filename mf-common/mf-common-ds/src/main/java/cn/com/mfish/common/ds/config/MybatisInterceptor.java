package cn.com.mfish.common.ds.config;

import cn.com.mfish.common.core.entity.BaseEntity;
import cn.com.mfish.common.core.utils.AuthInfoUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: mfish
 * @description: mybatis拦截设置默认值
 * @date: 2022/11/4 15:17
 */
@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List<?> parameters = getParameter(invocation);
        if (parameters == null) {
            return invocation.proceed();
        }
        //不支持异步调用获取当前帐号
        String account = AuthInfoUtils.getCurrentAccount();
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        setParam(mappedStatement, account, parameters);
        return invocation.proceed();
    }

    private void setParam(MappedStatement mappedStatement, String account, List<?> list) {
        for (Object entity : list) {
            if (!(entity instanceof BaseEntity)) {
                continue;
            }
            BaseEntity<?> parameter = (BaseEntity<?>) entity;
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            switch (sqlCommandType) {
                case INSERT:
                    if (!StringUtils.isEmpty(account)) {
                        parameter.setCreateBy(account);
                    }
                    parameter.setCreateTime(new Date());
                case UPDATE:
                    if (!StringUtils.isEmpty(account)) {
                        parameter.setUpdateBy(account);
                    }
                    parameter.setUpdateTime(new Date());
                    break;
            }
        }
    }

    /**
     * 获取请求参数
     * 该方法旨在从MyBatis的调用对象中提取参数，参数可能为单个对象、Map或List类型
     * 如果参数是Map类型且包含特定键（如"et", "param1", "list"），则优先返回这些键对应的值
     * 如果参数是BaseEntity的实例，则将其封装为List返回
     * 如果参数直接是一个List，或者符合特定条件的Map或对象，则直接返回该参数
     *
     * @param invocation 当前的调用对象，包含了执行方法所需的所有参数
     * @return 可能是封装了参数的List，也可能是null，具体取决于输入参数的类型和内容
     */
    private List<?> getParameter(Invocation invocation) {
        Object parameter = invocation.getArgs()[1];
        if (parameter instanceof MapperMethod.ParamMap<?> p) {
            if (p.containsKey("et")) {
                parameter = p.get("et");
            } else if (p.containsKey("param1")) {
                parameter = p.get("param1");
            } else if (p.containsKey("list")) {
                parameter = p.get("list");
            }
        }
        if (parameter instanceof BaseEntity) {
            List<Object> list = new ArrayList<>();
            list.add(parameter);
            return list;
        }
        if (parameter instanceof List) {
            return (List<?>) parameter;
        }
        return null;
    }
}

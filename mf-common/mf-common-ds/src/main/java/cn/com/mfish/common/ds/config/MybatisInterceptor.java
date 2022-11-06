package cn.com.mfish.common.ds.config;

import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.constants.HttpStatus;
import cn.com.mfish.common.core.utils.Utils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.ds.common.Constant;
import cn.com.mfish.oauth.api.entity.UserInfo;
import cn.com.mfish.oauth.api.remote.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * @author ：qiufeng
 * @description：mybatis拦截设置默认值
 * @date ：2022/11/4 15:17
 */
@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisInterceptor implements Interceptor {

    @Resource
    RemoteUserService remoteUserService;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameter = invocation.getArgs()[1];
        if (parameter == null) {
            return invocation.proceed();
        }
        List<Field> list = Utils.getAllFields(parameter);
        Result<UserInfo> result = remoteUserService.getUserInfo(CredentialConstants.INNER);
        if (result == null || HttpStatus.SUCCESS != result.getCode()) {
            log.warn("保存信息时未取到用户信息!" + result.getMsg());
            return invocation.proceed();
        }
        switch (sqlCommandType) {
            case INSERT:
                for (Field field : list) {
                    if (Constant.CREATE_BY.equals(field.getName())) {
                        setFiledValue(field, parameter, result.getData().getAccount());
                    }
                    if (Constant.CREATE_TIME.equals(field.getName())) {
                        setFiledValue(field, parameter, new Date());
                    }
                }
                break;
            case UPDATE:
                for (Field field : list) {
                    if (Constant.UPDATE_BY.equals(field.getName())) {
                        setFiledValue(field, parameter, result.getData().getAccount());
                    }
                    if (Constant.UPDATE_TIME.equals(field.getName())) {
                        setFiledValue(field, parameter, new Date());
                    }
                }
        }
        return invocation.proceed();
    }

    private void setFiledValue(Field field, Object obj, Object value) {
        try {
            field.setAccessible(true);
            Object localCreateDate = field.get(obj);
            if (ObjectUtils.isEmpty(localCreateDate)) {
                field.set(obj, value);
            }
        } catch (IllegalAccessException e) {
            log.error("设置字段值异常", e);
        } finally {
            field.setAccessible(false);
        }

    }
}

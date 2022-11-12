package cn.com.mfish.common.ds.config;

import cn.com.mfish.common.core.constants.CredentialConstants;
import cn.com.mfish.common.core.constants.HttpStatus;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.ds.entity.BaseEntity;
import cn.com.mfish.oauth.api.entity.UserInfo;
import cn.com.mfish.oauth.api.remote.RemoteUserService;
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

import javax.annotation.Resource;
import java.util.Date;

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
        BaseEntity parameter = getParameter(invocation);
        if (parameter == null) {
            return invocation.proceed();
        }
        Result<UserInfo> result = remoteUserService.getUserInfo(CredentialConstants.INNER);
        if (result == null || HttpStatus.SUCCESS != result.getCode()) {
            log.warn("保存信息时未取到用户信息!" + result.getMsg());
            return invocation.proceed();
        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        switch (sqlCommandType) {
            case INSERT:
                parameter.setCreateBy(result.getData().getAccount());
                parameter.setCreateTime(new Date());
                break;
            case UPDATE:
                parameter.setUpdateBy(result.getData().getAccount());
                parameter.setUpdateTime(new Date());
                break;
        }
        return invocation.proceed();
    }

    /**
     * 获取请求参数
     *
     * @param invocation
     * @return
     */
    private BaseEntity getParameter(Invocation invocation) {
        Object parameter = invocation.getArgs()[1];
        if (parameter instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap<?> p = (MapperMethod.ParamMap<?>) parameter;
            if (p.containsKey("et")) {
                parameter = p.get("et");
            } else {
                parameter = p.get("param1");
            }
        }
        if (parameter instanceof BaseEntity) {
            return (BaseEntity) parameter;
        }
        return null;
    }
}

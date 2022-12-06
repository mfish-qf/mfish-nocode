package cn.com.mfish.oauth.validator;

import cn.com.mfish.common.core.utils.SpringBeanFactory;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.entity.OAuthClient;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiufeng
 * @date 2020/2/13 14:41
 */
@Data
public abstract class MultipleValidator {

    List<Class<? extends IBaseValidator<OAuthClient>>> validateClientList = new ArrayList<>();

    /**
     * 客户端参数相关多个校验
     *
     * @param request
     * @param result
     * @return
     */
    public Result<OAuthClient> validateClient(HttpServletRequest request, Result<OAuthClient> result) {
        return validate(request, result, validateClientList);
    }

    public <T> Result<T> validate(HttpServletRequest request, Result<T> result, List<Class<? extends IBaseValidator<T>>> list) {
        for (Class<? extends IBaseValidator<T>> validator : list) {
            result = SpringBeanFactory.getBean(validator)
                    .validate(request, result);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return result;
    }
}

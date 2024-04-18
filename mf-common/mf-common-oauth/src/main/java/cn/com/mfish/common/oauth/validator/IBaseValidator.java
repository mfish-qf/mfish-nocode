package cn.com.mfish.common.oauth.validator;

import cn.com.mfish.common.core.web.Result;
import jakarta.servlet.http.HttpServletRequest;


/**
 * @author: mfish
 * @date: 2020/2/13 13:43
 */
public interface IBaseValidator<T> {
    Result<T> validate(HttpServletRequest request, Result<T> result);
}

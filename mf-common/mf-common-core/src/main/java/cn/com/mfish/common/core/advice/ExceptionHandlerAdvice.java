package cn.com.mfish.common.core.advice;

import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.web.AjaxTResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

/**
 * @author ：qiufeng
 * @description：oauth全局异常处理
 * @date ：2021/12/13 18:06
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
    /**
     * 认证异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(OAuthValidateException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AjaxTResult exception(OAuthValidateException exception) {
        log.error("401校验异常", exception);
        return AjaxTResult.fail(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
    }

    /**
     * 禁止访问异常
     * @param exception
     * @return
     */
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AjaxTResult badRequestException(AccessDeniedException exception) {
        log.error("403异常", exception);
        return AjaxTResult.fail(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    /**
     * 请求错误异常
     * @param exception
     * @return
     */
    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class
            , HttpMessageNotReadableException.class, UnsatisfiedServletRequestParameterException.class
            , MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AjaxTResult badRequestException(Exception exception) {
        log.error("400异常", exception);
        return AjaxTResult.fail(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    /**
     * 服务内部异常
     *
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxTResult handleException(Exception exception, HttpServletRequest request) {
        log.error("请求地址'{}',发生系统异常.", request.getRequestURI(), exception);
        return AjaxTResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }
}
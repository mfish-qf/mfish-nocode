package cn.com.mfish.common.core.advice;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

/**
 * @author: mfish
 * @description: WebFlux全局异常处理（与ExceptionHandlerAdvice对偶，仅Reactive环境生效）
 * <p>
 * Servlet环境由 {@link ExceptionHandlerAdvice} 处理；
 * WebFlux环境下切面（@Before/@Around）抛出的异常需要此处兜底，否则返回默认HTML错误页。
 * </p>
 * @date: 2026/07/14
 */
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Slf4j
public class ReactiveExceptionHandlerAdvice {

    @ExceptionHandler(OAuthValidateException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Integer> handleOAuthValidateException(OAuthValidateException exception) {
        log.error("401校验异常", exception);
        return Result.fail(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Integer> handleAccessDeniedException(AccessDeniedException exception) {
        log.error("403异常", exception);
        return Result.fail(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    @ExceptionHandler(MyRuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Integer> handleMyRuntimeException(MyRuntimeException exception) {
        log.error("业务异常", exception);
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Integer> handleException(Exception exception) {
        log.error("系统异常", exception);
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "错误:未知异常");
    }
}

package cn.com.mfish.common.core.advice;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.web.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;

/**
 * @author: mfish
 * @description: 全局异常处理
 * @date: 2021/12/13 18:06
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
    /**
     * 处理OAuth验证异常
     * <p>
     * 当OAuth验证过程中出现异常时，该方法会被调用以处理异常情况
     * 它记录异常信息并返回一个包含HTTP状态码和异常信息的响应对象
     *
     * @param exception 异常对象，类型为OAuthValidateException
     * @return 返回一个Result类型对象，其中包含一个状态码和异常信息
     */
    @ExceptionHandler(OAuthValidateException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Integer> exception(OAuthValidateException exception) {
        log.error("401校验异常", exception);
        return Result.fail(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
    }

    /**
     * 处理访问被拒绝异常的控制器方法
     * 当应用程序中抛出AccessDeniedException异常时，此方法将被调用
     * 它记录异常并返回一个包含403状态码和异常信息的响应体，表示访问被禁止
     *
     * @param exception AccessDeniedException类型的异常对象，表示访问被拒绝
     * @return 返回一个Result对象，其中包含HTTP状态码403和异常信息
     */
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Integer> badRequestException(AccessDeniedException exception) {
        log.error("403异常", exception);
        return Result.fail(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }

    /**
     * 请求错误异常处理器
     * <p>
     * 该方法用于处理请求中出现的各种参数和消息相关的异常。它处理的异常包括：
     * - IllegalArgumentException：非法参数异常。
     * - MissingServletRequestParameterException：请求缺少必需的参数。
     * - HttpMessageNotReadableException：HTTP消息无法读取，比如JSON格式错误。
     * - UnsatisfiedServletRequestParameterException：请求参数未满足需求。
     * - MethodArgumentTypeMismatchException：方法参数类型不匹配。
     * <p>
     * 这些异常通常由于客户端请求不当引起，因此返回400 Bad Request状态码。
     *
     * @param exception 抛出的异常对象，这里的方法会根据不同的异常类型，生成相同的响应状态码，但带有不同错误信息的响应。
     * @return 返回一个Result对象，其中包含失败的状态码和异常信息。状态码和错误信息帮助客户端理解请求失败的原因。
     */
    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestParameterException.class
            , HttpMessageNotReadableException.class, UnsatisfiedServletRequestParameterException.class
            , MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Integer> badRequestException(Exception exception) {
        log.error("400异常", exception);
        return Result.fail(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    /**
     * 自定义异常处理方法，专门处理{@link MyRuntimeException}异常
     * 当控制器中的方法抛出{@link MyRuntimeException}异常时，这个方法会被调用
     * 它返回一个包含错误代码和错误消息的响应体，统一处理异常，对客户端提供一致的错误信息
     * 同时，该方法会记录错误日志，方便开发人员定位问题
     *
     * @param exception 异常对象，被处理的异常
     * @param request   请求对象，用于获取请求的相关信息，比如请求URI
     * @return 返回一个Result对象，其中包含错误代码和错误消息
     */
    @ExceptionHandler(MyRuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Integer> myHandleException(Exception exception, HttpServletRequest request) {
        log.error("请求地址'{}',处理异常.", request.getRequestURI(), exception);
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }

    /**
     * 服务内部异常处理器
     * 用于捕获和处理服务内部出现的异常，将其转换为统一的响应格式返回
     *
     * @param exception   服务内部捕获的异常对象
     * @param request     异常发生的HTTP请求对象
     * @return 返回一个封装了错误码和错误信息的结果对象，用于向客户端反馈异常信息
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Integer> handleException(Exception exception, HttpServletRequest request) {
        log.error("请求地址'{}',发生系统异常.", request.getRequestURI(), exception);
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "错误:未知异常");
    }
}
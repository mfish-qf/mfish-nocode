package cn.com.mfish.common.oauth.aspect;

import cn.com.mfish.common.core.annotation.GlobalException;
import cn.com.mfish.common.core.annotation.InnerUser;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.utils.ServletUtils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.utils.http.WebRequest;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.validator.TokenValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author: mfish
 * @description: 内部用户校验切面（兼容Servlet与WebFlux双栈，支持Mono/Flux响应式返回值）
 * <p>
 * 从 mf-common-web 迁移至 mf-common-oauth，使 WebFlux 服务（如 mf-ai，排除了 mf-common-web）
 * 也能加载本切面，避免 @InnerUser 接口鉴权失效。
 * </p>
 * <p>
 * 身份验证应优先于数据范围（DataScope）执行：无权限的请求直接拒绝，不进入数据权限处理。
 * </p>
 * <p>
 * WebFlux环境下，token校验内部的 tokenService.getToken() 是阻塞式Redis查询，
 * 不能在Netty IO线程执行。对Mono/Flux返回值，用
 * Mono.defer(() -> { 校验; return mono; }).subscribeOn(boundedElastic) 推迟到弹性线程池。
 * </p>
 * <p>
 * 关键设计：用defer而非fromCallable+then（原因同AuthorizationAspect），
 * 确保与DataScope叠加时ThreadLocal传播不断链，且本切面可在DataScope外层执行。
 * </p>
 * @date: 2021/12/3 20:28
 */
@Aspect
@Component
@GlobalException
public class InnerUserAspect {
    @Resource
    TokenValidator tokenValidator;

    @Around("@annotation(innerUser)")
    public Object innerAround(ProceedingJoinPoint point, InnerUser innerUser) throws Throwable {
        // 双栈获取请求对象（Servlet优先，WebFlux回退）——仅读header，非阻塞，可在Netty线程执行
        Object request = resolveRequest();
        if (request == null) {
            throw new MyRuntimeException("错误:未获取到请求信息");
        }
        String source = WebRequest.getHeader(request, RPCConstants.REQ_ORIGIN);
        if (StringUtils.isEmpty(source)) {
            throw new OAuthValidateException("错误:内部接口禁止外部直接访问");
        }
        // 内部请求验证（仅读header，非阻塞）
        if (RPCConstants.INNER.equals(source) && !innerUser.validateUser()) {
            return point.proceed();
        }

        Object result = point.proceed();
        // 响应式返回值：将token校验推迟到boundedElastic线程执行
        // 关键：用defer而非fromCallable+then，确保token校验与业务Mono在同一个subscribeOn调度内执行
        // .then()会在上游Mono完成后才订阅下游，此时wrapper的finally已清理dataScope，导致断链
        // defer在订阅时同步执行lambda，校验和业务Mono订阅在同一个wrapper.run()内完成，finally不会在中间执行
        if (result instanceof Mono<?> mono) {
            return Mono.defer(() -> {
                        validateToken(request);
                        return mono;
                    })
                    .subscribeOn(Schedulers.boundedElastic());
        }
        if (result instanceof Flux<?> flux) {
            return Flux.defer(() -> {
                        validateToken(request);
                        return flux;
                    })
                    .subscribeOn(Schedulers.boundedElastic());
        }
        // 同步路径（Servlet环境）：直接校验
        validateToken(request);
        return result;
    }

    /**
     * 执行token校验（阻塞调用，必须在boundedElastic线程执行）
     */
    private void validateToken(Object request) {
        Result<?> result = tokenValidator.validator(request);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
    }

    /**
     * 双栈解析当前请求对象
     * Servlet环境返回 HttpServletRequest，WebFlux环境返回 ServerHttpRequest
     */
    private Object resolveRequest() {
        HttpServletRequest servletRequest = ServletUtils.getRequest();
        if (servletRequest != null) {
            return servletRequest;
        }
        ServerHttpRequest serverHttpRequest = ServletUtils.getServerHttpRequest();
        if (serverHttpRequest != null) {
            return serverHttpRequest;
        }
        return null;
    }
}

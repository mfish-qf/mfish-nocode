package cn.com.mfish.common.core.utils;

import cn.com.mfish.common.core.web.Result;
import com.alibaba.fastjson2.JSON;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author: mfish
 * @description: Feign降级异常信息解析工具
 * <p>
 * 微服务场景下，远程服务抛出业务异常（如权限校验失败 MyRuntimeException）后，
 * 全局异常处理器将其转换为 HTTP 500 + JSON body（Result.fail 的 JSON 形式）。
 * Feign 客户端收到非 2xx 状态码会抛出 FeignException，其 content() 包含响应体。
 * </p>
 * <p>
 * 但 FallbackFactory 通常会丢弃 cause，返回自定义的通用提示（如"查询失败"），
 * 导致真实业务异常信息（如"错误:该用户无此操作权限"）丢失，LLM 无法透传给用户。
 * </p>
 * <p>
 * 本工具从 FeignException 的响应体中解析出原始 Result.msg，供 fallback 使用：
 * <pre>
 * &#64;Override
 * public RemoteXxxService create(Throwable cause) {
 *     log.error("错误:xxx调用异常", cause);
 *     String msg = FeignFallbackHelper.resolveErrorMsg(cause, "查询xxx失败");
 *     return new RemoteXxxService() {
 *         &#64;Override
 *         public Result&lt;?&gt; query(String origin, ...) {
 *             return Result.fail(msg);
 *         }
 *     };
 * }
 * </pre>
 * </p>
 * @date: 2026/07/15
 */
@Slf4j
public class FeignFallbackHelper {

    private FeignFallbackHelper() {
    }

    /**
     * 从 Feign 降级异常中解析真实的业务错误消息
     * <p>
     * 解析顺序：
     * 1. 若 cause 是 FeignException，从其 content()（HTTP 响应体）解析 Result JSON，取 msg
     * 2. 若解析失败或 msg 为空，回退到 cause.getMessage()
     * 3. 若仍为空，返回 defaultMsg
     *
     * @param cause      FallbackFactory.create(Throwable cause) 中的 cause
     * @param defaultMsg 兜底默认消息（如"查询字典项失败"）
     * @return 真实的业务错误消息
     */
    public static String resolveErrorMsg(Throwable cause, String defaultMsg) {
        if (cause == null) {
            return defaultMsg;
        }
        // 1. 从 FeignException 的响应体中解析 Result.msg
        if (cause instanceof FeignException feignException) {
            String body = extractResponseBody(feignException);
            if (body != null && !body.isEmpty()) {
                try {
                    Result<?> result = JSON.parseObject(body, Result.class);
                    if (result != null && !StringUtils.isEmpty(result.getMsg())) {
                        return result.getMsg();
                    }
                } catch (Exception e) {
                    log.debug("[FeignFallback] 响应体非Result JSON格式: {}", body, e);
                }
            }
        }
        // 2. 回退到 cause.getMessage()
        if (!StringUtils.isEmpty(cause.getMessage())) {
            return cause.getMessage();
        }
        // 3. 兜底
        return defaultMsg;
    }

    /**
     * 从 FeignException 中提取 HTTP 响应体字符串
     */
    private static String extractResponseBody(FeignException feignException) {
        byte[] content = feignException.content();
        if (content == null || content.length == 0) {
            return null;
        }
        return new String(content, StandardCharsets.UTF_8);
    }
}

package cn.com.mfish.boot.controller;

import cn.com.mfish.common.ai.entity.AiRequest;
import cn.com.mfish.common.ai.entity.AiRouterVo;
import cn.com.mfish.common.ai.service.AiRouteService;
import cn.com.mfish.common.captcha.service.CheckCodeService;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.prom.annotation.MetricsMonitor;
import cn.com.mfish.common.prom.annotation.MetricsMonitors;
import cn.com.mfish.common.prom.enums.MetricEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @description: 单实例补充接口
 * @author: mfish
 * @date: 2024/1/26
 */
@Slf4j
@Tag(name = "单实例补充接口--获取验证码")
@RestController
public class BootController {
    @Resource
    CheckCodeService checkCodeService;
    @Resource
    AiRouteService aiRouteService;
    @Resource
    ObjectMapper objectMapper;

    @Operation(summary = "获取验证码", description = "获取验证码")
    @GetMapping("/captcha")
    public Result<Map<String, Object>> getCaptcha() {
        return checkCodeService.createCaptcha();
    }

    @Operation(summary = "未授权页面", description = "未授权页面")
    @GetMapping("/404")
    public Result<String> noAuth() {
        return Result.fail("错误:未授权，请联系管理员");
    }

    /**
     * AI智能路由（单实例模式）
     * 同步阻塞等待AI路由决策完成后，在原始请求线程上forward到目标Controller，
     * 避免异步处理上下文冲突（BootController的Mono异步与目标Controller的Flux SSE异步冲突）
     * forward使用缓存body的CachingRequestWrapper，确保目标@RequestBody能读取到完整请求体
     */
    @Operation(summary = "Ai路由", description = "Ai路由")
    @PostMapping("/aiRouter")
    @MetricsMonitors({
            @MetricsMonitor(metricEnum = MetricEnum.MFISH_REQUEST_COUNT, tagValues = {"POST", "/aiRouter"}),
            @MetricsMonitor(metricEnum = MetricEnum.MFISH_REQUEST_DURATION, tagValues = {"POST", "/aiRouter"})
    })
    public void aiRouter(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // 读取原始请求体并缓存
        byte[] bodyBytes = request.getInputStream().readAllBytes();
        AiRequest aiRequest = bodyBytes.length > 0
                ? objectMapper.readValue(bodyBytes, AiRequest.class)
                : new AiRequest();

        String sessionId = aiRequest.getSessionId();
        String prompt = aiRequest.getMessage() != null ? aiRequest.getMessage().getContent() : null;
        log.info("[AI路由-单实例] 接收到智能路由请求, sessionId={}, prompt={}", sessionId, prompt);

        // 同步阻塞等待路由决策完成
        AiRouterVo vo = aiRouteService.resolve(sessionId, prompt).block();
        if (vo == null) {
            vo = new AiRouterVo();
        }

        String targetPath = vo.getPath();
        log.info("[AI路由-单实例] 内部转发到, path={}", targetPath);

        // 用缓存body的wrapper包装request，forward到目标Controller
        CachingRequestWrapper cachedRequest = new CachingRequestWrapper(request, bodyBytes);
        request.getRequestDispatcher(targetPath).forward(cachedRequest, response);
    }

    /**
     * 缓存请求体的Request包装器
     * 解决Servlet输入流只能读取一次的问题
     */
    static class CachingRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] cachedBody;

        CachingRequestWrapper(HttpServletRequest request, byte[] cachedBody) {
            super(request);
            this.cachedBody = cachedBody;
        }

        @Override
        public ServletInputStream getInputStream() {
            return new CachedServletInputStream(cachedBody);
        }

        @Override
        public int getContentLength() {
            return cachedBody.length;
        }

        @Override
        public long getContentLengthLong() {
            return cachedBody.length;
        }
    }

    static class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        CachedServletInputStream(byte[] data) {
            this.inputStream = new ByteArrayInputStream(data);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("不支持异步读取");
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public int read(byte[] b, int off, int len) {
            return inputStream.read(b, off, len);
        }
    }
}

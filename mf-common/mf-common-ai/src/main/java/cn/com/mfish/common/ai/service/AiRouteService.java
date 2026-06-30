package cn.com.mfish.common.ai.service;

import cn.com.mfish.common.ai.agent.GatewayAssistant;
import cn.com.mfish.common.ai.entity.AiRouterVo;
import cn.com.mfish.common.core.constants.ServiceConstants;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * AI路由决策服务
 * 封装"LLM路由决策 + Caffeine缓存"的通用逻辑，
 * 微服务网关模式（AiRouteFilter）和单实例模式（BootController）共用
 *
 * @author: mfish
 * @date: 2025/8/22
 */
@Service
@Slf4j
public class AiRouteService {

    private final GatewayAssistant gatewayAssistant;

    /**
     * 路由缓存：相同prompt短时间内返回相同结果，避免重复调用LLM
     */
    private final Cache<String, AiRouterVo> routeCache = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

    public AiRouteService(GatewayAssistant gatewayAssistant) {
        this.gatewayAssistant = gatewayAssistant;
    }

    /**
     * AI路由决策：根据用户意图获取目标路由信息
     * 先查缓存，缓存未命中则调用LLM，结果写入缓存
     *
     * @param sessionId 会话ID
     * @param prompt    用户输入
     * @return 路由决策结果
     */
    public Mono<AiRouterVo> resolve(String sessionId, String prompt) {
        if (StringUtils.isEmpty(prompt) || prompt.isBlank()) {
            prompt = "介绍下摸鱼低代码";
        }
        if (StringUtils.isEmpty(sessionId)) {
            sessionId = "default";
        }

        // 先查缓存
        AiRouterVo cached = routeCache.getIfPresent(prompt);
        if (cached != null) {
            // 缓存结果也需校验，防止历史缓存中存在无效serviceId
            cached = validateAndFix(cached);
            log.info("[AI路由] 命中缓存, serviceId={}, path={}", cached.getServiceId(), cached.getPath());
            return Mono.just(cached);
        }

        // 调用LLM获取路由
        String finalPrompt = prompt;
        String finalSessionId = sessionId;
        return gatewayAssistant.chat(finalSessionId, finalPrompt)
                .map(result -> {
                    AiRouterVo vo = result.getData();
                    if (vo == null || vo.getPath() == null) {
                        log.warn("[AI路由] LLM返回空结果，使用默认路由");
                        vo = new AiRouterVo();
                    }
                    vo = validateAndFix(vo);
                    log.info("[AI路由] LLM决策结果, serviceId={}, path={}, name={}", vo.getServiceId(), vo.getPath(), vo.getName());
                    routeCache.put(finalPrompt, vo);
                    return vo;
                })
                .onErrorResume(ex -> {
                    log.error("[AI路由] LLM调用失败，使用默认路由", ex);
                    return Mono.just(new AiRouterVo());
                });
    }

    /**
     * 校验LLM返回的路由结果，修正无效的serviceId
     * LLM可能幻觉出不存在的服务名（如mf-ai），需要校验并回退到默认值
     */
    private AiRouterVo validateAndFix(AiRouterVo vo) {
        if (!ServiceConstants.MfService.isValid(vo.getServiceId())) {
            log.warn("[AI路由] LLM返回无效serviceId='{}'，不在有效枚举{}中，回退到默认值{}",
                    vo.getServiceId(), ServiceConstants.MfService.allServiceIds(), ServiceConstants.MfService.SYS.getValue());
            vo.setServiceId(ServiceConstants.MfService.SYS.getValue());
        }
        return vo;
    }
}

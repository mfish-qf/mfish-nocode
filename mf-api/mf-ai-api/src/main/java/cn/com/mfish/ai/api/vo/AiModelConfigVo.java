package cn.com.mfish.ai.api.vo;

import cn.com.mfish.ai.api.entity.AiModelConfig;

/**
 * @description: AI模型配置信息VO
 * @author: mfish
 * @date: 2026/7/8
 */
public class AiModelConfigVo extends AiModelConfig {
    public String getApiKey() {
        String apiKey = super.getApiKey();
        if (apiKey == null) {
            return null;
        }
        if (apiKey.length() <= 6) {
            return apiKey.charAt(0) + "****";
        }
        return apiKey.substring(0, 3) + "****" + apiKey.substring(apiKey.length() - 3);
    }
}

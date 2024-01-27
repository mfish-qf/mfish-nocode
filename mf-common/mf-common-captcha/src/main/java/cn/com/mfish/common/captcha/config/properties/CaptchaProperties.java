package cn.com.mfish.common.captcha.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author: mfish
 * @description: 验证码属性
 * @date: 2021/8/12 15:40
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.captcha")
@Data
public class CaptchaProperties {
    /**
     * 验证码开关
     */
    private Boolean enabled = true;

    /**
     * 验证码类型（math 数组计算 char 字符）
     */
    private CaptchaType type;

    /**
     * 网关校验后直接返回处理结果
     */
    private String[] gatewayCheckCaptcha;
    /**
     * 网关校验后将结果存入参数中由自己服务处理
     */
    private String[] selfCheckCaptcha;

    public void setType(String type) {
        this.type = CaptchaType.getCaptchaType(type);
    }

    public enum CaptchaType {
        计算("math"),
        字符("char");
        private final String captchaType;

        CaptchaType(String type) {
            this.captchaType = type;
        }

        @Override
        public String toString() {
            return this.captchaType;
        }

        public static CaptchaType getCaptchaType(String value) {
            for (CaptchaType type : CaptchaType.values()) {
                if (type.toString().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return CaptchaType.计算;
        }
    }
}

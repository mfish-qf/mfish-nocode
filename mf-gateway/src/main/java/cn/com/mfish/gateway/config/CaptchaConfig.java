package cn.com.mfish.gateway.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static com.google.code.kaptcha.Constants.*;

/**
 * 验证码配置
 *
 * @author qiufeng
 * @date 2021/8/12 15:41
 */
@Configuration
public class CaptchaConfig {
    @Bean(name = "charCaptchaProducer")
    public DefaultKaptcha charCaptchaProducer() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = createDefaultProperties();
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "charCaptchaCode");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    @Bean(name = "mathCaptchaProducer")
    public DefaultKaptcha mathCaptchaProducer() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = createDefaultProperties();

        // 缓存key
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "mathCaptchaCode");
        properties.setProperty(KAPTCHA_TEXTPRODUCER_IMPL, "cn.com.mfish.gateway.config.MathCaptchaCreate");
        // 干扰实现类
        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    private Properties createDefaultProperties() {
        Properties properties = new Properties();
        // 是否有边框 默认为true 我们可以自己设置yes，no
        properties.setProperty(KAPTCHA_BORDER, "yes");
        properties.setProperty(KAPTCHA_BORDER_THICKNESS,"3");
        // 边框颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_BORDER_COLOR, "204,204,204");
        // 验证码文本字符颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");
        // 验证码图片宽度 默认为200
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
        // 验证码图片高度 默认为50
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
        // 验证码文本字符大小 默认为40
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "40");
        // 缓存key
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "captchaCode");
        // 验证码文本字符长度 默认为5
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "5");
        // 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "宋体,楷体,微软雅黑");
        //图文样式
        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        //渐变色
        properties.setProperty(KAPTCHA_BACKGROUND_CLR_FROM,"204,204,204");
        properties.setProperty(KAPTCHA_BACKGROUND_CLR_TO,"white");
        return properties;
    }
}

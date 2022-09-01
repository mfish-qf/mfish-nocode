package cn.com.mfish.gateway.service.impl;

import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.exception.CaptchaException;
import cn.com.mfish.common.core.utils.Base64;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.gateway.config.properties.CaptchaProperties;
import cn.com.mfish.gateway.service.CheckCodeService;
import com.google.code.kaptcha.Producer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author qiufeng
 * @description：验证码服务
 * @date 2021/8/12 11:41
 */
@Service
public class CheckCodeServiceImpl implements CheckCodeService {
    @Resource(name = "charCaptchaProducer")
    private Producer charCaptchaProducer;
    @Resource(name = "mathCaptchaProducer")
    private Producer mathCaptchaProducer;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CaptchaProperties captchaProperties;

    @Override
    public Result<Map<String, Object>> createCaptcha() {
        Result<Map<String, Object>> ajax = Result.ok(new HashMap());
        boolean captchaOnOff = captchaProperties.getEnabled();
        ajax.getData().put("captchaOnOff", captchaOnOff);
        if (!captchaOnOff) {
            return ajax;
        }
        String code, value;
        BufferedImage img;
        switch (captchaProperties.getType()) {
            case 计算:
                String capText = mathCaptchaProducer.createText();
                String[] caps = capText.split("#");
                code = caps[0];
                value = caps[1];
                img = mathCaptchaProducer.createImage(code);
                break;
            default:
                code = value = charCaptchaProducer.createText();
                img = charCaptchaProducer.createImage(code);
                break;
        }
        String uuid = UUID.randomUUID().toString();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        stringRedisTemplate.opsForValue().set(verifyKey,value,Constants.CAPTCHA_EXPIRE,TimeUnit.MINUTES);
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", os);
        } catch (IOException e) {
            return Result.fail(e.getMessage());
        }
        ajax.getData().put("captchaKey", uuid);
        ajax.getData().put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    @Override
    public void checkCaptcha(String code, String uuid) {
        if (StringUtils.isEmpty(code)) {
            throw new CaptchaException("错误:验证码不能为空");
        }
        if (StringUtils.isEmpty(uuid)) {
            throw new CaptchaException("错误:验证码已失效");
        }
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = stringRedisTemplate.opsForValue().get(verifyKey);
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException("错误:验证码不正确");
        }
        stringRedisTemplate.delete(verifyKey);
    }
}

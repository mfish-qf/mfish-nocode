package cn.com.mfish.common.captcha.service.impl;

import cn.com.mfish.common.captcha.common.CaptchaConstant;
import cn.com.mfish.common.captcha.config.properties.CaptchaProperties;
import cn.com.mfish.common.captcha.service.CheckCodeService;
import cn.com.mfish.common.core.constants.Constants;
import cn.com.mfish.common.core.exception.CaptchaException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: mfish
 * @description: 验证码服务
 * @date: 2021/8/12 11:41
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
        Result<Map<String, Object>> ajax = Result.ok(new HashMap<>());
        boolean captchaOnOff = captchaProperties.getEnabled();
        ajax.getData().put(CaptchaConstant.CHECK_ON_OFF, captchaOnOff);
        if (!captchaOnOff) {
            return ajax;
        }
        String code, value;
        BufferedImage img;
        if (captchaProperties.getType() == CaptchaProperties.CaptchaType.计算) {
            String capText = mathCaptchaProducer.createText();
            String[] caps = capText.split("#");
            code = caps[0];
            value = caps[1];
            img = mathCaptchaProducer.createImage(code);
        } else {
            code = value = charCaptchaProducer.createText();
            img = charCaptchaProducer.createImage(code);
        }
        String uuid = UUID.randomUUID().toString();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        stringRedisTemplate.opsForValue().set(verifyKey, value, Constants.CAPTCHA_EXPIRE, TimeUnit.MINUTES);
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", os);
        } catch (IOException e) {
            return Result.fail(e.getMessage());
        }
        ajax.getData().put(CaptchaConstant.CAPTCHA_KEY, uuid);
        ajax.getData().put("img", Base64.getEncoder().encodeToString(os.toByteArray()));
        return ajax;
    }

    @Override
    public void checkCaptcha(String code, String uuid) {
        if (code == null || StringUtils.isEmpty(code.trim())) {
            throw new CaptchaException(CaptchaException.Info.NULL.getName());
        }
        if (StringUtils.isEmpty(uuid)) {
            throw new CaptchaException(CaptchaException.Info.TIMEOUT.getName());
        }
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = stringRedisTemplate.opsForValue().get(verifyKey);
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException(CaptchaException.Info.ERROR.getName());
        }
        stringRedisTemplate.delete(verifyKey);
    }
}

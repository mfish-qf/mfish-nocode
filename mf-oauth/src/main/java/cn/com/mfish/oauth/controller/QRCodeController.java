package cn.com.mfish.oauth.controller;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.exception.OAuthValidateException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.oauth.common.CheckWithResult;
import cn.com.mfish.oauth.common.SerConstant;
import cn.com.mfish.oauth.model.QRCode;
import cn.com.mfish.oauth.model.QRCodeImg;
import cn.com.mfish.oauth.model.RedisQrCode;
import cn.com.mfish.oauth.model.WeChatToken;
import cn.com.mfish.oauth.service.QRCodeService;
import cn.com.mfish.oauth.validator.WeChatTokenValidator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;
import java.util.Hashtable;
import java.util.UUID;

/**
 * @author qiufeng
 * @date 2020/3/5 14:54
 */
@RestController
@Slf4j
@RequestMapping("/qrCodeLogin")
public class QRCodeController {
    @Resource
    QRCodeService qrCodeService;
    @Resource
    WeChatTokenValidator weChatTokenValidator;

    @ApiOperation("生成二维码")
    @GetMapping("/build")
    public Result<QRCodeImg> buildQRCode() {
        String error = "错误:生成二维码异常!";
        try {
            Hashtable<EncodeHintType, Comparable> hints = new Hashtable<>();
            //指定二维码字符集编码
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //设置二维码纠错等级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            //设置图片边距
            hints.put(EncodeHintType.MARGIN, 2);
            String code = UUID.randomUUID().toString();
            BitMatrix matrix = new MultiFormatWriter().encode(code,
                    BarcodeFormat.QR_CODE, 250, 250, hints);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            if (ImageIO.write(bufferedImage, "png", byteArrayOutputStream)) {
                RedisQrCode qrCode = saveQRCode(code);
                return Result.ok(buildResponseCode(qrCode, byteArrayOutputStream));
            }
            throw new MyRuntimeException(error);
        } catch (WriterException | IOException e) {
            log.error(error, e);
            throw new MyRuntimeException(error);
        }
    }

    /**
     * 保存二维码
     *
     * @param code
     * @return
     */
    private RedisQrCode saveQRCode(String code) {
        RedisQrCode qrCode = new RedisQrCode();
        qrCode.setCode(code);
        qrCode.setStatus(SerConstant.ScanStatus.未扫描.toString());
        qrCodeService.saveQRCode(qrCode);
        return qrCode;
    }

    /**
     * 构建返回code
     *
     * @param qrCode
     * @param byteArrayOutputStream
     * @return
     */
    private QRCodeImg buildResponseCode(QRCode qrCode, ByteArrayOutputStream byteArrayOutputStream) {
        QRCodeImg qrCodeImg = new QRCodeImg();
        qrCodeImg.setImg("data:image/png;base64," + Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
        qrCodeImg.setCode(qrCode.getCode());
        qrCodeImg.setStatus(qrCode.getStatus());
        return qrCodeImg;
    }

    @ApiOperation("检测扫码登录状态")
    @GetMapping("/check")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "二维码生成的code值", paramType = "query", required = true)
    })
    public Result<QRCode> qrCodeLoginCheck(String code) throws InvocationTargetException, IllegalAccessException {
        RedisQrCode redisQrCode = qrCodeService.checkQRCode(code);
        if (redisQrCode == null) {
            return Result.ok(null, "未检测到扫码状态");
        }
        QRCode qrCode = new QRCode();
        BeanUtils.copyProperties(qrCode, redisQrCode);
        return Result.ok(qrCode);
    }

    @ApiOperation("扫描二维码登录")
    @PostMapping("/scan")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "二维码生成的code值", paramType = "query", required = true)
    })
    public Result<String> scanQrCode(HttpServletRequest request) {
        return qrCodeOperator(request, SerConstant.ScanStatus.未扫描, SerConstant.ScanStatus.已扫描);
    }

    @ApiOperation("扫码确认登录")
    @PostMapping("/login")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "二维码生成的code值", paramType = "query", required = true),
            @ApiImplicitParam(name = SerConstant.QR_SECRET, value = "前一次扫码返回的密钥", paramType = "query", required = true)
    })
    public Result<String> qrCodeLogin(HttpServletRequest request) {
        return qrCodeOperator(request, SerConstant.ScanStatus.已扫描, SerConstant.ScanStatus.已确认);
    }

    @ApiOperation("扫码取消登录")
    @PostMapping("/cancel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "二维码生成的code值", paramType = "query", required = true),
            @ApiImplicitParam(name = SerConstant.QR_SECRET, value = "前一次扫码返回的密钥", paramType = "query", required = true)
    })
    public Result<String> qrCodeCancel(HttpServletRequest request) {
        return qrCodeOperator(request, SerConstant.ScanStatus.已扫描, SerConstant.ScanStatus.已取消);
    }

    /**
     * 扫码登录操作
     *
     * @param request
     * @param origStatus
     * @param destStatus
     * @return
     */
    private Result<String> qrCodeOperator(HttpServletRequest request, SerConstant.ScanStatus origStatus, SerConstant.ScanStatus destStatus) {
        CheckWithResult<WeChatToken> result = weChatTokenValidator.validate(request);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        String code = request.getParameter(SerConstant.QR_CODE);
        RedisQrCode redisQrCode = qrCodeService.checkQRCode(code);
        if (redisQrCode == null) {
            return Result.fail("错误:code不正确");
        }
        if (!StringUtils.isEmpty(redisQrCode.getAccessToken())
                && !result.getResult().getAccess_token().equals(redisQrCode.getAccessToken())) {
            return Result.fail("错误:两次请求token不相同");
        }
        if (!StringUtils.isEmpty(redisQrCode.getSecret())) {
            String secret = request.getParameter(SerConstant.QR_SECRET);
            if (!redisQrCode.getSecret().equals(secret)) {
                return Result.fail("错误:传入密钥不正确");
            }
        }
        if (origStatus.toString().equals(redisQrCode.getStatus())) {
            redisQrCode.setStatus(destStatus.toString());
            redisQrCode.setAccessToken(result.getResult().getAccess_token());
            redisQrCode.setAccount(result.getResult().getAccount());
            redisQrCode.setSecret(UUID.randomUUID().toString());
            qrCodeService.updateQRCode(redisQrCode);
            return Result.ok(redisQrCode.getSecret(), "操作成功");
        }
        return Result.fail("错误:二维码状态不正确");
    }
}

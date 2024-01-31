package cn.com.mfish.oauth.common;

import cn.com.mfish.common.core.utils.StringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

/**
 * @description: 二维码操作类
 * @author: mfish
 * @date: 2023/1/14 9:53
 */
@Slf4j
public class QRCodeUtils {
    private static final String CHARSET = "UTF-8";
    private static final int QRCODE_SIZE = 300;
    private static final int LOGO_WIDTH = 60;
    private static final int LOGO_HEIGHT = 60;

    /**
     * 创建二维码
     * @param content 二维码内容
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static BufferedImage createQRCode(String content) throws WriterException, IOException {
        return createQRCode(content, null, false);
    }

    /**
     * 创建带logo二维码(默认压缩logo)
     * @param content 二维码内容
     * @param imgPath logo路径
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static BufferedImage createQRCode(String content, String imgPath) throws WriterException, IOException {
        return createQRCode(content, imgPath, true);
    }

    /**
     * 创建带logo二维码
     * @param content 二维码内容
     * @param imgPath logo路径
     * @param needCompress 是否压缩logo
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static BufferedImage createQRCode(String content, String imgPath, boolean needCompress) throws WriterException, IOException {
        Hashtable<EncodeHintType,Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 2);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (StringUtils.isEmpty(imgPath)) {
            return image;
        }
        insertLogo(image, imgPath, needCompress);
        return image;

    }

    /**
     * 在生成的二维码中插入logo
     *
     * @param source
     * @param imgPath
     * @param needCompress
     * @throws Exception
     */
    private static void insertLogo(BufferedImage source, String imgPath, boolean needCompress) throws IOException {
        ClassPathResource cpr = new ClassPathResource(imgPath);
        if (!cpr.exists()) {
            log.error(imgPath + "logo文件不存在");
            return;
        }
        Image src = ImageIO.read(cpr.getInputStream());
        int height = src.getHeight(null);
        int width = src.getWidth(null);
        // 压缩LOGO
        if (needCompress) {
            if (height > LOGO_HEIGHT) {
                height = LOGO_HEIGHT;
            }
            if (width > LOGO_WIDTH) {
                width = LOGO_WIDTH;
            }
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Image image = src.getScaledInstance(width, height, Image.SCALE_FAST);
            Graphics g = tag.getGraphics();
            // 绘制缩小后的图
            g.drawImage(image, 0, 0, null);
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 180, 180);
        graph.setStroke(new BasicStroke(5f));
        graph.draw(shape);
        graph.dispose();
    }

}

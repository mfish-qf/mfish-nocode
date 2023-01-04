package cn.com.mfish.gateway.config;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.security.SecureRandom;

/**
 * @author: mfish
 * @description：算数验证码创建
 * @date: 2021/12/21 17:04
 */
public class MathCaptchaCreate extends DefaultTextCreator {
    private static final String[] NUMBERS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    @Override
    public String getText() {
        SecureRandom random = new SecureRandom();
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        StringBuilder sb;
        int seed = (int) Math.round(Math.random() * 2);
        Integer result = 0;
        if (seed == 0) {
            sb = createFormula(NUMBERS[x], "*", NUMBERS[y]);
            result = x * y;
        } else if (seed == 1) {
            if (!(x == 0) && y % x == 0) {
                sb = createFormula(NUMBERS[y], "/", NUMBERS[x]);
                result = y / x;
            } else {
                sb = createFormula(NUMBERS[x], "+", NUMBERS[y]);
                result = x + y;
            }
        } else if (seed == 2) {
            if (x >= y) {
                sb = createFormula(NUMBERS[x], "-", NUMBERS[y]);
                result = x - y;
            } else {
                sb = createFormula(NUMBERS[y], "-", NUMBERS[x]);
                result = y - x;
            }
        } else {
            sb = createFormula(NUMBERS[x], "+", NUMBERS[y]);
            result = x + y;
        }
        sb.append("=?#" + result);
        return sb.toString();
    }

    private StringBuilder createFormula(String x, String op, String y) {
        return new StringBuilder().append(x).append(op).append(y);
    }

    public static void main(String[] args) {
        MathCaptchaCreate create = new MathCaptchaCreate();
        for (int i = 0; i < 1000; i++) {
            System.out.println(create.getText());
        }
    }
}

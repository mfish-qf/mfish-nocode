package cn.com.mfish.common.core.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: mfish
 * @date: 2021/8/12 11:32
 */
public class CaptchaException extends RuntimeException {
    public enum Info {
        NULL("null", "错误:验证码不能为空"),
        TIMEOUT("timeout", "错误:验证码已失效"),
        ERROR("error", "错误:验证码不正确");

        private final String name;
        private final String value;
        private static final Map<String, Info> map = new HashMap<>();

        static {
            for (Info info : Info.values()) {
                map.put(info.name, info);
            }
        }

        Info(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static Info getExceptionInfo(String name) {
            if (map.containsKey(name)) {
                return map.get(name);
            }
            return Info.ERROR;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public CaptchaException(String msg) {
        super(msg);
    }

    public CaptchaException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaptchaException(Throwable cause) {
        super(cause);
    }
}

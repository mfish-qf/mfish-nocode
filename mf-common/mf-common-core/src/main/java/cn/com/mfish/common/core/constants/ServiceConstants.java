package cn.com.mfish.common.core.constants;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: mfish
 * @description: RPC服务常量
 * @date: 2021/12/1 17:14
 */
public class ServiceConstants {
    //单实例类型服务
    public static final String SERVER_BOOT = "boot";
    // 编译期常量，供@FeignClient等注解使用
    public static final String OAUTH_SERVICE = "mf-oauth";
    public static final String SYS_SERVICE = "mf-sys";
    public static final String SCHEDULER_SERVICE = "mf-scheduler";
    public static final String STORAGE_SERVICE = "mf-storage";
    public static final String WORKFLOW_SERVICE = "mf-workflow";
    public static final String NOCODE_SERVICE = "mf-nocode";
    public static final String DEMO_SERVICE = "mf-demo";
    public static final String AI_SERVICE = "mf-ai";

    /**
     * 判断是否为单体服务类型
     *
     * @param type 服务类型
     * @return 是否为单体服务
     */
    public static boolean isBoot(String type) {
        return ServiceConstants.SERVER_BOOT.equals(type);
    }

    /**
     * 微服务枚举，引用ServiceConstants中的编译期常量，保持单一定义源
     * gatewayPrefix: 网关路由前缀（如/sys），用于将网关路径转换为服务实际路径
     */
    public enum MfService {
        OAUTH(OAUTH_SERVICE, "/oauth2"),
        SYS(SYS_SERVICE, "/sys"),
        SCHEDULER(SCHEDULER_SERVICE, "/scheduler"),
        STORAGE(STORAGE_SERVICE, "/storage"),
        WORKFLOW(WORKFLOW_SERVICE, "/workflow"),
        NOCODE(NOCODE_SERVICE, "/nocode"),
        DEMO(DEMO_SERVICE, "/demo"),
        AI(AI_SERVICE, "/ai");

        private final String value;
        private final String gatewayPrefix;

        MfService(String value, String gatewayPrefix) {
            this.value = value;
            this.gatewayPrefix = gatewayPrefix;
        }

        public String getValue() {
            return value;
        }

        public String getGatewayPrefix() {
            return gatewayPrefix;
        }

        /**
         * 获取所有微服务ID集合
         */
        public static Set<String> allServiceIds() {
            return Arrays.stream(values()).map(MfService::getValue).collect(Collectors.toUnmodifiableSet());
        }

        /**
         * 判断服务ID是否有效
         */
        public static boolean isValid(String serviceId) {
            return Arrays.stream(values()).anyMatch(s -> s.value.equals(serviceId));
        }

        /**
         * 根据服务ID获取枚举，无效时返回null
         */
        public static MfService fromValue(String serviceId) {
            return Arrays.stream(values()).filter(s -> s.value.equals(serviceId)).findFirst().orElse(null);
        }

        /**
         * 将网关路径转换为服务实际路径
         * 例如：/sys/ai/assist → /ai/assist（去掉/sys前缀）
         *
         * @param gatewayPath 网关路径（如/sys/ai/assist）
         * @return 服务路径（如/ai/assist），如果路径不以该服务前缀开头则原样返回
         */
        public String toServicePath(String gatewayPath) {
            if (gatewayPath != null && gatewayPath.startsWith(gatewayPrefix)) {
                return gatewayPath.substring(gatewayPrefix.length());
            }
            return gatewayPath;
        }
    }
}

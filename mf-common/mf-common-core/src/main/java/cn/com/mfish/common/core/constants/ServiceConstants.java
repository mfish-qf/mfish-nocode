package cn.com.mfish.common.core.constants;

/**
 * @author: mfish
 * @description: RPC服务常量
 * @date: 2021/12/1 17:14
 */
public class ServiceConstants {
    //单实例类型服务
    public static final String SERVER_BOOT = "boot";
    public static final String OAUTH_SERVICE = "mf-oauth";
    public static final String SYS_SERVICE = "mf-sys";
    public static final String SCHEDULER_SERVICE = "mf-scheduler";
    public static final String STORAGE_SERVICE = "mf-storage";
    public static final String WORKFLOW_SERVICE = "mf-workflow";
    public static final String NOCODE_SERVICE = "mf-nocode";

    public static boolean isBoot(String type) {
        return ServiceConstants.SERVER_BOOT.equals(type);
    }
}

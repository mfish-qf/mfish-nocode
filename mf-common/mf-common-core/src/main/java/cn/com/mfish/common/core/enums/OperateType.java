package cn.com.mfish.common.core.enums;

/**
 * @author: mfish
 * @description: 操作类型
 * @date: 2022/9/1 17:05
 */
public enum OperateType {
    //其他操作
    OTHER("其他操作"),
    //查询
    QUERY("查询"),
    //新增
    INSERT("新增"),
    //更新
    UPDATE("修改"),
    //删除
    DELETE("删除"),
    //授权
    GRANT("授权"),
    //导入
    IMPORT("导入"),
    //导出
    EXPORT("导出"),
    //登录
    LOGIN("登录"),
    //登出
    LOGOUT("登出");
    private final String value;

    OperateType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

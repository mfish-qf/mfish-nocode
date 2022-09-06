package cn.com.mfish.common.core.enums;

/**
 * @author ：qiufeng
 * @description：操作类型
 * @date ：2022/9/1 17:05
 */
public enum OperateType {
    //其他操作
    OTHER(0),
    //查询
    QUERY(1),
    //新增
    INSERT(2),
    //更新
    UPDATE(3),
    //删除
    DELETE(4),
    //授权
    GRANT(5),
    //导入
    IMPORT(6),
    //导出
    EXPORT(7);
    private Integer value;
    OperateType(Integer value){
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}

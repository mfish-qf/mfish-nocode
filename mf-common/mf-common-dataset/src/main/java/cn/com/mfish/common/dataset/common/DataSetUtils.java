package cn.com.mfish.common.dataset.common;

import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.dataset.datatable.MetaDataHeader;

import java.util.Set;

/**
 * @description: 数据集通用处理
 * @author: mfish
 * @date: 2023/5/9 20:47
 */
public class DataSetUtils {
    /**
     * 从列头信息中选择列名 当列名
     *
     * @param col 列
     * @return
     */
    public static String chooseColName(MetaDataHeader col) {
        //表达式为空时 返回别名
        if (StringUtils.isEmpty(col.getExpression())) {
            if (StringUtils.isEmpty(col.getColName())) {
                col.setColName(col.getFieldName());
            }
            return col.getColName();
        }
        //表达式不为空 且别名=字段名（别名未修改） 返回表达式
        if (col.getColName().equals(col.getFieldName())) {
            return col.getExpression();
        }
        //别名已修改 返回别名
        return col.getColName();
    }


    /**
     * 判断是否存在重复列名并自动递增计算补充后缀
     *
     * @param i       从0开始循环
     * @param colName 列名称
     * @param set     从外将别名加入set
     * @return
     */
    public static String calcColName(int i, String colName, Set<String> set) {
        while (true) {
            if (!set.contains(colName)) {
                return colName;
            }
            if (colName.endsWith("_" + i)) {
                colName = colName.substring(0, colName.length() - (i + "").length() - 1);
            }
            return calcColName(++i, colName + "_" + i, set);
        }
    }
}

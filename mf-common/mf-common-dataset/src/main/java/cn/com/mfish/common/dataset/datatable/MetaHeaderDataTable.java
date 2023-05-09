package cn.com.mfish.common.dataset.datatable;

import cn.com.mfish.common.core.web.PageResult;
import lombok.Data;

/**
 * @description: 带头信息元数据表
 * @author: mfish
 * @date: 2023/4/5 0:01
 */
@Data
public class MetaHeaderDataTable {
    /**
     * 表格
     */
    private PageResult<MetaDataRow> table;
    /**
     * 头信息
     */
    private MetaDataHeaders header;

    public MetaHeaderDataTable(MetaDataTable metaDataTable) {
        this.table = new PageResult<>(metaDataTable);
        this.header = metaDataTable.getColHeaders();
    }
}

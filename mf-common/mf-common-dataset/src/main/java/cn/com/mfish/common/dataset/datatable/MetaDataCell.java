package cn.com.mfish.common.dataset.datatable;

import cn.com.mfish.common.core.utils.DataUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * @description: 数据单元
 * @author: mfish
 * @date: 2023/12/22
 */
@Data
public class MetaDataCell implements Comparable<MetaDataCell> {
    private MetaDataHeader header;
    private Object value;

    public MetaDataCell(MetaDataHeader header, Object value) {
        this.header = header;
        this.value = value;
    }

    @Override
    public int compareTo(@NotNull MetaDataCell cell) {
        switch (cell.getHeader().getDataType()) {
            case NUMBER:
                return DataUtils.numCompare(this.getValue(), cell.getValue());
            case DATE:
                return DataUtils.dateCompare(this.getValue(), cell.getValue());
            case STRING:
                return DataUtils.strCompare(this.getValue(), cell.getValue());
        }
        return 0;
    }
}

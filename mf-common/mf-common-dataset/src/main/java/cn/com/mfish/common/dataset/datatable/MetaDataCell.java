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
        return switch (cell.getHeader().getDataType()) {
            case NUMBER -> DataUtils.numCompare(this.getValue(), cell.getValue());
            case DATE -> DataUtils.dateCompare(this.getValue(), cell.getValue());
            case STRING -> DataUtils.strCompare(this.getValue(), cell.getValue());
            default -> 0;
        };
    }
}

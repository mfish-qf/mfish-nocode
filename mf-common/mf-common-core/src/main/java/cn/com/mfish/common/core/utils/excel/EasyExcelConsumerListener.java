package cn.com.mfish.common.core.utils.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @description: easyExcel回调处理
 * @author: mfish
 * @date: 2023/5/12 19:41
 */
public class EasyExcelConsumerListener<T> extends AnalysisEventListener<T> {

    private int pageSize;
    private List<T> list;
    private Consumer<List<T>> consumer;

    public EasyExcelConsumerListener(int pageSize, Consumer<List<T>> consumer) {
        this.pageSize = pageSize;
        this.consumer = consumer;
        list = new ArrayList<>(pageSize);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        list.add(data);
        if (list.size() >= pageSize) {
            consumer.accept(list);
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        consumer.accept(list);
    }
}

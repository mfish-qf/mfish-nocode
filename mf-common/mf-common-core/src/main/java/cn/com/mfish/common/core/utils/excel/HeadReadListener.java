package cn.com.mfish.common.core.utils.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @description: excel头读取监听
 * @author: mfish
 * @date: 2023/12/18
 */
public class HeadReadListener extends AnalysisEventListener<Map<Integer, String>> {
    private final Consumer<Map<Integer, String>> consumer;

    public HeadReadListener(Consumer<Map<Integer, String>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        consumer.accept(headMap);
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return false;
    }

    @Override
    public void invoke(Map<Integer, String> t, AnalysisContext analysisContext) {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}

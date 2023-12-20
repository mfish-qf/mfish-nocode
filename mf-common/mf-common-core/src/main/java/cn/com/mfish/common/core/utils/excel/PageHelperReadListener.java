package cn.com.mfish.common.core.utils.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;

/**
 * @description: easyExcel回调处理
 * @author: mfish
 * @date: 2023/5/12 19:41
 */
@Slf4j
public class PageHelperReadListener extends AnalysisEventListener<Map<Integer, String>> {

    private final int pageSize;
    private final Map<Integer, String> headMap = new HashMap<>();
    private final List<Map<String, String>> dataList = new ArrayList<>();
    private final Consumer<List<Map<String, String>>> consumer;

    @Getter
    private Integer total = 0;

    public PageHelperReadListener(Consumer<List<Map<String, String>>> consumer) {
        this(Integer.MAX_VALUE, consumer);
    }

    public PageHelperReadListener(int pageSize, Consumer<List<Map<String, String>>> consumer) {
        this.pageSize = pageSize;
        this.consumer = consumer;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Integer key : data.keySet()) {
            map.put(headMap.get(key), data.get(key));
        }
        dataList.add(map);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        //只读取第一行作为行头
        if (!this.headMap.isEmpty()) return;
        this.headMap.putAll(headMap);
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        //超过页数直接返回
        if (dataList.size() >= pageSize) {
            readEnd(context);
            return false;
        }
        return true;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        readEnd(context);
    }

    private void readEnd(AnalysisContext context) {
        consumer.accept(dataList);
        dataList.clear();
        //easyExcel获取total不准确，CSV获取为空
        total = context.readSheetHolder().getApproximateTotalRowNumber();
        total = total == null ? 0 : total;
        if (total > 0) {
            //去除行头记录数
            total--;
        }
    }
}

package cn.com.mfish.common.core.utils.excel;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.metadata.CellExtra;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @description: 模板读取(有个很重要的点 DemoDataListener 不能被spring管理 ， 要每次读取excel都要new, 然后里面用到spring可以构造方法传进去)
 * @author: mfish
 * @date: 2023/12/18
 */
@Slf4j
public class UploadDataListener<T> implements ReadListener<T> {
    /**
     * 每隔10条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private int BATCH_COUNT = 10;

    private final List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private final UploadDAO<T> uploadDAO;
    /**
     * 额外属性
     */
    private final ExtraProp extraProp = new ExtraProp();
    /**
     * 列头 如果设置了列头，会校验列头是否正确
     */
    private final Class<T> head;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param uploadDAO 上传操作类
     */
    public UploadDataListener(UploadDAO<T> uploadDAO) {
        this(uploadDAO, 10);
    }

    /**
     * 构造一个 UploadDataListener 实例。
     * <p>
     * 此构造函数主要用于使用必要的 UploadDAO 实例和批量处理计数初始化 UploadDataListener。
     * UploadDAO 实例用于与指定数据类型的访问层进行交互。
     * 批量处理计数用于确定批量处理操作中每次处理的数据项数量。
     *
     * @param uploadDAO  用于数据操作的 UploadDAO 实例，特定于数据类型 T。
     * @param batchCount 批量处理计数，一个正整数，表示批量大小。
     */
    public UploadDataListener(UploadDAO<T> uploadDAO, int batchCount) {
        this(uploadDAO, batchCount, null);
    }

    public UploadDataListener(UploadDAO<T> uploadDAO, int batchCount, Class<T> head) {
        this.uploadDAO = uploadDAO;
        this.BATCH_COUNT = batchCount;
        this.head = head;
    }


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data            数据
     * @param analysisContext 分析上下文
     */
    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList.clear();
        }
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        //列头默认都是字符串，如果出现其他类型列头暂时不支持
        for (ReadCellData<?> value : headMap.values()) {
            extraProp.getHeadSet().add(value.getStringValue());
        }
        verifyHead();
    }

    /**
     * 如果设置了列头类型，需要校验列头
     * 如果不需要检验可以不设置
     */
    private void verifyHead() {
        if (head == null) {
            return;
        }
        // 校验列头
        Field[] fields = head.getDeclaredFields();
        List<String> objectHeads = new ArrayList<>();
        for (Field field : fields) {
            ExcelProperty property = field.getAnnotation(ExcelProperty.class);
            ExcelIgnore ignore = field.getAnnotation(ExcelIgnore.class);
            //如果字段被忽略，不校验
            if (ignore != null) {
                continue;
            }
            if (null != property && property.value().length > 0) {
                objectHeads.add(property.value()[0]);
            }
        }
        for (String head : objectHeads) {
            if (!extraProp.getHeadSet().contains(head)) {
                throw new MyRuntimeException("错误：导入文件缺少【" + head + "】列头");
            }
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context 分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        extraProp.isEnd = true;
        saveData();
        log.info("所有数据解析完成！");
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        switch (extra.getType()) {
            case COMMENT:
                extraProp.setCommentExtra(extra);
                break;
            case HYPERLINK:
                extraProp.setLinkExtra(extra);
                break;
            case MERGE:
                extraProp.setMergeExtra(extra);
                break;
            default:
        }
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        uploadDAO.save(cachedDataList, extraProp);
        extraProp.batch++;
        log.info("存储数据库成功！");
    }

    @Data
    public static class ExtraProp {
        /**
         * 当前保存批次号
         */
        private int batch = 1;
        private boolean isEnd = false;
        /**
         * 设置列头用于后续校验列头
         */
        private final Set<String> headSet = new HashSet<>();
        /**
         * todo 注意注意
         * 使用extra属性时，batchCount要设置到Integer.MAX_VALUE
         * 因为easyexcel extra方式是读取完所有数据后才会调用，需要保证excel全部读取完成，此时批量入库失效
         */
        private CellExtra mergeExtra;
        private CellExtra linkExtra;
        private CellExtra commentExtra;
    }
}

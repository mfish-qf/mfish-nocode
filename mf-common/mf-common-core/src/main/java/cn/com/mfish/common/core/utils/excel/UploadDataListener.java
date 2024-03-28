package cn.com.mfish.common.core.utils.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @description: 模板读取(有个很重要的点 DemoDataListener 不能被spring管理 ， 要每次读取excel都要new, 然后里面用到spring可以构造方法传进去)
 * @author: mfish
 * @date: 2023/12/18
 */
@Slf4j
public class UploadDataListener<T> implements ReadListener<T> {
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private int BATCH_COUNT = 10;
    private final List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private final UploadDAO<T> uploadDAO;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param uploadDAO 上传操作类
     */
    public UploadDataListener(UploadDAO<T> uploadDAO) {
        this.uploadDAO = uploadDAO;
    }

    public UploadDataListener(UploadDAO<T> uploadDAO, int batchCount) {
        this.uploadDAO = uploadDAO;
        this.BATCH_COUNT = batchCount;
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

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context 分析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        uploadDAO.save(cachedDataList);
        log.info("存储数据库成功！");
    }
}

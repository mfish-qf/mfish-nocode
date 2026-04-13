package cn.com.mfish.demo.service;

import cn.com.mfish.demo.entity.DemoImportExport;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description: 导入导出 Demo
 * @author: mfish
 * @date: 2024-09-02
 * @version: V2.3.1
 */
public interface DemoImportExportService extends IService<DemoImportExport> {
    /**
     * 批量插入数据（部分字段）
     *
     * @param list 待插入的数据列表
     * @return 插入的记录数
     */
    int insertBatchSomeColumn(List<DemoImportExport> list);
}

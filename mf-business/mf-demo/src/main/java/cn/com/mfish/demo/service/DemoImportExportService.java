package cn.com.mfish.demo.service;

import cn.com.mfish.demo.entity.DemoImportExport;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @description: 导入导出Demo
 * @author: mfish
 * @date: 2024-09-02
 * @version: V1.3.2
 */
public interface DemoImportExportService extends IService<DemoImportExport> {
    int insertBatchSomeColumn(List<DemoImportExport> list);
}

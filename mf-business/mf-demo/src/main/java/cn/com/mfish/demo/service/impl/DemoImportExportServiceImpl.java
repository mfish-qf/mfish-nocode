package cn.com.mfish.demo.service.impl;

import cn.com.mfish.demo.entity.DemoImportExport;
import cn.com.mfish.demo.mapper.DemoImportExportMapper;
import cn.com.mfish.demo.service.DemoImportExportService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @description: 导入导出Demo
 * @author: mfish
 * @date: 2024-09-02
 * @version: V2.1.1
 */
@Service
public class DemoImportExportServiceImpl extends ServiceImpl<DemoImportExportMapper, DemoImportExport> implements DemoImportExportService {

    @Override
    public int insertBatchSomeColumn(List<DemoImportExport> list) {
        return baseMapper.insertBatchSomeColumn(list);
    }
}

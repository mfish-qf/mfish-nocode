package cn.com.mfish.code.service.impl;

import cn.com.mfish.code.entity.FieldInfo;
import cn.com.mfish.code.entity.TableInfo;
import cn.com.mfish.code.mapper.MysqlTableMapper;
import cn.com.mfish.code.service.MysqlTableService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: mfish
 * @description：表信息
 * @date: 2022/8/31 23:05
 */
@Service
public class MysqlTableServiceImpl implements MysqlTableService {
    @Resource
    MysqlTableMapper mysqlTableMapper;

    @Override
    public List<FieldInfo> getColumns(String schema, String tableName) {
        List<FieldInfo> list = mysqlTableMapper.getColumns(schema, tableName);

        return list;
    }

    @Override
    public TableInfo getTableInfo(String schema, String tableName) {
        return mysqlTableMapper.getTableInfo(schema, tableName);
    }
}

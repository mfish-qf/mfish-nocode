package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.manger.PoolManager;
import cn.com.mfish.common.dblink.query.QueryHandler;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.mapper.DbConnectMapper;
import cn.com.mfish.sys.service.DbConnectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V1.2.0
 */
@Service
@RefreshScope
public class DbConnectServiceImpl extends ServiceImpl<DbConnectMapper, DbConnect> implements DbConnectService {
    @Value("${DBConnect.password.privateKey}")
    private String privateKey;

    @Override
    public Result<DbConnect> insertConnect(DbConnect dbConnect) {
        Result<Boolean> result = testConnect(dbConnect);
        if (!result.isSuccess()) {
            return Result.fail(dbConnect, "错误:尝试连接失败!");
        }
        if (save(dbConnect)) {
            return Result.ok(dbConnect, "数据库连接-添加成功!");
        }
        return Result.fail(dbConnect, "错误:数据库连接-添加失败!");
    }

    @Override
    public Result<DbConnect> updateConnect(DbConnect dbConnect) {
        Result<Boolean> result = testConnect(dbConnect);
        if (!result.isSuccess()) {
            return Result.fail(dbConnect, "错误:尝试连接失败!");
        }
        if (updateById(dbConnect)) {
            return Result.ok(dbConnect, "数据库连接-编辑成功!");
        }
        return Result.fail(dbConnect, "错误:数据库连接-编辑失败!");
    }

    @Override
    public Result<Boolean> testConnect(DbConnect dbConnect) {
        DataSourceOptions<?> dataSourceOptions = QueryHandler.buildDataSourceOptions(dbConnect, privateKey);
        //测试连接不使用连接池
        dataSourceOptions.setPoolType(PoolType.NoPool);
        try {
            Connection conn = PoolManager.getConnection(dataSourceOptions, 3000);
            return Result.ok(!conn.isClosed(), "连接成功");
        } catch (SQLException e) {
            log.error("错误:测试连接异常", e);
            return Result.fail(false, "错误:连接失败");
        } finally {
            PoolManager.release();
        }
    }

    /**
     * 通过ID查询数据库配置
     *
     * @param id
     * @return
     */
    public Result<DbConnect> queryById(String id) {
        DbConnect dbConnect = baseMapper.selectById(id);
        if (dbConnect == null) {
            return Result.fail(null, "错误:未获取到数据连接");
        }
        return Result.ok(dbConnect, "数据库连接-查询成功!");
    }

}

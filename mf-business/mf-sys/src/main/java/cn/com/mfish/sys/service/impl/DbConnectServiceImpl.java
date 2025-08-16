package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.manger.PoolManager;
import cn.com.mfish.common.dblink.query.QueryHandler;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import cn.com.mfish.sys.mapper.DbConnectMapper;
import cn.com.mfish.common.dblink.service.DbConnectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V2.1.0
 */
@Service
@RefreshScope
public class DbConnectServiceImpl extends ServiceImpl<DbConnectMapper, DbConnect> implements DbConnectService {
    @Value("${DBConnect.password.privateKey}")
    private String privateKey;

    @Override
    public Result<PageResult<DbConnect>> queryPageList(ReqDbConnect reqDbConnect, ReqPage reqPage) {
        PageHelper.startPage(reqPage.getPageNum(), reqPage.getPageSize());
        List<DbConnect> list = baseMapper.selectList(new LambdaQueryWrapper<DbConnect>()
                .like(reqDbConnect.getDbTitle() != null, DbConnect::getDbTitle, reqDbConnect.getDbTitle())
                .eq(reqDbConnect.getDbType() != null, DbConnect::getDbType, reqDbConnect.getDbType())
                .like(reqDbConnect.getDbName() != null, DbConnect::getDbName, reqDbConnect.getDbName())
                .like(reqDbConnect.getHost() != null, DbConnect::getHost, reqDbConnect.getHost())
                .orderByDesc(DbConnect::getCreateTime));
        //密码不返回查询界面
        for (DbConnect connect : list) {
            connect.setPassword(null);
        }
        return Result.ok(new PageResult<>(list), "数据库连接-查询成功!");
    }

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
     * @param id id
     * @return 返回数据
     */
    public Result<DbConnect> queryById(String id) {
        DbConnect dbConnect = baseMapper.selectById(id);
        if (dbConnect == null) {
            return Result.fail(null, "错误:未获取到数据连接");
        }
        return Result.ok(dbConnect, "数据库连接-查询成功!");
    }

}

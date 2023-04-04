package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.secret.SM2Utils;
import cn.com.mfish.common.core.utils.StringUtils;
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
 * @version: V1.0.0
 */
@Service
@RefreshScope
public class DbConnectServiceImpl extends ServiceImpl<DbConnectMapper, DbConnect> implements DbConnectService {
    @Value("${DBConnect.password.privateKey}")
    private String privateKey;

    @Override
    public Result<Boolean> testConnect(DbConnect dbConnect) {
        String pwd = SM2Utils.decrypt(privateKey, dbConnect.getPassword());
        if (StringUtils.isEmpty(pwd)) {
            return Result.fail(false, "错误:密码解密失败");
        }
        DataSourceOptions dataSourceOptions = QueryHandler.buildDataSourceOptions(dbConnect);
        //测试连接不使用连接池
        dataSourceOptions.setPoolType(PoolType.NoPool);
        try {
            Connection conn = PoolManager.getConnection(dataSourceOptions, 3000);
            return Result.ok(!conn.isClosed(), "连接成功");
        } catch (SQLException e) {
            log.error("错误:测试连接异常", e);
            return Result.fail(false, "连接失败");
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
        String pwd = SM2Utils.decrypt(privateKey, dbConnect.getPassword());
        if (StringUtils.isEmpty(pwd)) {
            return Result.fail(dbConnect, "错误:密码解密失败");
        }
        dbConnect.setPassword(pwd);
        return Result.ok(dbConnect, "数据库连接-查询成功!");
    }
}

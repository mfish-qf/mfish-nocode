package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.secret.SM2Utils;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.dblink.entity.DataSourceOptions;
import cn.com.mfish.common.dblink.enums.DBType;
import cn.com.mfish.common.dblink.enums.PoolType;
import cn.com.mfish.common.dblink.manger.PoolManager;
import cn.com.mfish.sys.entity.DbConnect;
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
            return Result.fail(false, "密码不正确");
        }
        DBType dbType = DBType.getType(dbConnect.getDbType());
        DataSourceOptions dataSourceOptions = new DataSourceOptions().setDbType(dbType)
                .setPoolType(PoolType.NoPool)
                .setUser(dbConnect.getUsername())
                .setPassword(pwd)
                .setJdbcUrl(dbType.getJdbcUrl(dbConnect.getHost(), dbConnect.getPort(), dbConnect.getDbName()));
        try (Connection conn = PoolManager.getConnection(dataSourceOptions, 3000)) {
            return Result.ok(!conn.isClosed(), "连接成功");
        } catch (SQLException e) {
            return Result.fail(false, "连接失败");
        }
    }
}

package cn.com.mfish.common.dblink.service;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description: 数据库连接
 * @author: mfish
 * @date: 2023-03-13
 * @version: V2.0.0
 */
public interface DbConnectService extends IService<DbConnect> {
    Result<PageResult<DbConnect>> queryPageList(ReqDbConnect reqDbConnect, ReqPage reqPage);

    Result<DbConnect> insertConnect(DbConnect dbConnect);

    Result<DbConnect> updateConnect(DbConnect dbConnect);

    Result<Boolean> testConnect(DbConnect dbConnect);

    Result<DbConnect> queryById(String id);
}

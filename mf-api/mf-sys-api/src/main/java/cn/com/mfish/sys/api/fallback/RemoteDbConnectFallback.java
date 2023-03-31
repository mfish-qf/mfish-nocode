package cn.com.mfish.sys.api.fallback;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;
import cn.com.mfish.sys.api.remote.RemoteDbConnectService;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 远程数据连接失败处理
 * @author: mfish
 * @date: 2023/3/23 21:39
 */
@Slf4j
@Component
public class RemoteDbConnectFallback implements FallbackFactory<RemoteDbConnectService> {
    @Override
    public RemoteDbConnectService create(Throwable cause) {
        log.error("错误:数据库连接调用异常", cause);
        return new RemoteDbConnectService() {
            @Override
            public Result<PageResult<DbConnect>> queryPageList(ReqDbConnect reqDbConnect, ReqPage reqPage) {
                return Result.fail("错误:查询数据库连接列表出错");
            }

            @Override
            public Result<DbConnect> queryById(String origin, String id) {
                return Result.fail("错误:查询数据库连接信息出错");
            }

            @Override
            public Result<List<TableInfo>> getTableList(String connectId, String tableName) {
                return Result.fail("错误:查询数据库表列表出错");
            }

            @Override
            public Result<List<FieldInfo>> getFieldList(String connectId, String tableName) {
                return Result.fail("错误:查询表字段列表出错");
            }
        };
    }
}

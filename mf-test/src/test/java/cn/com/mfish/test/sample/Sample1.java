package cn.com.mfish.test.sample;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.oauth.api.entity.SsoOrg;
import cn.com.mfish.common.oauth.api.remote.RemoteOrgService;
import cn.com.mfish.common.redis.common.IDBuild;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.api.remote.RemoteDbConnectService;
import cn.com.mfish.sys.api.remote.RemoteDictService;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @description: 测试类
 * @author: mfish
 * @date: 2023/3/5 14:59
 */
@Slf4j
@SpringBootTest
public class Sample1 {
    @Resource
    RemoteDbConnectService remoteDbConnectService;
    @Resource
    RemoteDictService remoteDictService;
    @Resource
    RemoteOrgService remoteOrgService;

    @Test
    public void testIDBuild() {
        for (int i = 0; i < 2000; i++) {
            CompletableFuture.runAsync(() -> System.out.println(IDBuild.getID("File")));
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testController() {
        Result<PageResult<DbConnect>> result = remoteDbConnectService.queryPageList(new ReqDbConnect().setDbName("aaa"), new ReqPage());
        System.out.println(result);
    }

    @Test
    public void testDict() {
        Result<List<DictItem>> result = remoteDictService.queryList("sso_grant_type");
        System.out.println(result.getData());
    }

    @Test
    public void testOrg() {
        Result<List<SsoOrg>> result = remoteOrgService.queryByFixCode("mysyb", "all");
        Result<List<SsoOrg>> result1 = remoteOrgService.queryById("a453767fa3907e320f458f254a4549ae,bb94731770f981fae7eec5cbb1b32bb3");
        System.out.println(result.getData());
        System.out.println(result1.getData());
    }
}

package cn.com.mfish.test.sample;

import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.redis.common.IDBuild;
import cn.com.mfish.sys.api.entity.DbConnect;
import cn.com.mfish.sys.api.remote.RemoteDbConnectService;
import cn.com.mfish.sys.api.req.ReqDbConnect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @description: 测试类
 * @author: mfish
 * @date: 2023/3/5 14:59
 */
@Slf4j
@SpringBootTest
@ComponentScan(basePackages = "cn.com.mfish.test")
public class Sample1 {
    @Resource
    RemoteDbConnectService remoteDbConnectService;

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
}

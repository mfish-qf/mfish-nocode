package cn.com.mfish.test.sample;

import cn.com.mfish.common.redis.common.IDBuild;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

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
}

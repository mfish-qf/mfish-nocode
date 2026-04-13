package cn.com.mfish.oauth;

import cn.com.mfish.common.oauth.entity.SsoUser;
import cn.com.mfish.oauth.mapper.SsoUserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: mfish
 * @description: 测试数据库请求
 * @date: 2022/11/24 23:10
 */
@Slf4j
@SpringBootTest
@ComponentScan(basePackages = "cn.com.mfish.oauth")
public class DBTest {
    @Resource
    SsoUserMapper ssoUserMapper;

    @Test
    public void getUser() {
        SsoUser ssoUser = ssoUserMapper.getUserById("40062f1156ef42b9b3a341462c927fb6", null);
        System.out.println(ssoUser);
    }
}

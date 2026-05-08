package cn.com.mfish.consume.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * @description: 消费端测试任务
 * @author: mfish
 * @date: 2023/3/1 17:35
 */
@Component
@Slf4j
public class ConsumerJob {
    /**
     * 无参任务执行方法
     */
    public void test() {
        log.info("任务执行");
    }

    /**
     * 带字符串参数的任务执行方法
     *
     * @param param 任务参数
     */
    public void test(String param) {
        log.info(MessageFormat.format("任务执行!参数:{0}", param));
    }

    /**
     * 带字符串和整数参数的任务执行方法
     *
     * @param param1 字符串参数
     * @param param2 整数参数
     */
    public void test(String param1, Integer param2) {
        log.info(MessageFormat.format("任务执行!参数1:{0},参数:{1}", param1, param2));
    }

    /**
     * 带两个字符串参数的任务执行方法
     *
     * @param param1 第一个字符串参数
     * @param param2 第二个字符串参数
     */
    public void test(String param1, String param2) {
        log.info(MessageFormat.format("任务执行!参数1:{0},参数:{1}", param1, param2));
    }

}

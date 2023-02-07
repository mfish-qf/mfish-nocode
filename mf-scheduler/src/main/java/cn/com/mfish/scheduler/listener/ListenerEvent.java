package cn.com.mfish.scheduler.listener;

import lombok.extern.slf4j.Slf4j;

/**
 * @description: 监听事件接口
 * @author: mfish
 * @date: 2023/2/7 12:07
 */
@Slf4j
public class ListenerEvent<T> {

    public void save(T t) {
        log.info("----- ListenerEvent.save -----> {}", t.toString());
    }

    public void delete(String fireInstanceId) {
        log.info("----- ListenerEvent.delete -----> {}", fireInstanceId);
    }

}

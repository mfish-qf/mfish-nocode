package cn.com.mfish.sys.api.fallback;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.remote.RemoteDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @description: 远程字典失败处理
 * @author: mfish
 * @date: 2023/5/18 18:16
 */
@Slf4j
@Component
public class RemoteDictFallback implements FallbackFactory<RemoteDictService> {

    /**
     * 创建字典服务降级实例
     *
     * @param cause 导致降级的异常原因
     * @return 降级后的字典服务实例
     */
    @Override
    public RemoteDictService create(Throwable cause) {
        log.error("错误:字典调用异常", cause);
        return (origin, dictCode) -> Result.fail("错误:查询字典数据出错");
    }
}

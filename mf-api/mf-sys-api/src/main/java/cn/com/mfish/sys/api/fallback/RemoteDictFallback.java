package cn.com.mfish.sys.api.fallback;

import cn.com.mfish.common.core.utils.FeignFallbackHelper;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.Dict;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.api.remote.RemoteDictService;
import cn.com.mfish.sys.api.req.ReqDict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

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
        return new RemoteDictService() {
            @Override
            public Result<List<DictItem>> queryByCode(String origin, String dictCode) {
                // 从 FeignException 响应体中解析真实的业务错误消息（如"错误:该用户无此操作权限"）
                return Result.fail(FeignFallbackHelper.resolveErrorMsg(cause, "查询字典项失败"));
            }

            @Override
            public Result<List<Dict>> queryList(String origin, ReqDict reqDict) {
                return Result.fail(FeignFallbackHelper.resolveErrorMsg(cause, "查询字典列表失败"));
            }
        };
    }
}

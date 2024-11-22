package cn.com.mfish.sys.api.fallback;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.DictCategory;
import cn.com.mfish.sys.api.remote.RemoteDictCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 远程树形字典失败处理
 * @author: mfish
 * @date: 2024/11/19
 */
@Slf4j
@Component
public class RemoteDictCategoryFallback implements FallbackFactory<RemoteDictCategoryService> {
    @Override
    public RemoteDictCategoryService create(Throwable cause) {
        log.error("错误:树形字典调用异常", cause);
        return new RemoteDictCategoryService() {
            @Override
            public Result<List<DictCategory>> queryByIds(String origin, String ids) {
                return Result.fail("错误:查询树形字典列表出错");
            }

            @Override
            public Result<List<DictCategory>> queryTreeByCode(String origin, String code, String direction) {
                return Result.fail("错误:查询字典树出错");
            }

            @Override
            public Result<List<DictCategory>> queryListByCode(String origin, String code, String direction) {
                return Result.fail("错误:查询字典树列表出错");
            }

            @Override
            public Result<List<DictCategory>> queryTreeById(String origin, String id, String direction) {
                return Result.fail("错误:查询字典树出错");
            }

            @Override
            public Result<List<DictCategory>> queryListById(String origin, String id, String direction) {
                return Result.fail("错误:查询字典树列表出错");
            }
        };
    }
}

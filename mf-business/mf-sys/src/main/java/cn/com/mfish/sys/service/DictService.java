package cn.com.mfish.sys.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @version: V2.0.0
 */
public interface DictService extends IService<Dict> {
    Result<Dict> updateDict(Dict dict);

    /**
     * 字典编码是否存在
     *
     * @param id id
     * @param dictCode 字典编码
     * @return 返回是否存在
     */
    boolean isDictCodeExist(String id, String dictCode);

    /**
     * 删除字典
     *
     * @param id id
     * @return 返回删除结果
     */
    Result<Boolean> deleteDict(String id);
}

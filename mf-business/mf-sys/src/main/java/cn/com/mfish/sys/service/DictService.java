package cn.com.mfish.sys.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.2.0
 */
public interface DictService extends IService<Dict> {
    Result<Dict> updateDict(Dict dict);

    /**
     * 字典编码是否存在
     *
     * @param id
     * @param dictCode
     * @return
     */
    boolean isDictCodeExist(String id, String dictCode);

    /**
     * 删除字典
     *
     * @param id
     * @return
     */
    Result<Boolean> deleteDict(String id);
}

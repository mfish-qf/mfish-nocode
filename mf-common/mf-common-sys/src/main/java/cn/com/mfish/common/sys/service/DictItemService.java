package cn.com.mfish.common.sys.service;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.sys.req.ReqDictItem;
import cn.com.mfish.sys.api.entity.DictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 字典项
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.2.0
 */
public interface DictItemService extends IService<DictItem> {
    /**
     * 获取字典项
     *
     * @param reqDictItem
     * @return
     */
    List<DictItem> getDictItems(ReqDictItem reqDictItem);

    /**
     * 通过code删除字典项
     * @param dictCode
     * @return
     */
    boolean deleteDictItemsByCode(String dictCode);

    Result<List<DictItem>> queryByCode(String dictCode);
}

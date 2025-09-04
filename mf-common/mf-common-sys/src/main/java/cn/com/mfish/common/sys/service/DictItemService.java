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
 * @version: V2.1.1
 */
@SuppressWarnings("rawtypes")
public interface DictItemService extends IService<DictItem> {
    /**
     * 获取字典项
     *
     * @param reqDictItem 请求参数
     * @return 字典项列表
     */
    List<DictItem> getDictItems(ReqDictItem reqDictItem);

    /**
     * 通过code删除字典项
     *
     * @param dictCode 字典编码
     * @return 是否删除成功
     */
    boolean deleteDictItemsByCode(String dictCode);

    /**
     * 通过code查询字典项
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    Result<List<DictItem>> queryByCode(String dictCode);
}

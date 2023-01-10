package cn.com.mfish.sys.service;

import cn.com.mfish.sys.entity.DictItem;
import cn.com.mfish.sys.req.ReqDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 字典项
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.0.0
 */
public interface DictItemService extends IService<DictItem> {
    /**
     * 获取字典项
     *
     * @param reqDictItem
     * @return
     */
    List<DictItem> getDictItems(ReqDictItem reqDictItem);

}

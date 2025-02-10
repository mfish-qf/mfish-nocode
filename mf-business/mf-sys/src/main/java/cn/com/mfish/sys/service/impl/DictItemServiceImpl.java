package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.common.sys.req.ReqDictItem;
import cn.com.mfish.common.sys.service.DictItemService;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.cache.DictCache;
import cn.com.mfish.sys.mapper.DictItemMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * @Description: 字典项
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.3.2
 */
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {

    @Resource
    private DictCache dictCache;

    @Override
    public List<DictItem> getDictItems(ReqDictItem reqDictItem) {
        return baseMapper.selectList(buildCondition(reqDictItem));
    }

    @Override
    public boolean deleteDictItemsByCode(String dictCode) {
        return baseMapper.delete(new LambdaQueryWrapper<DictItem>().eq(DictItem::getDictCode, dictCode)) > 0;
    }

    private LambdaQueryWrapper buildCondition(ReqDictItem reqDictItem) {
        return new LambdaQueryWrapper<DictItem>()
                .eq(reqDictItem.getDictCode() != null, DictItem::getDictCode, reqDictItem.getDictCode())
                .like(reqDictItem.getDictLabel() != null, DictItem::getDictLabel, reqDictItem.getDictLabel())
                .like(reqDictItem.getDictValue() != null, DictItem::getDictValue, reqDictItem.getDictValue())
                .eq(reqDictItem.getStatus() != null, DictItem::getStatus, reqDictItem.getStatus())
                .eq(reqDictItem.getDictId() != null, DictItem::getDictId, reqDictItem.getDictId())
                .orderByAsc(DictItem::getDictSort);
    }

    @Override
    public Result<List<DictItem>> queryByCode(String dictCode) {
        List<DictItem> list = dictCache.getFromCacheAndDB(dictCode);
        if (list == null || list.isEmpty()) {
            return Result.fail("错误:未获取到字典信息");
        }
        return Result.ok(list, "字典项-查询成功!");
    }
}

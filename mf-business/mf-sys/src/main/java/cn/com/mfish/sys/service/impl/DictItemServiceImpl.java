package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.utils.StringUtils;
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
 * @version: V2.3.1
 */
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {

    @Resource
    private DictCache dictCache;

    /**
     * 查询字典项列表
     *
     * @param reqDictItem 查询条件
     * @return 字典项列表
     */
    @Override
    public List<DictItem> getDictItems(ReqDictItem reqDictItem) {
        return baseMapper.selectList(buildCondition(reqDictItem));
    }

    /**
     * 根据字典编码删除所有字典项
     *
     * @param dictCode 字典编码
     * @return 是否删除成功
     */
    @Override
    public boolean deleteDictItemsByCode(String dictCode) {
        return baseMapper.delete(new LambdaQueryWrapper<DictItem>().eq(DictItem::getDictCode, dictCode)) > 0;
    }

    /**
     * 构建字典项查询条件
     *
     * @param reqDictItem 查询参数
     * @return Lambda查询条件包装器
     */
    private LambdaQueryWrapper buildCondition(ReqDictItem reqDictItem) {
        return new LambdaQueryWrapper<DictItem>()
                .eq(StringUtils.isNotEmpty(reqDictItem.getDictCode()), DictItem::getDictCode, reqDictItem.getDictCode())
                .like(StringUtils.isNotEmpty(reqDictItem.getDictLabel()), DictItem::getDictLabel, reqDictItem.getDictLabel())
                .like(StringUtils.isNotEmpty(reqDictItem.getDictValue()), DictItem::getDictValue, reqDictItem.getDictValue())
                .eq(reqDictItem.getStatus() != null, DictItem::getStatus, reqDictItem.getStatus())
                .eq(StringUtils.isNotEmpty(reqDictItem.getDictId()), DictItem::getDictId, reqDictItem.getDictId())
                .orderByAsc(DictItem::getDictSort);
    }

    /**
     * 根据字典编码查询字典项（优先从缓存获取）
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    @Override
    public Result<List<DictItem>> queryByCode(String dictCode) {
        List<DictItem> list = dictCache.getFromCacheAndDB(dictCode);
        if (list == null || list.isEmpty()) {
            return Result.fail("错误:未获取到字典项信息");
        }
        return Result.ok(list, "字典项-查询成功!");
    }
}

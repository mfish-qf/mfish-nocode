package cn.com.mfish.sys.cache;

import cn.com.mfish.common.redis.common.RedisPrefix;
import cn.com.mfish.common.redis.temp.BaseTempCache;
import cn.com.mfish.sys.api.entity.DictItem;
import cn.com.mfish.sys.mapper.DictItemMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * @description: 获取字典
 * @author: mfish
 * @date: 2023/3/6 16:00
 */
@Component
@Slf4j
public class DictCache extends BaseTempCache<List<DictItem>> {
    @Resource
    DictItemMapper dictItemMapper;

    @Override
    protected String buildKey(String... key) {
        return RedisPrefix.buildCode2DictItemKey(key[0]);
    }

    @Override
    protected List<DictItem> getFromDB(String... key) {
        String dictCode = key[0];
        List<DictItem> list = dictItemMapper.selectList(new LambdaQueryWrapper<DictItem>()
                .eq(DictItem::getDictCode, dictCode).eq(DictItem::getStatus, 0)
                .orderByAsc(DictItem::getDictSort));
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (DictItem item : list) {
            if (item.getValueType() == null) {
                continue;
            }
            try {
                switch (item.getValueType()) {
                    case 1:
                        item.setDictValue(Integer.parseInt(item.getDictValue().toString()));
                        break;
                    case 2:
                        item.setDictValue(Boolean.parseBoolean(item.getDictValue().toString()));
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                log.error(MessageFormat.format("错误:字典[{0}]类型转换异常", item.getDictCode()));
            }
        }
        return list;
    }
}

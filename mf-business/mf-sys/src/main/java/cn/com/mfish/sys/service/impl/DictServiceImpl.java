package cn.com.mfish.sys.service.impl;

import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.entity.Dict;
import cn.com.mfish.sys.entity.DictItem;
import cn.com.mfish.sys.mapper.DictMapper;
import cn.com.mfish.sys.req.ReqDictItem;
import cn.com.mfish.sys.service.DictItemService;
import cn.com.mfish.sys.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @Version: V1.0.0
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Resource
    DictItemService dictItemService;

    @Override
    @Transactional
    public Result<Dict> updateDict(Dict dict) {
        List<DictItem> list = dictItemService.getDictItems(new ReqDictItem().setDictId(dict.getId()));
        if (list.size() > 0 && !list.get(0).getDictCode().equals(dict.getDictCode())) {
            for (DictItem item : list) {
                item.setDictCode(dict.getDictCode());
            }
            if (!dictItemService.updateBatchById(list)) {
                throw new MyRuntimeException("错误:修改字典项失败!");
            }
        }
        if (baseMapper.updateById(dict) > 0) {
            return Result.ok(dict, "字典-编辑成功!");
        }
        throw new MyRuntimeException("错误:编辑字典失败!");
    }

    @Override
    public boolean isDictCodeExist(String id, String dictCode) {
        return baseMapper.isDictCodeExist(id, dictCode) > 0;
    }
}

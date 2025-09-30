package cn.com.mfish.sys.mapper;

import cn.com.mfish.sys.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @version: V2.2.0
 */
public interface DictMapper extends BaseMapper<Dict> {
    Integer isDictCodeExist(@Param("id") String id, @Param("dictCode") String dictCode);
}

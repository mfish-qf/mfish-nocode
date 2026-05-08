package cn.com.mfish.sys.mapper;

import cn.com.mfish.sys.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 字典
 * @Author: mfish
 * @date: 2023-01-03
 * @version: V2.3.1
 */
public interface DictMapper extends BaseMapper<Dict> {
    /**
     * 判断字典编码是否已存在（排除指定ID）
     *
     * @param id       字典ID（排除自身）
     * @param dictCode 字典编码
     * @return 存在的记录数
     */
    Integer isDictCodeExist(@Param("id") String id, @Param("dictCode") String dictCode);
}
